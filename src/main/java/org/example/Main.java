package org.example;


import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import nu.pattern.OpenCV;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfInt;

import java.io.File;
import java.util.function.Function;


public class Main extends Application {

    Menu fileMenu = new Menu("_File");
    MenuItem chooseMenuItem = new MenuItem("_Choose...");
    FileChooser fileChooser = new FileChooser();

    TextField textField = new TextField();

    ImageView imageView = new ImageView();

    Mat mat = null;
    Mat initialMat = null;
    String ext = null;

    {
        imageView.setPreserveRatio(true);
        imageView.setFitHeight(720);
        imageView.setFitWidth(1280);
    }

    public static void main(String[] args) {
        OpenCV.loadShared();

        Application.launch(args);


    }

    public static <T> T parseOrDefault(Function<String, T> parseFunc, String src, T defaultt) {
        try {
            return parseFunc.apply(src);
        } catch (Exception e) {
            return defaultt;
        }
    }

    public void initConfigPane(Pane pane) {

        Button medianBlurButton = new Button("median blur");
        Button minimumBLurButton = new Button("minimum blur");
        Button maximumBlurButton = new Button("maximum blur");
        Button otsuThresholdButton = new Button("otsu treshold");
        Button histogramThresholdButton = new Button("histogram treshold");
        Button restoreButton = new Button("restore image");

        medianBlurButton.setOnAction(e -> {
                    if (this.mat == null) {
                        return;
                    }

                    Integer valueToSet = parseOrDefault(
                            Integer::parseInt,
                            textField.getText(),
                            0
                    );
                    textField.setText(valueToSet.toString());

                    this.mat = ImageProcessing.medianBlur(
                            this.mat,
                            valueToSet
                    );

                    imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
                }
        );

        minimumBLurButton.setOnAction(e -> {
                    if (this.mat == null) {
                        return;
                    }

                    Integer valueToSet = parseOrDefault(
                            Integer::parseInt,
                            textField.getText(),
                            0
                    );
                    textField.setText(valueToSet.toString());

                    this.mat = ImageProcessing.minimumBlur(
                            this.mat,
                            valueToSet
                    );

                    imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
                }
        );

        maximumBlurButton.setOnAction(e -> {
                    if (this.mat == null) {
                        return;
                    }

                    Integer valueToSet = parseOrDefault(
                            Integer::parseInt,
                            textField.getText(),
                            0
                    );
                    textField.setText(valueToSet.toString());

                    this.mat = ImageProcessing.maximumBlur(
                            this.mat,
                            valueToSet
                    );

                    imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
                }
        );

        otsuThresholdButton.setOnAction(e -> {
            if (this.mat == null) {
                return;
            }

            this.mat = ImageProcessing.processWithGlobalThresholdOtsu(this.mat);
            imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
        });

        histogramThresholdButton.setOnAction(e -> {
            if (this.mat == null) {
                return;
            }

            this.mat = ImageProcessing.processWithGlobalThresholdBinaryHistogram(this.mat);
            imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
        });

        restoreButton.setOnAction(e -> {
            if (this.mat == null) {
                return;
            }

            this.mat = this.initialMat;
            imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
        });


        pane.getStylesheets().add("styles.css");
        pane.getChildren().addAll(
                textField,
                medianBlurButton,
                minimumBLurButton,
                maximumBlurButton,
                otsuThresholdButton,
                histogramThresholdButton,
                restoreButton
        );


        // -------------------------------------------------
    }

    @Override
    public void start(Stage stage) throws Exception {
        VBox configPane = new VBox();
        configPane.setSpacing(5);
        configPane.setPadding(new Insets(5));
        configPane.setAlignment(Pos.CENTER);


        initConfigPane(configPane);


        fileMenu.getItems().add(chooseMenuItem);
        chooseMenuItem.setOnAction(e -> {
            fileChooser.setInitialDirectory(new File("."));
            File chosenFile = fileChooser.showOpenDialog(stage);
            if (chosenFile == null) {
                return;
            }

            try {
                var imageWithExt = ImageUtil.loadImageInGray(chosenFile.getAbsolutePath());
                Mat mat = imageWithExt.getKey();
                String ext = imageWithExt.getValue();
                this.mat = mat;
                this.initialMat = mat;
                this.ext = ext;

                byte[] compressed = ImageProcessing.compressImage(mat);

                System.out.println("Compressed size: " + compressed.length);

                byte[] decompressed = ImageProcessing.decompressImage(compressed);
                System.out.println("Decompressed size: " + decompressed.length);

                MatOfInt matOfByte = new MatOfInt();
                mat.convertTo(matOfByte, CvType.CV_8U);

                new Alert(Alert.AlertType.INFORMATION,
                        "Pre compressed size: " + matOfByte.total() * matOfByte.channels() + "\n"
                                + "Compressed size: " + compressed.length + "\n"
                                + "Decompressed size: " + decompressed.length
                ).showAndWait();


                imageView.setImage(ImageUtil.getJavaFXImage(mat, ext));
            } catch (Exception ex) {
                System.err.println(ex);
                new Alert(Alert.AlertType.ERROR, ex.getMessage())
                        .showAndWait();
            }
        });
        MenuBar menuBar = new MenuBar(fileMenu);

        BorderPane borderPane = new BorderPane();
        borderPane.setTop(menuBar);
        borderPane.setRight(imageView);
        borderPane.setLeft(configPane);
        Scene scene = new Scene(borderPane);
        stage.setScene(scene);
        stage.setMinWidth(1080);
        stage.setMinHeight(720);
        stage.show();
        stage.centerOnScreen();
    }
}