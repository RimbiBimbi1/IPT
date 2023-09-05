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

            BufferedImage illuminationInvariant = new ShadowRemover(img).CalcInvariant();

            saveImageAs(illuminationInvariant, "Invariant");

            int[] pixels = ImageConverter.BufImg2IntArray(illuminationInvariant);

            long sum = Arrays.stream(pixels).mapToLong(pixel -> (long)new Color(pixel).getRed() ).reduce(0,Long::sum);
            int avg = (int) (sum/pixels.length);

            Painter painter = new Painter(illuminationInvariant);

            BufferedImage binary = ImageProcessor.toBinaryWithThreshold(painter.getImage(), avg);
            saveImageAs(binary,"Binary");

            painter.applyGaussian();
            saveImageAs(painter.getImage(), "Gaussian");

            painter.applySobel();
            saveImageAs(painter.getImage(), "Sobel");












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
