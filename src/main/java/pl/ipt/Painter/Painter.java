package pl.ipt.Painter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Painter {
    public static BufferedImage toGrayScale(BufferedImage img){
        int width = img.getWidth();
        int height = img.getHeight();

        for (int i = 0; i<height; i++){
           for (int j = 0; j<width;j++){
               Color c = new Color(img.getRGB(j,i));
               int red = (int) (c.getRed() * 0.299);
               int green = (int) (c.getGreen() * 0.587);
               int blue = (int) (c.getBlue() *0.114);

               int sum = red+green+blue;
               int grey = new Color(sum,sum,sum).getRGB();

               img.setRGB(j,i,grey);
           }
        }
        return img;
    }
}
