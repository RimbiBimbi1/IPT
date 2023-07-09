package pl.ipt;


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

            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Results/0input.jpg");
            ImageIO.write(img,"jpg",fileOutputStream);
            fileOutputStream.close();

            Painter painter = new Painter(img);

            painter.toGrayScale();
            fileOutputStream = new FileOutputStream("src/main/resources/Results/1grayscale.jpg");
            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);
            fileOutputStream.close();

            painter.applyGaussian();
            fileOutputStream = new FileOutputStream("src/main/resources/Results/2gaussian.jpg");
            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);
            fileOutputStream.close();

            painter.applySobel();
            fileOutputStream = new FileOutputStream("src/main/resources/Results/3sobel.jpg");
            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);
            fileOutputStream.close();

            painter.applyNonMaxSuppression();
            fileOutputStream = new FileOutputStream("src/main/resources/Results/4nonmax.jpg");
            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);
            fileOutputStream.close();

            painter.applyDoubleThreshold();
            fileOutputStream = new FileOutputStream("src/main/resources/Results/5postHisteresis.jpg");
            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);
            fileOutputStream.close();

//            ImageIO.write(painter.getImage(),"jpg",fileOutputStream);


//            painter.applyHarris();

            BufferedImage eroded = ImageProcessor.erode(painter.getImage());
            fileOutputStream = new FileOutputStream("src/main/resources/Results/6eroded.jpg");
            ImageIO.write(eroded,"jpg",fileOutputStream);
            fileOutputStream.close();

            eroded = ImageProcessor.erode(eroded);
            fileOutputStream = new FileOutputStream("src/main/resources/Results/7eroded.jpg");
            ImageIO.write(eroded,"jpg",fileOutputStream);
            fileOutputStream.close();

//            ImageIO.write(eroded,"jpg",fileOutputStream);

//
//
//            BufferedImage dilated = ImageProcessor.dilate(eroded);
//            dilated = ImageProcessor.dilate(dilated);

//            ImageIO.write(dilated,"jpg",fileOutputStream);

            BufferedImage floodfilled = ImageProcessor.floodfill(eroded);
            fileOutputStream = new FileOutputStream("src/main/resources/Results/8floodfilled.jpg");
            ImageIO.write(floodfilled,"jpg",fileOutputStream);
            fileOutputStream.close();

//            ImageIO.write(floodfilled,"jpg",fileOutputStream);
//
            eroded = ImageProcessor.erode(floodfilled);
            fileOutputStream = new FileOutputStream("src/main/resources/Results/9eroded.jpg");
            ImageIO.write(eroded,"jpg",fileOutputStream);
            fileOutputStream.close();
//            ImageIO.write(eroded,"jpg",fileOutputStream);
//
            BufferedImage dilated = ImageProcessor.dilate(eroded);
            fileOutputStream = new FileOutputStream("src/main/resources/Results/10dilated.jpg");
            ImageIO.write(dilated,"jpg",fileOutputStream);
            fileOutputStream.close();

            dilated = ImageProcessor.dilate(dilated);
            fileOutputStream = new FileOutputStream("src/main/resources/Results/11dilated.jpg");
            ImageIO.write(dilated,"jpg",fileOutputStream);
            fileOutputStream.close();

            dilated = ImageProcessor.dilate(dilated);
            fileOutputStream = new FileOutputStream("src/main/resources/Results/12dilated.jpg");
            ImageIO.write(dilated,"jpg",fileOutputStream);
            fileOutputStream.close();

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
            fileOutputStream = new FileOutputStream("src/main/resources/Results/13result.jpg");
            ImageIO.write(result,"jpg",fileOutputStream);
            fileOutputStream.close();

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
