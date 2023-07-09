package pl.ipt.ImageProcessor;

import pl.ipt.DiagonalCornerScanner.DiagonalCornerScanner;
import pl.ipt.ImageConverter.ImageConverter;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ImageProcessor {


    public static BufferedImage erode(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);

        int[] imagePixels = ImageConverter.BufImg2IntArray(image);
        int[] resultPixels = new int[imagePixels.length];

        System.arraycopy(imagePixels, 0, resultPixels, 0, imagePixels.length);

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


        int[] imagePixels = ImageConverter.BufImg2IntArray(image);
        int[] resultPixels = new int[imagePixels.length];
        System.arraycopy(imagePixels, 0, resultPixels, 0, imagePixels.length);

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
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

    public static BufferedImage floodfill(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] imagePixels = ImageConverter.BufImg2IntArray(image);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);


        int white = new Color(255, 255, 255).getRGB();
        int grey = new Color(127, 127, 127).getRGB();
        Stack<Point> flooded = new Stack<Point>();

        for (int i=0;i<width;i++){
            flooded.push(new Point(i, 0));
            flooded.push(new Point(i, height-1));
        }
        for (int i=1;i<height-1;i++){
            flooded.push(new Point(0, i));
            flooded.push(new Point(width-1, i));
        }

//        flooded.push(new Point(0, 0));
//        flooded.push(new Point(width, 0));
//        flooded.push(new Point(0, height));
//        flooded.push(new Point(width, height));

        while (!flooded.isEmpty()) {
            Point p = flooded.pop();
            int i = p.x + p.y * width;
            try {
                if (imagePixels[i] < grey) {
                    imagePixels[i] = white;
                    if (p.y > 0) flooded.push(new Point(p.x, p.y - 1));
                    if (p.x > 0) flooded.push(new Point(p.x - 1, p.y));
                    if (p.x < width-1) flooded.push(new Point(p.x + 1, p.y));
                    if (p.y < height-1) flooded.push(new Point(p.x, p.y + 1));
                }
            } catch (Exception ignored) {
            }

        }

        result.setRGB(0, 0, width, height, imagePixels, 0, width);
        return result;
    }

//    private static Point upperLeftCornerScan(BufferedImage image) {
//        int width = image.getWidth();
//        int height = image.getHeight();
//
//        int xStart = 0;
//        int yStart = 0;
//
//        int xEnd = width;
//        int yEnd = height;
//
//        int xJump = 1;
//        int yJump = 1;
//
//        int xStep = -1;
//        int yStep = 1;
//
//        int i = xStart;
//        while (i < xEnd) {
//            int x = xStart;
//            int y = yStart;
//            while (x >= xStart && y < yEnd) {
//                System.out.println(new Point(x, y));
//
//                x += xStep;
//                y += yStep;
//
//            }
//
//            xStart += xJump;
//        }
//        xStart-=xJump;
//        yStart+=yJump;
//        while (yStart < height) {
//            int x = xStart;
//            int y = yStart;
//            while (x >= 0 && y < height) {
//                System.out.println(new Point(x, y));
//                image.setRGB(x,y, new Color(0,0,0).getRGB());
//
//                x += xStep;
//                y += yStep;
//            }
//
//
//            yStart += yJump;
//        }
//
//        return new Point(0, 0);
//    }

//    private static Point diagonalCornerScan(BufferedImage image, int corner) {
//        DiagonalCornerScanner dcs = new DiagonalCornerScanner(image,corner);
//
//
////
////
////        java.util.function.Supplier<Point> xPhase = () -> {
////            while ((0 <= config.xStart) && (config.xStart < width)) {
////                int x = config.xStart;
////                int y = config.yStart;
////
////                while ((0 <= x) && (x < width) && (y <= 0) && (y < height)) {
////
////                    System.out.println(new Point(x, y));
////
////                    x += config.xStep;
////                    y += config.yStep;
////                }
////
////                config.xStart += config.xJump;
////            }
////            config.xStart-= config.xJump;
////            config.yStart+= config.yJump;
////            return new Point(-1, -1);
////        };
////
////        java.util.function.Supplier<Point> yPhase = () -> {
////            while ((0 <= config.yStart) && (config.yStart < width)) {
////                int x = config.xStart;
////                int y = config.yStart;
////
////                while ((0 <= x) && (x < width) && (y <= 0) && (y < height)) {
////
////                    System.out.println(new Point(x, y));
////
////                    x += config.xStep;
////                    y += config.yStep;
////                }
////
////                config.yStart += config.yJump;
////            }
////            config.xStart+= config.xJump;
////            config.yStart-= config.yJump;
////            return new Point(-1, -1);
////        };
//
//        Point result;
//        if (corner%2==0){
//            result = xPhase.get();
//            if (result.x!=-1){
//                return result;
//            }
//
//            result = yPhase.get();
//
//        }
//        else {
//            result = yPhase.get();
//            if (result.x!=-1){
//                return result;
//            }
//
//            result = xPhase.get();
//        }
//
//        return result;
//    }

    public static List<Point> sheetDetector(BufferedImage image) {
        DiagonalCornerScanner dcs = new DiagonalCornerScanner(image);
        return dcs.scanForCorners();
    }

}
