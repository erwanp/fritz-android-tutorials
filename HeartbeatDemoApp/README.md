# Fritz Demo App

[ ![Codeship Status for fritzlabs/fritz-sdk-android](https://app.codeship.com/projects/c74152e0-65d1-0136-2d69-32e87736c6c6/status?branch=master)](https://app.codeship.com/projects/297281)
[![Twitter](https://img.shields.io/badge/twitter-@fritzlabs-blue.svg?style=flat)](http://twitter.com/fritzlabs)

Fritz AI helps you teach your applications how to see, hear, feel, think, and sense. Create ML-powered features in your mobile apps for both Android and iOS. Start with our ready-to-use feature APIs or connect and deploy your own custom models.

[Sign up](https://app.fritz.ai/register) for an account on Fritz AI in order to get started with machine learning in your apps.


**Vision API: Prebuilt models that you can simply drop into your apps:**

- [Image Segmentation](https://www.fritz.ai/features/image-segmentation.html): Create pixel level masks of different objects in a scene ([code](app/src/main/java/ai/fritz/heartbeat/activities/vision/ImageSegmentationActivity.java))
- [Image Labeling](https://www.fritz.ai/features/image-labeling.html): Classify different objects in an video or image ([code](app/src/main/java/ai/fritz/heartbeat/activities/vision/ImageLabelingActivity.java))
- [Pose Estimation](https://www.fritz.ai/features/pose-estimation.html): Identify and track a person's body position ([code](app/src/main/java/ai/fritz/heartbeat/activities/vision/PoseEstimationActivity.java))
- [Object Detection](https://www.fritz.ai/features/object-detection.html): Detect multiple objects and track their location ([code](app/src/main/java/ai/fritz/heartbeat/activities/vision/ObjectDetectionActivity.java))
- [Style Transfer](https://www.fritz.ai/features/style-transfer.html): Transform photos and videos into artistic masterpieces ([code](app/src/main/java/ai/fritz/heartbeat/activities/vision/StyleTransferActivity.java))

**Custom Models: Deploy, Monitor, and Update your own models:**

We currently support both TensorFlow Lite and TensorFlow Mobile for Android.

- [Analytics and Monitoring](https://www.fritz.ai/features/analytics-monitoring.html): Monitor machine learning models running on-device with Fritz.
- [Model Management](https://www.fritz.ai/features/model-management.html): Iterate on your ML models over-the-air, without having to release your app.
- [Model Protection](https://www.fritz.ai/features/model-protection.html): Use model protection to keep models from being tampered-with or stolen.

## Requirements

- Android Studio 3.2 or above
- Android device in developer model (USB debugging enabled)

## Getting Started

**Step 1: Create a Fritz Account**

In order to use Fritz, please [register for a free account](https://app.fritz.ai/register).

**Step 2: Clone / Fork the fritz-android-tutorials repository and open the demo app in Android Studio**

```
git clone https://github.com/fritzlabs/fritz-android-tutorials.git
```

In Android Studio, choose "Open an existing Android Studio project" and select `fritz-demo-app`.

**Step 3: Build the Android Studio Project**

Select "Build > Make Project" from the top nav. Download any missing libraries if applicable. This should sync the gradle dependencies so give the build a second to complete.

**Step 4: Install the app onto your device**

With your Android device connected, select `Run > Run App` from the top nav. After it's installed, select any of the options to try out the different ML features. When running the app for the first time, you'll have to give permissions to access the camera.

## Official Documentation

[SDK Documentation](https://docs.fritz.ai/)

[Android API Docs](https://docs.fritz.ai/android/latest/index.html)

## Join the community

[Heartbeat](https://heartbeat.fritz.ai/?utm_source=github&utm_campaign=fritz-models) is a community of developers interested in the intersection of mobile and machine learning. [Chat with us in Slack](https://join.slack.com/t/heartbeat-by-fritz/shared_invite/enQtMzY5OTM1MzgyODIzLTZhNTFjYmRiODU0NjZjNjJlOGRjYzI2OTIwY2M4YTBiNjM1ODU1ZmU3Y2Q2MmMzMmI2ZTIzZjQ1ZWI3NzBkZGU) and stay up to date on the latest mobile ML news with our [Newsletter](https://mobileml.us16.list-manage.com/subscribe?u=de53bead690affb8e9a21de8f&id=68acb5c0fd).

## Help

For any questions or issues, you can:
- Submit an issue on this repo
- Go to our [Help Center](https://docs.fritz.ai/help-center/index.html)
- Message us directly in [Slack](https://heartbeat-by-fritz.slack.com/join/shared_invite/enQtNTY5NDM2MTQwMTgwLTAyODE3MmQzZjU2NWE5MDNmYTgwM2E1MjU5Y2Y2NmI2YTlkMTMwZTAwYTAwMzQ5NzQ2NDBhZjhmYjU2YWY3OGU)
