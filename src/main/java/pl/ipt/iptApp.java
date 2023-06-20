package pl.ipt;

import org.opencv.core.Mat;
import pl.ipt.ImageConverter.ImageConverter;
import pl.ipt.ImageProcessor.ImageProcessor;
import pl.ipt.Painter.Painter;
import pl.ipt.Triangle.Triangle;
import pl.ipt.TriangleTexturer.TriangleTexturer;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;


public class iptApp {
    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/1.jpg");
//            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.jpg");

            BufferedImage img = ImageIO.read(fileInputStream);

            BufferedImage imgCp = Painter.getExtendedImage(0, img);


            Painter painter = new Painter(img);

            painter.toGrayScale();
            painter.applyGaussian();
            painter.applySobel();
            painter.applyNonMaxSuppression();
            painter.applyDoubleThreshold();

//            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);


//            painter.applyHarris();

            BufferedImage eroded = ImageProcessor.erode(painter.getImage());
            eroded = ImageProcessor.erode(eroded);

//            ImageIO.write(eroded,"jpg",fileOutputStream);

//
//
//            BufferedImage dilated = ImageProcessor.dilate(eroded);
//            dilated = ImageProcessor.dilate(dilated);

//            ImageIO.write(dilated,"jpg",fileOutputStream);

            BufferedImage floodfilled = ImageProcessor.floodfill(eroded);
//            ImageIO.write(floodfilled,"jpg",fileOutputStream);
//
            eroded = ImageProcessor.erode(floodfilled);
//            ImageIO.write(eroded,"jpg",fileOutputStream);
//
            BufferedImage dilated = ImageProcessor.dilate(eroded);
            dilated = ImageProcessor.dilate(dilated);
            dilated = ImageProcessor.dilate(dilated);

            List<Point> corners = ImageProcessor.sheetDetector(dilated);
//            BufferedImage image = ImageConverter.IntArray2BufImg(ImageProcessor.getSheetDetector(img), img.getWidth(), img.getHeight());

//            System.out.println(corners);
//            img = ImageIO.read(fileInputStream);


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

            int a4Width = 1050;
            int a4Height = 1485;

            BufferedImage a4 = new BufferedImage(a4Width, a4Height, BufferedImage.TYPE_3BYTE_BGR);

            Triangle textureUpperRight = new Triangle(corners.get(0), corners.get(1), corners.get(2));
            Triangle textureLowerLeft = new Triangle(corners.get(0), corners.get(3), corners.get(2));

            Triangle targetUpperRight = new Triangle(new Point(0,0), new Point(a4Width-1,0), new Point(a4Width-1,a4Height-1));
            Triangle targetLowerLeft = new Triangle(new Point(0,0), new Point(0,a4Height-1), new Point(a4Width-1,a4Height-1));


            TriangleTexturer triangleTexturer = new TriangleTexturer(a4, imgCp, textureUpperRight, targetUpperRight );
            triangleTexturer.texture();
            triangleTexturer.setFrom(textureLowerLeft);
            triangleTexturer.setTo(targetLowerLeft);
            triangleTexturer.texture();


            BufferedImage result = triangleTexturer.getCanvas();

            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(null);
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(1f);

            ImageWriter writer = ImageIO.getImageWritersByFormatName("jpg").next();
            writer.setOutput(new FileImageOutputStream(
                    new File("src/main/resources/result.jpg")
            ));
            writer.write(null, new IIOImage(result, null, null), jpegParams);

            writer.dispose();


//            ImageIO.write(dilated, "jpg", fileOutputStream);

            fileInputStream.close();
//            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
