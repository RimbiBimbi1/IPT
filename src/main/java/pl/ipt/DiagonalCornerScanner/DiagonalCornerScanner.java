package pl.ipt.DiagonalCornerScanner;

import pl.ipt.ImageConverter.ImageConverter;
import pl.ipt.Kernel.Kernel;

import java.awt.*;
import java.awt.image.BufferedImage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class DiagonalCornerScanner {
    private BufferedImage image;
    private int width;
    private int height;
    //    private int[] kernel;
    private int[] pixels;
    private int[] pixelNeighbours;
    private double[] pixelGravityCenterDistance;

    private int xStart;
    private int yStart;
    //    private int xEnd;
//    private int yEnd;
    private int xJump;
    private int yJump;
    private int yStep;
    private int xStep;


    public DiagonalCornerScanner() {
    }

    public DiagonalCornerScanner(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = ImageConverter.BufImg2IntArray(image);
        this.pixelNeighbours = new int[image.getHeight() * image.getWidth()];
        this.pixelGravityCenterDistance = new double[image.getHeight() * image.getWidth()];
    }

    private void calcPixelNeighboursCount(int kRadius) {
        int[] kernel = Kernel.roundKernel(image, kRadius);

        for (int y = kRadius; y < height - kRadius; y++) {
            for (int x = kRadius; x < width - kRadius; x++) {
                int pixel = y * width + x;

                for (int offset : kernel) {
                    try {
                        if (pixels[pixel + offset] == pixels[pixel]) pixelNeighbours[pixel]++;

                    } catch (Exception ignored) {
                    }
                }
                if (pixelNeighbours[pixel] > (kernel.length / 2)) pixelNeighbours[pixel] = 0;
            }
        }
    }

    private void calcPixelGravity(int kRadius) {
        ArrayList<Point> offsets = Kernel.roundKernel(kRadius);

        for (int y = kRadius; y < height - kRadius; y++) {
            for (int x = kRadius; x < width - kRadius; x++) {
                int pixel = y * width + x;

                int xSum = 0;
                int ySum = 0;
                if (pixelNeighbours[pixel] > 0) {
                    for (Point o : offsets) {
                        try {
                            int neighbourPixel = pixel + (o.y * width) + o.x;
                            if (pixels[neighbourPixel] == pixels[pixel]) {
                                xSum += o.x;
                                ySum += o.y;
                            }
                        } catch (Exception ignored) {
                        }
                    }
                    double gX = (double) xSum / pixelNeighbours[pixel];
                    double gY = (double) ySum / pixelNeighbours[pixel];
                    pixelGravityCenterDistance[pixel] = Math.sqrt(gX * gX + gY * gY);
                }
            }
        }
    }


    private boolean isPixelACorner(int x, int y) {
        return isPixelACorner(x, y, 3);
    }

    private boolean isPixelACorner(int x, int y, int size) {
        return false;
    }


    private void setCorner(int corner) {
        switch (corner) {
            case 0 -> {
                this.xStart = 0;
                this.yStart = 0;

//                this.xEnd = image.getWidth()-1;
//                this.yEnd = image.getHeight()-1;

                this.xJump = 1;
                this.yJump = 1;

                this.xStep = -1;
                this.yStep = 1;
            }
            case 1 -> {
                this.xStart = image.getWidth() - 1;
                this.yStart = 0;

//                this.xEnd = 0;
//                this.yEnd = image.getHeight()-1;

                this.xJump = 1;
                this.yJump = 1;

                this.xStep = -1;
                this.yStep = -1;
            }
            case 2 -> {
                this.xStart = image.getWidth() - 1;
                this.yStart = image.getHeight() - 1;

//                this.xEnd = 0;
//                this.yEnd = 0;

                this.xJump = -1;
                this.yJump = -1;

                this.xStep = 1;
                this.yStep = -1;
            }
            default -> {
                this.xStart = 0;
                this.yStart = image.getHeight() - 1;

//                this.xEnd = image.getWidth()-1;
//                this.yEnd = 0;

                this.xJump = 1;
                this.yJump = -1;

                this.xStep = 1;
                this.yStep = 1;
            }
        }
    }

    private Point scanXPhase() {
        while ((0 <= xStart) && (xStart < width)) {
            int x = xStart;
            int y = yStart;


            while ((0 <= x) && (x < width) && (0 <= y) && (y < height)) {
                int pixel = x + width * y;
//                System.out.println(new Point(x, y));
//                System.out.println(pixelNeighbours[x + y * width]);
//                System.out.println(pixelGravityCenterDistance[x + y * width]);
//                System.out.println(pixelGravityCenterDistance[pixel]);

//                if (pixelGravityCenterDistance[pixel] > 0.0) {
//                    return new Point(x, y);
//                    int pixelRed = (int) (
//                            100 * pixelGravityCenterDistance[pixel]);
//                    pixels[pixel] = new Color(pixelRed, 0,0).getRGB();
//                }
//                if (pixelGravityCenterDistance[pixel]>2) return new Point(x,y);

//                try{
//                    if (0 < pixelNeighbours[pixel] && pixelGravityCenterDistance[pixel] > 1.){
//                        return new Point(x,y);
//                    }
//                }catch (Exception ignored){}
                if (pixels[pixel]!=-1) return new Point(x,y);

                x += xStep;
                y += yStep;
            }
            xStart += xJump;
        }
        xStart -= xJump;
        yStart += yJump;
        return new Point(-1, -1);
    }

    private Point scanYPhase() {
        while ((0 <= yStart) && (yStart < height)) {
            int x = xStart;
            int y = yStart;

            while ((0 <= x) && (x < width) && (0 <= y) && (y < height)) {
                int pixel = x + width * y;
//                System.out.println(new Point(x, y));
//                System.out.println(pixelNeighbours[x + y * width]);
//                System.out.println(pixelGravityCenterDistance[x + y * width]);
//                System.out.println(pixelGravityCenterDistance[pixel]);
//                if (pixelGravityCenterDistance[pixel] > 0.0) {
//                    return new Point(x, y);
////                    int pixelRed = (int) (
////                            100 * pixelGravityCenterDistance[pixel]);
////                    pixels[pixel] = new Color(pixelRed, 0,0).getRGB();
//                }

//                try{
//                    if (0 < pixelNeighbours[pixel] && pixelGravityCenterDistance[pixel] > 1.){
//                        return new Point(x,y);
//                    }
//                }catch (Exception ignored){}


                if (pixels[pixel]!=-1) return new Point(x,y);

//                int pixel = x + width* y;
//                if (pixelGravityCenterDistance[pixel]>2) return new Point(x,y);

                x += xStep;
                y += yStep;
            }

            yStart += yJump;
        }
        xStart += xJump;
        yStart -= yJump;
        return new Point(-1, -1);
    }

    private Point scanForCorner(int corner) {
        setCorner(corner);
        Point result;
        if (corner % 2 == 0) {
            result = scanXPhase();
            if (result.x != 0) {
                return result;
            }
            result = scanYPhase();
        } else {
            result = scanYPhase();
            if (result.x != 0) {
                return result;
            }
            result = scanXPhase();
        }
        return result;
    }

    public List<Point> scanForCorners() {
//        int kRadius = 3;
//        calcPixelNeighboursCount(kRadius);
//        calcPixelGravity(kRadius);
        return Arrays.asList(scanForCorner(0), scanForCorner(1), scanForCorner(2), scanForCorner(3));
    }


    public int[] getPixels() {
        return pixels;
    }
}


