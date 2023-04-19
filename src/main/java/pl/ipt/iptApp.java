package pl.ipt;

import pl.ipt.Painter.Painter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

public class iptApp {
    public static void main( String[] args){

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/1.jpg");
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.jpg");

            BufferedImage img = ImageIO.read(fileInputStream);

            img = Painter.toGrayScale(img);


            ImageIO.write(img,"jpg",fileOutputStream);


            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
