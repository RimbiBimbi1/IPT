package pl.ipt;


import pl.ipt.DiagonalCornerScanner.DiagonalCornerScanner;
import pl.ipt.ImageConverter.ImageConverter;
import pl.ipt.ImageProcessor.ImageProcessor;
import pl.ipt.Painter.Painter;
import pl.ipt.ShadowRemover.ShadowRemover;
import pl.ipt.Triangle.Triangle;
import pl.ipt.TriangleTexturer.TriangleTexturer;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.KeyPair;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static pl.ipt.ImageSaver.saveImageAs;


public class iptApp {

    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/result.jpg");
//            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.jpg");


            BufferedImage img = ImageIO.read(fileInputStream);
            BufferedImage imgCp = Painter.getExtendedImage(0, img);


//            Painter painter = new Painter(img);
//            painter.toGrayScale();
//            painter.applyGaussian();
//
//            painter.applySobel();
//            saveImageAs(painter.getImage(), "presheet");
//
//
//            painter.applyNonMaxSuppression();
//            painter.applyDoubleThreshold();
//            painter.applyHysteresis();
//
//
//            img = painter.getImage();
//
//
//            img = ImageProcessor.erode(img);
//            img = ImageProcessor.erode(img);
//            img = ImageProcessor.floodfill(img);
//            img = ImageProcessor.dilate(img);
//            img = ImageProcessor.dilate(img);
//
//
//
//
//
//            DiagonalCornerScanner diagonalCornerScanner = new DiagonalCornerScanner(img);
//            List<Point> corners = diagonalCornerScanner.scanForCorners();
//            for (Point c : corners
//            ) {
//                try {
//                    imgCp.setRGB(c.x, c.y, new Color(0, 255, 0).getRGB());
//                } catch (Exception ignored) {
//                }
//                try {
//                    imgCp.setRGB(c.x + 1, c.y, new Color(0, 255, 0).getRGB());
//                } catch (Exception ignored) {
//                }
//                try {
//                    imgCp.setRGB(c.x - 1, c.y, new Color(0, 255, 0).getRGB());
//                } catch (Exception ignored) {
//                }
//                try {
//                    imgCp.setRGB(c.x, c.y + 1, new Color(0, 255, 0).getRGB());
//                } catch (Exception ignored) {
//                }
//                try {
//                    imgCp.setRGB(c.x, c.y - 1, new Color(0, 255, 0).getRGB());
//                } catch (Exception ignored) {
//                }
//            }
//            int a4Width = 1050;
//            int a4Height = 1485;
//            BufferedImage a4 = new BufferedImage(a4Width, a4Height, BufferedImage.TYPE_3BYTE_BGR);
//            Triangle textureUpperRight = new Triangle(corners.get(0), corners.get(1), corners.get(2));
//            Triangle textureLowerLeft = new Triangle(corners.get(0), corners.get(3), corners.get(2));
//            Triangle targetUpperRight = new Triangle(new Point(0,0), new Point(a4Width-1,0), new Point(a4Width-1,a4Height-1));
//            Triangle targetLowerLeft = new Triangle(new Point(0,0), new Point(0,a4Height-1), new Point(a4Width-1,a4Height-1));
//            TriangleTexturer triangleTexturer = new TriangleTexturer(a4, imgCp, textureUpperRight, targetUpperRight );
//            triangleTexturer.texture();
//            triangleTexturer.setFrom(textureLowerLeft);
//            triangleTexturer.setTo(targetLowerLeft);
//            triangleTexturer.texture();
//            BufferedImage sheet = triangleTexturer.getCanvas();
//
//            saveImageAs(sheet, "sheet");


            AbstractMap<Integer, Double> entropies = new HashMap<>();


            BufferedImage illuminationInvariant = new ShadowRemover(img).CalcInvariant();



//                saveImageAs(illuminationInvariant, "Entropy %d deg".formatted(i));

//            List<Double> values = entropies.values().stream().sorted().toList();
//            double midEntropyValue = values.get(0);
//
//
//            int midEntropyAngle = entropies.entrySet().stream().filter(entry -> entry.getValue() == midEntropyValue).toList().get(0).getKey();
//
//            midEntropyImage = new ShadowRemover(img).CalcInvariant(midEntropyAngle);


            saveImageAs(illuminationInvariant, "Invariant");

            int[] pixels = ImageConverter.BufImg2IntArray(illuminationInvariant);
//            pixels = ImageConverter.IntArray2Greyscale(pixels);



            long sum = Arrays.stream(pixels).mapToLong(pixel -> (long)new Color(pixel).getRed() ).reduce(0,Long::sum);
            int avg = (int) (sum/pixels.length);


//            List<BigInteger> BIPixels = Arrays.stream(pixels).mapToObj(BigInteger::valueOf).toList();
//            BigInteger sum = BIPixels.stream().reduce(BigInteger.ZERO, BigInteger::add);


//            int avg = sum.divide(BigInteger.valueOf(pixels.length)).intValue();
//
////                    int min = painter.getMinRed();
////                    int max = painter.getMaxRed();
//            int colorCount = ShadowRemover.countGreyColors(minEntropyImage);
//



            Painter painter = new Painter(illuminationInvariant);
//            painter.toGrayScale();

            BufferedImage binary = ImageProcessor.toBinaryWithThreshold(painter.getImage(), avg);
            saveImageAs(binary,"Binary");

            binary = ImageProcessor.erode(binary);
            binary = ImageProcessor.erode(binary);
            binary = ImageProcessor.dilate(binary);
            binary = ImageProcessor.dilate(binary);
            binary = ImageProcessor.dilate(binary);

            Painter painter2 = new Painter(binary);
//            painter2.applyGaussian();
            painter2.applySobel();
            painter2.applyNonMaxSuppression();



            saveImageAs(painter.getImage(), "BinarySobel");



//            int lth = (int) ((2.0 / 3.0) * avg);
//            int hth = (int) ( avg + (1.0 / 3.0) * (255-avg));
//            BufferedImage ternary = ImageProcessor.toTernaryWithThresholds(painter.getImage(), lth, avg);
//            saveImageAs(ternary,"Ternary");


//            painter.applyGaussian(7, 3.5);
            painter.applyGaussian();
            saveImageAs(painter.getImage(), "gaussian");

            painter.applySobel();
            saveImageAs(painter.getImage(), "Sobel");


            painter.applyNonMaxSuppression();
            saveImageAs(painter.getImage(),"Non-Max Suppression");

            painter.applyDoubleThreshold();
            saveImageAs(painter.getImage(),"DoubleThreshold");

            painter.applyHysteresis();
            saveImageAs(painter.getImage(), "Hysteresis");

            BufferedImage binary2 = painter.getImage();
            binary2 = ImageProcessor.erode(binary2);
            binary2 = ImageProcessor.erode(binary2);
            binary2 = ImageProcessor.dilate(binary2);
            binary2 = ImageProcessor.dilate(binary2);

            BufferedImage avgImage = ImageProcessor.greyscaleAverage(painter2.getImage(), binary2);
            saveImageAs(avgImage, "Average");

            painter.setImage(avgImage);
            painter.applyHysteresis();

            saveImageAs(painter.getImage(), "Hysteresis");
            binary = painter.getImage();
            binary = ImageProcessor.floodfill(binary);
            saveImageAs(binary, "floodfill");












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
