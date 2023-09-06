package pl.ipt.ShadowRemover;

import pl.ipt.ImageConverter.ImageConverter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.List;

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


    public BufferedImage CalcInvariant() {
        int l = pixels.length;
        double[] X = new double[l];
        double[] Y = new double[l];
        double[] Xs = new double[l];
        double[] Ys = new double[l];
        double[] XYs = new double[l];
        double[] invariant = new double[l];
        int[] result = new int[l];


        BigDecimal meanX = BigDecimal.ZERO;
        BigDecimal meanY = BigDecimal.ZERO;
        BigDecimal covXY = BigDecimal.ZERO;
        BigDecimal sumXAbs = BigDecimal.ZERO;
        BigDecimal sumYAbs = BigDecimal.ZERO;

        for (int i = 0; i < l; i++) {
            Color c = new Color(pixels[i]);

            double r = Math.max(c.getRed(), 1.0);
            double g = Math.max(c.getGreen(), 1.0);
            double b = Math.max(c.getBlue(), 1.0);

            double geoMean = Math.pow(r * g * b, 1.0/3.0);
            X[i] = Math.log(r/b);
            Y[i] = Math.log(g/b);

            meanX = meanX.add(BigDecimal.valueOf(X[i]));
            meanY = meanY.add(BigDecimal.valueOf(Y[i]));
        }

        meanX = meanX.divide(BigDecimal.valueOf(l), RoundingMode.HALF_UP);
        meanY = meanY.divide(BigDecimal.valueOf(l), RoundingMode.HALF_UP);

        for (int i = 0; i < l; i++) {
            Xs[i] = X[i] - meanX.doubleValue();
            Ys[i] = X[i] - meanY.doubleValue();
            XYs[i] = Xs[i] * Ys[i];
            covXY = covXY.add(BigDecimal.valueOf(XYs[i]));
            sumXAbs = sumXAbs.add(BigDecimal.valueOf(Math.abs(Xs[i])));
            sumYAbs = sumYAbs.add(BigDecimal.valueOf(Math.abs(Ys[i])));
        }

        double alpha = Math.atan(1 / Math.signum(covXY.doubleValue())) * sumXAbs.divide(sumYAbs, RoundingMode.HALF_UP).doubleValue();

        double maxI = Double.MIN_VALUE;
        double minI = Double.MAX_VALUE;



        double sin = Math.sin(alpha);
        double cos = Math.cos(alpha);

        for (int i = 0; i < l; i++) {
            invariant[i] = (X[i] * cos + Y[i] * sin);

            if (maxI < invariant[i]) {
                maxI = invariant[i];
            }
            if (minI > invariant[i]) {
                minI = invariant[i];
            }
        }

        minI *= 1.025;
        maxI *= 0.975;

        for (int i = 0; i < l; i++) {
            if (invariant[i] > maxI) {
                invariant[i] = maxI;
            } else if (invariant[i] < minI) {
                invariant[i] = minI;
            }

            int gray = (int) ((invariant[i] - minI) / (maxI - minI) * 255);
//            System.out.println(gray);
            result[i] = new Color(gray, gray, gray).getRGB();
        }

        return ImageConverter.IntArray2BufImg(result, width, height);
    }

 public BufferedImage CalcInvariant1(int angle) {
        int l = pixels.length;
//        double[] invariant = new double[l];
        int[] result = new int[l];

        for (int i = 0; i < l; i++) {
            Color c = new Color(pixels[i]);
            double r = Math.max(c.getRed(), 1.0) / 255.0;
            double g = Math.max(c.getGreen(), 1.0) / 255.0;
            double b = Math.max(c.getBlue(), 1.0) / 255.0;

            double sum = r+g+b;

            r = Math.floor((r / sum)*255 );
            g = Math.floor((g / sum)*255 );
            b = Math.floor((b / sum)*255 );

            result[i] = new Color((int) r, (int) g, (int) b).getRGB();
        }

        return ImageConverter.IntArray2BufImg(result, width, height);
    }

    private BufferedImage RgbToReflectance() {
        int k = 0;
        int[] reflPixels = new int[width * height];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++, k++) {
                int refl = calcPixelReflectance(j, i);
                reflPixels[k] = new Color(refl, refl, refl).getRGB();
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


