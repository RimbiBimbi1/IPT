package pl.ipt.ShadowRemover;

import pl.ipt.ImageConverter.ImageConverter;

import java.awt.*;
import java.awt.image.BufferedImage;

import static pl.ipt.ImageConverter.ImageConverter.IntArray2BufImg;

public class ShadowRemover {
    private BufferedImage image;
    private int width;
    private int height;
    private int[] pixels;

    private BufferedImage reflectance;


    public ShadowRemover(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
        this.pixels = ImageConverter.BufImg2IntArray(image);
        this.reflectance = RgbToReflectance();
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int[] getPixels() {
        return pixels;
    }

    public void setPixels(int[] pixels) {
        this.pixels = pixels;
    }

    public BufferedImage getReflectance() {
        return reflectance;
    }

    public void setReflectance(BufferedImage reflectance) {
        this.reflectance = reflectance;
    }

//    public static BufferedImage CalcInvariant(BufferedImage image){
//        int[] pixels = ImageConverter.BufImg2IntArray(image);
//        int width = image.getHeight();
//        int height = image.getHeight();
//        int l = pixels.length;
//
//        double[] X = new double[l];
//        double[] Y = new double[l];
//        double [] invariant = new double[l];
//        int [] result = new int[l];
//
//
//        double meanX = 0.0;
//        double meanY = 0.0;
//
//        for (int i = 0; i < l; i++){
//            Color c = new Color(pixels[i]);
//            double r = Math.max(c.getRed(),1.0)/255.0;
//            double g = Math.max(c.getGreen(),1.0)/255.0;
//            double b = Math.max(c.getBlue(),1.0)/255.0;
//
//            double geoMean = Math.pow(r*g*b, 1.0/3);
//
//            X[i] = Math.log(r/geoMean);
//            Y[i] = Math.log(b/geoMean);
//
//            meanX += X[i];
//            meanY += Y[i];
//        }
//
//        meanX /= l;
//        meanY /= l;
//
//        for (int i = 0; i < l; i++) {
//            X[i] -= meanX;
//            Y[i] -= meanY;
//        }
//
//        double maxI = 0.0;
//        double minI = Double.MAX_VALUE;
//
//        double alpha = 0.11;
//        double cos = Math.cos(alpha);
//        double sin = Math.sin(alpha);
//
//        for (int i = 0; i < l; i++) {
//            invariant[i] = (X[i]*cos + Y[i]*sin);
//            if (maxI < invariant[i]){
//                maxI = invariant[i];
//            }
//            if(minI > invariant[i]){
//                minI = invariant[i];
//            }
//        }
//
//        for (int i = 0; i < l; i++) {
//            int gray = (int) ((invariant[i]-minI)/(maxI-minI)*255);
//            result[i] = new Color (gray,gray,gray).getRGB();
//        }
//
//        return ImageConverter.IntArray2BufImg(result,width,height);
//    }

//    public BufferedImage CalcInvariant(){
//        return CalcInvariant(0);
//    }

    public BufferedImage CalcInvariant(int angle){
        int l = pixels.length;
        double[] X = new double[l];
        double[] Y = new double[l];
        double [] invariant = new double[l];
        int [] result = new int[l];


        double meanX = 0.0;
        double meanY = 0.0;

        for (int i = 0; i < l; i++){
            Color c = new Color(pixels[i]);
            double r = Math.max(c.getRed(),1.0)/255.0;
            double g = Math.max(c.getGreen(),1.0)/255.0;
            double b = Math.max(c.getBlue(),1.0)/255.0;

            double geoMean = Math.pow(r*g*b, 1.0/3);

            X[i] = Math.log(r/geoMean);
            Y[i] = Math.log(b/geoMean);

            meanX += X[i];
            meanY += Y[i];
        }

        meanX /= l;
        meanY /= l;

        for (int i = 0; i < l; i++) {
            X[i] -= meanX;
            Y[i] -= meanY;
        }

        double maxI = 0.0;
        double minI = Double.MAX_VALUE;


        double alpha = angle * Math.PI / 180;
        double cos = Math.cos(alpha);
        double sin = Math.sin(alpha);

        for (int i = 0; i < l; i++) {
            invariant[i] = (X[i]*cos + Y[i]*sin);
            if (maxI < invariant[i]){
                maxI = invariant[i];
            }
            if(minI > invariant[i]){
                minI = invariant[i];
            }
        }

        for (int i = 0; i < l; i++) {
            int gray = (int) ((invariant[i]-minI)/(maxI-minI)*255);
            result[i] = new Color (gray,gray,gray).getRGB();
        }

        return ImageConverter.IntArray2BufImg(result,width,height);
    }


//    public BufferedImage CalcInvariant() {
//        int[] pixelsCp = new int[pixels.length];
//        double[] pixelsInv = new double[pixels.length];
//
////        System.arraycopy(pixels, 0, pixelsCp, 0, pixels.length);
//
//        double max = 0.0;
//        double min = 1.0;
//        double alpha = 0.9;
//
//
//        double[] pixelsRed = new double[pixels.length];
//        double[] pixelsGreen = new double[pixels.length];
//        double[] pixelsBlue = new double[pixels.length];
//        for (int i = 0; i < pixels.length; i++) {
//            Color c = new Color(pixels[i]);
//            pixelsRed[i] = Math.max(c.getRed(),1.0);
//            pixelsGreen[i] = Math.max(c.getGreen(),1.0);
//            pixelsBlue[i] = Math.max(c.getBlue(),1.0);
//
////            pixelsInv[i] = Math.log(pixelsGreen[i]) - alpha*Math.log(pixelsBlue[i]) - (1-alpha)*Math.log(pixelsRed[i]);
//            pixelsInv[i] = Math.log(pixelsGreen[i]) - alpha*Math.log(pixelsBlue[i]) - (1.0-alpha)*Math.log(pixelsRed[i]);
////            pixelsInv[i] = 0.5 + Math.log(pixelsGreen[i]/255) - alpha*Math.log(pixelsRed[i]/255) - (1-alpha)*Math.log(pixelsBlue[i]/255);
////            pixelsInv[i] = 0.5 + Math.log(pixelsBlue[i]/255) - alpha*Math.log(pixelsRed[i]/255) - (1-alpha)*Math.log(pixelsGreen[i]/255);
////            pixelsInv[i] = 0.5 + Math.log(pixelsBlue[i]/255) - alpha*Math.log(pixelsGreen[i]/255) - (1-alpha)*Math.log(pixelsRed[i]/255);
////            pixelsInv[i] = 0.5 + Math.log(pixelsRed[i]/255) - alpha*Math.log(pixelsBlue[i]/255) - (1-alpha)*Math.log(pixelsGreen[i]/255);
////            pixelsInv[i] = 0.5 + Math.log(pixelsRed[i]/255) - alpha*Math.log(pixelsGreen[i]/255) - (1-alpha)*Math.log(pixelsBlue[i]/255);
//
////            pixelsInv[i] = Math.log(pixelsGreen[i]) +  Math.log(pixelsRed[i]) + Math.log(pixelsBlue[i]);
//
////            System.out.println(0.1*Math.log(pixelsRed[i]/255));
//
//            if (pixelsInv[i] > max) max = pixelsInv[i];
//            if (pixelsInv[i] < min) min = pixelsInv[i];
//
//
//        }
//
//        for (int i = 0; i < pixels.length; i++) {
//            pixelsInv[i] = (pixelsInv[i] - min)/(max - min);
//            int grey = (int) (pixelsInv[i]*255);
//            pixelsCp[i] = new Color(grey,grey,grey).getRGB();
//        }
//        return IntArray2BufImg(pixelsCp, width, height);
//    }

    private BufferedImage RgbToReflectance() {
        int k = 0;
        int[] reflPixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++, k++) {
                int refl = calcPixelReflectance(j, i);
                reflPixels[k] = new Color(refl,refl,refl).getRGB();
            }
        }

        return IntArray2BufImg(reflPixels, width, height);
    }

    private int calcPixelReflectance(int x, int y) {
        Color c = new Color(image.getRGB(x, y));
        int red = (int) Math.floor(c.getRed() * 0.2125);
        int green = (int) Math.floor(c.getGreen() * 0.7154);
        int blue = (int) Math.floor(c.getBlue() * 0.0721);

        return red + green + blue;
    }

}
