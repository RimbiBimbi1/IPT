package pl.ipt;

import org.opencv.core.Mat;
import pl.ipt.ImageProcessor.ImageProcessor;
import pl.ipt.Painter.Painter;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.io.*;



public class iptApp {
    public static void main( String[] args){

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/1.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.jpg");

            BufferedImage img = ImageIO.read(fileInputStream);


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
            ImageIO.write(dilated,"jpg",fileOutputStream);

            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
