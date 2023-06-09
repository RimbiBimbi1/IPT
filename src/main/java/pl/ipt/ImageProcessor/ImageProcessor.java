package pl.ipt.ImageProcessor;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Stack;

public class ImageProcessor {


    private static int[] getPixels(BufferedImage image) {


        int width = image.getWidth();
        int height = image.getHeight();
        int[] result = new int[width * height];


        image.getRGB(
                0,
                0,
                width,
                height,
                result,
                0,
                width);
        return result;
    }


    public static BufferedImage erode(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int[] imagePixels = getPixels(image);
        int[] resultPixels = new int[imagePixels.length];

        System.arraycopy(imagePixels,0,resultPixels,0,imagePixels.length);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                int grey = new Color(127, 127, 127).getRGB();
                int white = new Color(255, 255, 255).getRGB();


                List<Integer> kernel = Arrays.asList(
                        (j - 1) * width + i - 1, (j - 1) * width + i, (j - 1) * width + i + 1,
                        j * width + i, j * width + i + 1,
                        (j + 1) * width + i - 1, (j + 1) * width + i, (j + 1) * width + i + 1
                );
                int finalJ = j;
                int finalI = i;
                kernel.forEach(neighbour -> {
                    try {
                        if (imagePixels[neighbour] > grey) {
                            resultPixels[finalJ * width + finalI] = white;

                        }
                    } catch (Exception ignored) {
                    }

                });


            }
        }
        result.setRGB(0, 0, width, height, resultPixels, 0, width);
        return result;
    }


    public static BufferedImage dilate(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);


        int[] imagePixels = getPixels(image);
        int[] resultPixels = new int[imagePixels.length];
        System.arraycopy(imagePixels,0,resultPixels,0,imagePixels.length);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height ; j++) {
                int grey = new Color(127, 127, 127).getRGB();
                int black = new Color(0, 0, 0).getRGB();

                List<Integer> kernel = Arrays.asList(
                        (j - 1) * width + i - 1, (j - 1) * width + i, (j - 1) * width + i + 1,
                        j * width + i, j * width + i + 1,
                        (j + 1) * width + i - 1, (j + 1) * width + i, (j + 1) * width + i + 1
                );
                int finalJ = j;
                int finalI = i;
                kernel.forEach(neighbour -> {
                    try {
                        if (imagePixels[neighbour] < grey) {
                            resultPixels[finalJ * width + finalI] = black;
                        }
                    } catch (Exception ignored) {
                    }

                });

            }
        }

        result.setRGB(0, 0, width, height, resultPixels, 0, width);
        return result;

    }

    public static BufferedImage floodfill(BufferedImage image){
        int width = image.getWidth();
        int height = image.getHeight();
        int[] imagePixels = getPixels(image);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);


        int white = new Color(255,255,255).getRGB();
        int grey = new Color(127,127,127).getRGB();
        Stack<Point> flooded = new Stack<Point>();

        flooded.push(new Point(0,0));
        flooded.push(new Point(width,0));
        flooded.push(new Point(0,height));
        flooded.push(new Point(width,height));

        while(!flooded.isEmpty()){
            Point p = flooded.pop();
            int i = p.x + p.y * width;
            try {
                if(imagePixels[i]<grey){
                    imagePixels[i]=white;
                    if (p.y>0) flooded.push(new Point(p.x,p.y-1));
                    if (p.x>0) flooded.push(new Point(p.x-1,p.y));
                    if (p.x<width) flooded.push(new Point(p.x+1,p.y));
                    if (p.y<height) flooded.push(new Point(p.x,p.y+1));
                }
            }catch (Exception ignored){}

        }

        result.setRGB(0, 0, width, height, imagePixels, 0, width);
        return result;
    }


}
