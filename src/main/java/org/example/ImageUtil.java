package org.example;

import javafx.scene.image.Image;
import lombok.experimental.UtilityClass;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

@UtilityClass
public class ImageUtil {
    public static Map.Entry<Mat, String> loadImageInGray(String imagePath) {
        Mat mat = Imgcodecs.imread(imagePath);
        Mat grayMat = new Mat();
        try {
            Imgproc.cvtColor(mat, grayMat, Imgproc.COLOR_RGB2GRAY);
        } catch (Throwable t) {
            throw new RuntimeException("Can not convert image to gray");
        }

        return Map.entry(grayMat, getExtOrDefault(imagePath, ".jpg"));
    }

    public static Image getJavaFXImage(Mat mat, String ext) {
        Objects.requireNonNull(ext);
        Objects.requireNonNull(mat);

        MatOfByte bytes = new MatOfByte();
        try {
            Imgcodecs.imencode(ext, mat, bytes);
        } catch (Throwable t) {
            throw new RuntimeException(String.format("Can not encode %s to byte array", ext));
        }

        InputStream inputStream = new ByteArrayInputStream(bytes.toArray());
        return new Image(inputStream);
    }
    public static String getExtOrDefault(String src, String defaults) {
        int lastDot = src.lastIndexOf('.');
        if (lastDot == -1) {
            return defaults;
        }

        if (lastDot == src.length() - 1) {
            return defaults;
        }

        return src.substring(lastDot);

    }
}
