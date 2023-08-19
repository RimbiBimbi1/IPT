package pl.ipt;


import pl.ipt.ImageProcessor.ImageProcessor;
import pl.ipt.Painter.Painter;
import pl.ipt.ShadowRemover.ShadowRemover;
import pl.ipt.Triangle.Triangle;
import pl.ipt.TriangleTexturer.TriangleTexturer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static pl.ipt.ImageSaver.saveImageAs;


public class iptApp {
    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/9.jpg");
//            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.jpg");


            BufferedImage img = ImageIO.read(fileInputStream);
            BufferedImage imgCp = Painter.getExtendedImage(0, img);

            saveImageAs(img, "Input");

//            BufferedImage illuminationInvariant1 = new ShadowRemover(img).CalcInvariant(0);
//            saveImageAs(illuminationInvariant1, "Invariant");
//
//            BufferedImage illuminationInvariant2 = new ShadowRemover(img).CalcInvariant(120);
//            saveImageAs(illuminationInvariant2, "Invariant");
//
//            BufferedImage illuminationInvariant3 = new ShadowRemover(img).CalcInvariant(240);
//            saveImageAs(illuminationInvariant3, "Invariant");


            Painter painter = new Painter();

            for (int i =0; i < 30; i+=3){
                BufferedImage illuminationInvariant = new ShadowRemover(img).CalcInvariant(i);
                painter.setImage(illuminationInvariant);
                painter.applyGaussian();

                illuminationInvariant = painter.getImage();
                saveImageAs(illuminationInvariant, "Invariant");


                int min = painter.getMinRed();
                int max = painter.getMaxRed();

                int threshold = (int) (min+ (max-min)*0.40);

                BufferedImage binary = ImageProcessor.toBinaryWithThreshold(illuminationInvariant,threshold);
                saveImageAs(binary,"Binary");
            }
//            BufferedImage illuminationInvariant = new ShadowRemover(img).CalcInvariant();
//            saveImageAs(illuminationInvariant, "Invariant");
//
//            Painter painter = new Painter(illuminationInvariant);
//            painter.applyGaussian();
//            saveImageAs(painter.getImage(), "Blurred");
//
//            painter.applySobel();
//            saveImageAs(painter.getImage(), "Sobel");
//
//            painter.applyNonMaxSuppression();
//            saveImageAs(painter.getImage(), "Suppression");
//
//            painter.applyDoubleThreshold();
//            saveImageAs(painter.getImage(), "Double threshold");
//
//            painter.applyHysteresis();
//            saveImageAs(painter.getImage(), "Hysteresis");
//
//            BufferedImage eroded = ImageProcessor.erode(painter.getImage());
//            saveImageAs(eroded, "Eroded");
//
//            eroded = ImageProcessor.erode(eroded);
//            saveImageAs(eroded, "Eroded");
//
//            eroded = ImageProcessor.erode(eroded);
//            saveImageAs(eroded, "Eroded");
//
//            BufferedImage floodfilled = ImageProcessor.floodfill(eroded);
//            saveImageAs(floodfilled, "Floodfiled");
//
//            eroded = ImageProcessor.erode(floodfilled);
//            saveImageAs(eroded, "Eroded");
//
//            BufferedImage dilated = ImageProcessor.dilate(eroded);
//            saveImageAs(dilated, "Dilated");
//
//            dilated = ImageProcessor.dilate(dilated);
//            saveImageAs(dilated, "Dilated");
//
//            dilated = ImageProcessor.dilate(dilated);
//            saveImageAs(dilated, "Dilated");
//
//            List<Point> corners = ImageProcessor.sheetDetector(dilated);
//
////
//////            for (Point c : corners
//////            ) {
//////                try {
//////                    imgCp.setRGB(c.x, c.y, new Color(0, 255, 0).getRGB());
//////                } catch (Exception ignored) {
//////                }
//////                try {
//////                    imgCp.setRGB(c.x + 1, c.y, new Color(0, 255, 0).getRGB());
//////                } catch (Exception ignored) {
//////                }
//////                try {
//////                    imgCp.setRGB(c.x - 1, c.y, new Color(0, 255, 0).getRGB());
//////                } catch (Exception ignored) {
//////                }
//////                try {
//////                    imgCp.setRGB(c.x, c.y + 1, new Color(0, 255, 0).getRGB());
//////                } catch (Exception ignored) {
//////                }
//////                try {
//////                    imgCp.setRGB(c.x, c.y - 1, new Color(0, 255, 0).getRGB());
//////                } catch (Exception ignored) {
//////                }
//////            }
////
//            int a4Width = 1050;
//            int a4Height = 1485;
//
//            BufferedImage a4 = new BufferedImage(a4Width, a4Height, BufferedImage.TYPE_3BYTE_BGR);
//
//            Triangle textureUpperRight = new Triangle(corners.get(0), corners.get(1), corners.get(2));
//            Triangle textureLowerLeft = new Triangle(corners.get(0), corners.get(3), corners.get(2));
//
//            Triangle targetUpperRight = new Triangle(new Point(0,0), new Point(a4Width-1,0), new Point(a4Width-1,a4Height-1));
//            Triangle targetLowerLeft = new Triangle(new Point(0,0), new Point(0,a4Height-1), new Point(a4Width-1,a4Height-1));
//
//            TriangleTexturer triangleTexturer = new TriangleTexturer(a4, illuminationInvariant, textureUpperRight, targetUpperRight );
//            triangleTexturer.texture();
//            triangleTexturer.setFrom(textureLowerLeft);
//            triangleTexturer.setTo(targetLowerLeft);
//            triangleTexturer.texture();
//
////
//            BufferedImage result = triangleTexturer.getCanvas();
//            saveImageAs(result, "Textured");
////
////
////            ShadowRemover shadowRemover = new ShadowRemover(result);
////            BufferedImage invariant = shadowRemover.CalcInvariant();
////            fileOutputStream = new FileOutputStream("src/main/resources/Results/14Invariant.jpg");
////            ImageIO.write(invariant, "jpg", fileOutputStream);
////            fileOutputStream.close();
////
////
////
////
////            painter = new Painter(invariant);
////            painter.applyGaussian(3, 1.5);
////            fileOutputStream = new FileOutputStream("src/main/resources/Results/15Gaussian.jpg");
////            ImageIO.write(painter.getImage(), "jpg", fileOutputStream);
////            fileOutputStream.close();
////
////            painter.applySobel();
////            fileOutputStream = new FileOutputStream("src/main/resources/Results/16Sobel.jpg");
////            ImageIO.write(painter.getImage(), "jpg", fileOutputStream);
////            fileOutputStream.close();
////
////            painter.applyNonMaxSuppression();
////            fileOutputStream = new FileOutputStream("src/main/resources/Results/17Supp.jpg");
////            ImageIO.write(painter.getImage(), "jpg", fileOutputStream);
////            fileOutputStream.close();
////
////            painter.applyDoubleThreshold();
////            fileOutputStream = new FileOutputStream("src/main/resources/Results/18DT.jpg");
////            ImageIO.write(painter.getImage(), "jpg", fileOutputStream);
////            fileOutputStream.close();
////
////
//


//            FileOutputStream fileOutputStream
            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(1f);

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(new FileImageOutputStream(
                    new File("src/main/resources/result.jpg")
            ));
//            writer.write(null, new IIOImage(result, null, null), jpegParams);

            writer.dispose();


//            ImageIO.write(dilated, "jpg", fileOutputStream);

            fileInputStream.close();
//            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
