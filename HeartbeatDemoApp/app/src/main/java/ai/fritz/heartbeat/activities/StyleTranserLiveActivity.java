package ai.fritz.heartbeat.activities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.media.Image;
import android.media.ImageReader;
import android.os.Bundle;
import android.util.Size;
import android.widget.Button;

import java.util.concurrent.atomic.AtomicBoolean;

import ai.fritz.core.FritzOnDeviceModel;
import ai.fritz.fritzvisionhairsegmentationmodel.HairSegmentationOnDeviceModelFast;
import ai.fritz.fritzvisionstylepaintings.PaintingStyles;
import ai.fritz.heartbeat.R;
import ai.fritz.heartbeat.ui.OverlayView;
import ai.fritz.vision.FritzVision;
import ai.fritz.vision.FritzVisionImage;
import ai.fritz.vision.FritzVisionOrientation;
import ai.fritz.vision.ImageRotation;
import ai.fritz.vision.imagesegmentation.FritzVisionSegmentationPredictorOptions;
import ai.fritz.vision.imagesegmentation.MaskClass;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictor;
import ai.fritz.vision.styletransfer.FritzVisionStylePredictorOptions;
import ai.fritz.vision.styletransfer.FritzVisionStyleResult;

public class StyleTranserLiveActivity  extends BaseCameraActivity implements ImageReader.OnImageAvailableListener {

    protected FritzVisionImage fritzVisionImage;
    private AtomicBoolean computing = new AtomicBoolean(false);
    private FritzVisionStylePredictor predictor;

    private ImageRotation imageRotation;
    protected Button chooseModelBtn;
    private Bitmap resultImage;
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onPreviewSizeChosen(Size previewSize, final Size cameraViewSize, int rotation) {
        imageRotation = FritzVisionOrientation.getImageRotationFromCamera(this, cameraId);
        chooseModelBtn = findViewById(R.id.chose_model_btn);

        setCallback(
                new OverlayView.DrawCallback() {
                    @Override
                    public void drawCallback(final Canvas canvas) {
                        handleDrawingResult(canvas, cameraViewSize);
                    }
                });

        loadPredictor(1);

    }

    protected void loadPredictor(int choice) {
        FritzOnDeviceModel onDeviceModel = getModel(choice);
        FritzVisionStylePredictorOptions options = new FritzVisionStylePredictorOptions();
        predictor = FritzVision.StyleTransfer.getPredictor(onDeviceModel, options);
    }

    private FritzOnDeviceModel getModel(int choice) {
        FritzOnDeviceModel[] styles = PaintingStyles.getAll();
        return styles[choice];
    }

    protected void handleDrawingResult(Canvas canvas, Size cameraSize) {
        if (resultImage != null)
            canvas.drawBitmap(resultImage, null, new RectF(0, 0, cameraSize.getWidth(), cameraSize.getHeight()), null);
    }

    @Override
    protected int getLayoutId() {
         return R.layout.camera_connection_fragment_tracking;
    }

    @Override
    public void onImageAvailable(ImageReader imageReader) {
        Image image = imageReader.acquireLatestImage();

        if (image == null) {
            return;
        }

        if (!computing.compareAndSet(false, true)) {
            image.close();
            return;
        }
        fritzVisionImage = FritzVisionImage.fromMediaImage(image, imageRotation);
        image.close();

        runInBackground(
                new Runnable() {
                    @Override
                    public void run() {
                        runInference(fritzVisionImage);
                        requestRender();
                        computing.set(false);
                    }
                });

    }

    protected void runInference(FritzVisionImage visionImage) {
        FritzVisionStyleResult styleResult = predictor.predict(visionImage);
        resultImage = styleResult.toBitmap();
    }
}
