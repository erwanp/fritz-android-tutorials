package ai.fritz.animatedSky;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Size;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import ai.fritz.core.Fritz;
import ai.fritz.fritzvisionskysegmentationmodel.SkySegmentationOnDeviceModel;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentPredictorOptions;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentResult;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentTFLPredictor;


public class MainActivity extends BaseCameraActivity implements ImageReader.OnImageAvailableListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String API_KEY = "bbe75c73f8b24e63bc05bf81ed9d2829";

    private AtomicBoolean shouldSample = new AtomicBoolean(true);
    private FritzVisionSegmentTFLPredictor predictor;
    private int imgRotation;

    private static final int DURATION = 5000;

    private ValueAnimator mCurrentAnimator;
    private Matrix mMatrix = new Matrix();
    private ImageView mImageView;
    private float mScaleFactor;
    private RectF mDisplayRect = new RectF();

    private FritzVisionSegmentResult segmentResult;
    private FritzVisionImage visionImage;


    Button snapshotButton;
    RelativeLayout previewLayout;
    RelativeLayout snapshotLayout;
    OverlayView snapshotOverlay;
    ProgressBar snapshotProcessingSpinner;
    Button closeButton;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fritz.configure(getApplicationContext(), API_KEY);

        SkySegmentationOnDeviceModel onDeviceModel = new SkySegmentationOnDeviceModel();
        FritzVisionSegmentPredictorOptions options = new FritzVisionSegmentPredictorOptions.Builder()
                .targetConfidenceThreshold(.6f)
                .build();
        predictor = FritzVision.ImageSegmentation.getPredictorTFL(onDeviceModel, options);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.camera_connection_fragment_background_replace;
    }

    @Override
    public void onPreviewSizeChosen(final Size size, final Size cameraSize, final int rotation) {
        imgRotation = FritzVisionOrientation.getImageRotationFromCamera(this, cameraId);

        snapshotButton = findViewById(R.id.take_picture_btn);
        previewLayout = findViewById(R.id.preview_frame);
        snapshotLayout = findViewById(R.id.snapshot_frame);
        snapshotOverlay = findViewById(R.id.snapshot_view);
        closeButton = findViewById(R.id.close_btn);
        snapshotProcessingSpinner = findViewById(R.id.snapshotProcessingSpinner);
        mImageView = findViewById(R.id.backgroundImgView);

        snapshotOverlay.setCallback(new OverlayView.DrawCallback() {
            @Override
            public void drawCallback(final Canvas canvas) {

                // If there's no result, just return
                if (segmentResult == null) {
                    return;
                }

                // Create a bitmap for undetected items. Scale it up for the camera.
                Bitmap notSkyBitmap = segmentResult.createBackgroundBitmap();
                float scaleWidth = ((float) cameraSize.getWidth()) / notSkyBitmap.getWidth();
                float scaleHeight = ((float) cameraSize.getWidth()) / notSkyBitmap.getHeight();
                final Matrix matrix = new Matrix();
                float scale = Math.min(scaleWidth, scaleHeight);
                matrix.postScale(scale, scale);

                // Create a scaled bitmap
                Bitmap scaledNonSkyBitmap = Bitmap.createBitmap(notSkyBitmap, 0, 0, notSkyBitmap.getWidth(), notSkyBitmap.getHeight(), matrix, false);

                // Hide the button until the animation finishes.
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeButton.setVisibility(View.GONE);
                    }
                });

                // Start the animation
                mImageView.post(new Runnable() {
                    @Override
                    public void run() {
                        mScaleFactor = (float) mImageView.getHeight() / (float) mImageView.getDrawable().getIntrinsicHeight();
                        mMatrix.postScale(mScaleFactor, mScaleFactor);
                        mImageView.setImageMatrix(mMatrix);
                        animate();
                    }
                });

                // Draw the non-sky bitmap on the bottom center.
                canvas.drawBitmap(scaledNonSkyBitmap, (cameraSize.getWidth() - scaledNonSkyBitmap.getWidth()) / 2, cameraSize.getHeight() - scaledNonSkyBitmap.getHeight(), new Paint());
            }
        });

        snapshotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!shouldSample.compareAndSet(true, false)) {
                    return;
                }

                snapshotOverlay.postInvalidate();

                runInBackground(
                        new Runnable() {
                            @Override
                            public void run() {
                                showSpinner();
                                segmentResult = predictor.predict(visionImage);
                                showSnapshotLayout();
                                hideSpinner();
                                snapshotOverlay.postInvalidate();
                            }
                        });
            }
        });

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPreviewLayout();
                shouldSample.set(true);
                mMatrix = new Matrix();
                mImageView.setImageMatrix(mMatrix);
                mDisplayRect = new RectF();
            }
        });
    }

    private void animate() {
        int width = mImageView.getDrawable().getIntrinsicWidth();
        int height = mImageView.getDrawable().getIntrinsicHeight();
        mDisplayRect.set(0, 0, width, height);
        mMatrix.mapRect(mDisplayRect);
        animate(mDisplayRect.left, mDisplayRect.left - (mDisplayRect.right - mImageView.getWidth()));
    }

    private void animate(float from, float to) {
        mCurrentAnimator = ValueAnimator.ofFloat(from, to);
        mCurrentAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (Float) animation.getAnimatedValue();

                mMatrix.reset();
                mMatrix.postScale(mScaleFactor, mScaleFactor);
                mMatrix.postTranslate(value, 0);

                mImageView.setImageMatrix(mMatrix);
            }
        });
        mCurrentAnimator.setDuration(DURATION);
        mCurrentAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                closeButton.setVisibility(View.VISIBLE);
            }
        });
        mCurrentAnimator.start();
    }

    private void showSpinner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snapshotProcessingSpinner.setVisibility(View.VISIBLE);
            }
        });
    }

    private void hideSpinner() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                snapshotProcessingSpinner.setVisibility(View.GONE);
            }
        });
    }

    private void showSnapshotLayout() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                previewLayout.setVisibility(View.GONE);
                snapshotLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void showPreviewLayout() {
        previewLayout.setVisibility(View.VISIBLE);
        snapshotLayout.setVisibility(View.GONE);
    }

    @Override
    public void onImageAvailable(final ImageReader reader) {
        Image image = reader.acquireLatestImage();

        if (image == null) {
            return;
        }

        if (!shouldSample.get()) {
            image.close();
            return;
        }
        // Feel free to uncomment if you'd like to try it out with a static image
        // Bitmap testImage = getBitmapForAsset(this, "climbing.png");
        // visionImage = FritzVisionImage.fromBitmap(testImage, 0);

        // Using the image from the camera
        visionImage = FritzVisionImage.fromMediaImage(image, imgRotation);
        image.close();
    }

    public static Bitmap getBitmapForAsset(Context context, String path) {
        AssetManager assetManager = context.getAssets();
        InputStream inputStream;
        Bitmap bitmap = null;
        try {
            inputStream = assetManager.open(path);
            bitmap = BitmapFactory.decodeStream(inputStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return bitmap;
    }
}