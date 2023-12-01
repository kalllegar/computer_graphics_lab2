package org.example;

import lombok.experimental.UtilityClass;
import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.awt.image.Kernel;
import java.util.ArrayList;

@UtilityClass
public class ImageProcessing {
    public Mat medianBlur(Mat image, int value) {
        Mat result = new Mat();
        Imgproc.medianBlur(image, result, value);

        return result;
    }

    public Mat multiplyByScalar(Mat image, int value) {
        Mat result = new Mat();
        Mat kernel = new Mat(new Size(value, value), Imgproc.MORPH_RECT);
        Imgproc.erode(image, result, kernel);

        return result;
    }


    public Mat toPowerOf(Mat image, int value) {
        Mat result = new Mat();
        Mat kernel = new Mat(new Size(value, value), Imgproc.MORPH_RECT);
        Imgproc.dilate(image, result, kernel);

        return result;
    }    public Mat processWithGlobalThresholdOtsu(Mat image) {
        Mat result = new Mat(image.size(), CvType.CV_8UC1);
        double max = Core.minMaxLoc(image).maxVal;

        Imgproc.threshold(image, result, 0, max, Imgproc.THRESH_OTSU);

        return result;
    }

    public Mat processWithGlobalThresholdBinaryHistogram(Mat image) {
        Mat result = new Mat(image.size(), CvType.CV_8UC1);
        double max = Core.minMaxLoc(image).maxVal;

        int t = calculateThreshold(image);
        Imgproc.threshold(image, result, t, max, Imgproc.THRESH_BINARY);

        return result;
    }
    public byte[] compressImage(Mat image) {
        MatOfInt mat = new MatOfInt();
        image.convertTo(mat, CvType.CV_8U);

        byte[] arr= new byte[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, arr);

        System.out.println("Pre compress size: " + arr.length);

        try {
            return CompressionUtil.compress(arr);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] decompressImage(byte[] image) {
        byte[] decompressed = new byte[0];
        try {
            decompressed = CompressionUtil.decompress(image);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return decompressed;
    }

    private int calculateThreshold(Mat image) {
        MatOfInt mat = new MatOfInt();
        image.convertTo(mat, CvType.CV_32S);

        int[] arr= new int[(int) (mat.total() * mat.channels())];
        mat.get(0, 0, arr);
        Core.MinMaxLocResult minMax = Core.minMaxLoc(image);

        int thresh = (int) (minMax.maxVal - minMax.minVal) / 2;
        int eps = 5;
        int prevThresh = Integer.MAX_VALUE - 256;

        ArrayList<Long> g1 = new ArrayList<>();
        ArrayList<Long> g2 = new ArrayList<>();

        while (Math.abs(thresh - prevThresh) > eps) {
            for (long j : arr) {
                if (j > thresh) {
                    g1.add(j);
                } else {
                    g2.add(j);
                }
            }

            double avg1 = 0;
            for (var x : g1) {
                avg1 += (double) x / g1.size();
            }
            double avg2 = 0;
            for (var x : g2) {
                avg2 += (double) x / g2.size();
            }
            prevThresh = thresh;
            thresh = (int) Math.round((avg1 + avg2) / 2);

            g1.clear();
            g2.clear();
        }

        return thresh;
    }

}
