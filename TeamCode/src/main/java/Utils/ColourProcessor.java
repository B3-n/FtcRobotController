package Utils;

import static org.opencv.core.Core.inRange;
import static org.opencv.core.CvType.CV_8U;
import static org.opencv.imgproc.Imgproc.CHAIN_APPROX_SIMPLE;
import static org.opencv.imgproc.Imgproc.RETR_TREE;
import static org.opencv.imgproc.Imgproc.boundingRect;
import static org.opencv.imgproc.Imgproc.dilate;
import static org.opencv.imgproc.Imgproc.erode;
import static org.opencv.imgproc.Imgproc.findContours;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import static android.graphics.Color.*;

import org.firstinspires.ftc.robotcore.external.function.Consumer;
import org.firstinspires.ftc.robotcore.external.function.Continuation;
import org.firstinspires.ftc.robotcore.external.stream.CameraStreamSource;
import org.firstinspires.ftc.robotcore.internal.camera.calibration.CameraCalibration;
import org.firstinspires.ftc.vision.VisionProcessor;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.core.Rect;
import static org.opencv.imgproc.Imgproc.distanceTransform;
import static org.opencv.core.Core.normalize;
import static org.opencv.imgproc.Imgproc.threshold;
import org.opencv.core.CvType;
import org.opencv.core.Core;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.acmerobotics.dashboard.config.Config;

public class ColourProcessor implements VisionProcessor, CameraStreamSource {

    @Override
    public void init(int width, int height, CameraCalibration calibration) {
        lastFrame.set(Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565));
    }

    @Override
    public void onDrawFrame(Canvas canvas, int onscreenWidth, int onscreenHeight, float scaleBmpPxToCanvasPx, float scaleCanvasDensity, Object userContext) {

        Paint artifactPaintGreen = new Paint();
        artifactPaintGreen.setColor(GREEN);
        artifactPaintGreen.setStyle(Paint.Style.STROKE);
        artifactPaintGreen.setStrokeWidth(scaleCanvasDensity * 8);

        Paint artifactPaintPurple = new Paint();
        artifactPaintPurple.setColor(MAGENTA);
        artifactPaintPurple.setStyle(Paint.Style.STROKE);
        artifactPaintPurple.setStrokeWidth(scaleCanvasDensity * 8);

        if(targetDetected) {

            if (largestGreenRect != null) {
                canvas.drawRect(makeGraphicsRect(largestGreenRect, scaleBmpPxToCanvasPx), artifactPaintGreen);
            }
            if (largestPurpleRect != null) {
                canvas.drawRect(makeGraphicsRect(largestPurpleRect, scaleBmpPxToCanvasPx), artifactPaintPurple);
            }
        }
    }

    @Override
    public void getFrameBitmap(Continuation<? extends Consumer<Bitmap>> continuation) {
        continuation.dispatch(bitmapConsumer -> bitmapConsumer.accept(lastFrame.get()));
    }

    public enum RECT_OPTION {
        AREA, WIDTH, HEIGHT, X, Y
    }

    public static Rect largestRect;
    //SET VARIABLE COLOUR NAMES
    public static Rect largestPurpleRect;
    public static Rect largestGreenRect;

    public static int erodeConstant = 5;
    public static int dilateConstant = 5;

    public static boolean targetDetected = false;
    ArrayList<MatOfPoint> contoursGreen = new ArrayList<>();
    ArrayList<MatOfPoint> contoursPurple = new ArrayList<>();

    List<Rect> purpleRects = new ArrayList<>();
    List<Rect> greenRects = new ArrayList<>();
    private static int IMG_HEIGHT = 0;
    private static int IMG_WIDTH = 0;
    //Set Variables after Calibration
    private Mat output = new Mat(),
            maskGreen = new Mat(),
            maskPurple = new Mat();
    private Mat hierarchy = new Mat();
    private Mat distanceMat = new Mat();

    private final AtomicReference<Bitmap> lastFrame =
            new AtomicReference<>(Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565));

    @Config
    public static class visionDash {
        public static int green_MaxH = 32;
        public static int green_MaxS = 255;
        public static int green_MaxV = 255;
        public static int green_MinH = 25;
        public static int green_MinS = 150;
        public static int green_MinV = 70;

        public static int purple_MaxH = 120;
        public static int purple_MaxS = 255;
        public static int purple_MaxV = 255;
        public static int purple_MinH = 110;
        public static int purple_MinS = 70;
        public static int purple_MinV = 100;
    }

    @Override
    public Mat processFrame(Mat input, long captureTimeNanos) {
        input.copyTo(output);
        contoursGreen.clear();
        contoursPurple.clear();
        greenRects.clear();
        purpleRects.clear();

        IMG_HEIGHT = input.rows();
        IMG_WIDTH = input.cols();

        /*telemetry.addLine(String.format("%d %d %d %d %d %d", visionDash.green_MaxH, visionDash.green_MaxS, visionDash.green_MaxV, visionDash.green_MinH, visionDash.green_MinS, visionDash.green_MinV));
        telemetry.update();*/

        Scalar MIN_THRESH_GREEN = new Scalar(visionDash.green_MinH, visionDash.green_MinS, visionDash.green_MinV);
        Scalar MAX_THRESH_GREEN = new Scalar(visionDash.green_MaxH, visionDash.green_MaxS, visionDash.green_MaxV);

        Scalar MIN_THRESH_PURPLE = new Scalar(visionDash.purple_MinH, visionDash.purple_MinS, visionDash.purple_MinV);
        Scalar MAX_THRESH_PURPLE = new Scalar(visionDash.purple_MaxH, visionDash.purple_MaxS, visionDash.purple_MaxV);

        Imgproc.cvtColor(input, maskGreen, Imgproc.COLOR_RGB2HSV);
        Imgproc.cvtColor(input, maskPurple, Imgproc.COLOR_RGB2HSV);
        inRange(maskGreen, MIN_THRESH_GREEN, MAX_THRESH_GREEN, maskGreen);
        inRange(maskPurple, MIN_THRESH_PURPLE, MAX_THRESH_PURPLE, maskPurple);

        Rect submatRect = new Rect(new Point(4, 4), new Point(IMG_WIDTH, IMG_HEIGHT));
        maskGreen = maskGreen.submat(submatRect);
        maskPurple = maskPurple.submat(submatRect);

        erode(maskGreen, maskGreen, new Mat(erodeConstant, erodeConstant, CV_8U));
        dilate(maskGreen, maskGreen, new Mat(dilateConstant, dilateConstant, CV_8U));
        erode(maskPurple, maskPurple, new Mat(erodeConstant, erodeConstant, CV_8U));
        dilate(maskPurple, maskPurple, new Mat(dilateConstant, dilateConstant, CV_8U));

        distanceTransform(maskGreen, distanceMat, Imgproc.DIST_L2, 3);
        normalize(distanceMat, distanceMat, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        threshold(distanceMat, maskGreen, 120, 255, Imgproc.THRESH_BINARY);
        distanceTransform(maskPurple, distanceMat, Imgproc.DIST_L2, 3);
        normalize(distanceMat, distanceMat, 0, 255, Core.NORM_MINMAX, CvType.CV_8UC1);
        threshold(distanceMat, maskPurple, 120, 255, Imgproc.THRESH_BINARY);

        findContours(maskGreen, contoursGreen, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);
        findContours(maskPurple, contoursPurple, hierarchy, RETR_TREE, CHAIN_APPROX_SIMPLE);

        largestGreenRect = getObjectsDetected(contoursGreen, 1);
        largestPurpleRect = getObjectsDetected(contoursPurple, 2);

        Bitmap b = Bitmap.createBitmap(output.width(), output.height(), Bitmap.Config.RGB_565);
        Utils.matToBitmap(output, b);
        lastFrame.set(b);

        return output;
    }

    public Rect getObjectsDetected(ArrayList<MatOfPoint> contours, int colour) {
        if (!contours.isEmpty()) {
            MatOfPoint largestContour = findLargestContour(contours);
            if (largestContour != null) {
                List<Rect> rects = new ArrayList<>();
                for (int i = 0; i < contours.size(); i++) {
                    Rect rect = boundingRect(contours.get(i));
                    rects.add(rect);
                }
                if (!rects.isEmpty()) {
                    this.largestRect = sortRectsByMaxOption(1, RECT_OPTION.AREA, rects).get(0);
                    targetDetected = true;
                    if (colour == 1 && !rects.isEmpty()) {
                        greenRects = sortRectsByMaxOption(1, RECT_OPTION.AREA, rects);
                    } else if (colour == 2 && !rects.isEmpty()) {
                        purpleRects = sortRectsByMaxOption(1, RECT_OPTION.AREA, rects);
                    }
                } else {
                    largestRect = null;
                    targetDetected = false;
                }}

                return largestRect;
            } else {
            return null;
        }
    }

    private MatOfPoint findLargestContour(List<MatOfPoint> contours) {
        double maxArea = 0;
        MatOfPoint largestContour = null;
        for (MatOfPoint contour : contours) {
            double area = Imgproc.contourArea(contour);
            if (area > maxArea) {
                maxArea = area;
                largestContour = contour;
            }
        }

        return largestContour;
    }

    public static List<Rect> sortRectsByMaxOption(int n, RECT_OPTION option, List<Rect> rects) {
        List<Rect> sorted_rects = new ArrayList<>();
        for (int j = 0; j < n; j++) {
            int alpha_i = getMaxIndex(rects, option);
            sorted_rects.add(rects.get(alpha_i));

            rects.remove(alpha_i);
            if (rects.isEmpty()) break;
        }

        return sorted_rects;
    }

    public static int getMaxIndex(List<Rect> rects, RECT_OPTION option){
        int alpha_index = 0;
        double max = Integer.MIN_VALUE;
        double cur = 0;
        for (int i=0; i < rects.size(); i++){

            switch (option){
                case X:
                    cur = rects.get(i).x;
                    break;

                case Y:
                    cur = rects.get(i).y;
                    break;

                case WIDTH:
                    cur = rects.get(i).width;
                    break;

                case HEIGHT:
                    cur = rects.get(i).height;
                    break;

                case AREA:
                    cur = rects.get(i).width * rects.get(i).height;
                    break;

            }
            if (cur > max) {
                max = cur;
                alpha_index = i;
            }
        }
        return alpha_index;
    }

    private android.graphics.Rect makeGraphicsRect(Rect rect, float scaleBmpPxToCanvasPx) {
        int left = Math.round(rect.x * scaleBmpPxToCanvasPx);
        int top = Math.round(rect.y * scaleBmpPxToCanvasPx);
        int right = left + Math.round(rect.width * scaleBmpPxToCanvasPx);
        int bottom = top + Math.round(rect.height * scaleBmpPxToCanvasPx);

        return new android.graphics.Rect(left, top, right, bottom);
    }
}