package pl.ipt.Painter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Painter {
    private BufferedImage image;

    private Integer width;
    private Integer height;


    private Double[][] sobelX;
    private Double[][] sobelY;
    private Double[][] sobel;


    /**
     * Methods
     **/


    private BufferedImage getExtendedImage(Integer ext) {
        BufferedImage e = new BufferedImage(width + ext, height + ext, BufferedImage.TYPE_INT_RGB);
        e.setRGB(ext/2,ext/2,width,height,image.getRGB(0,0,width,height,null,0,width),0,width);
//        e.getGraphics().drawImage(image, ext / 2, ext / 2, null);
        return e;
    }

    private Integer getMaxRGB() {
        int max = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = new Color(image.getRGB(j, i)).getRed();
                if (red > max) {
                    max = red;
                }
                if (max >= 255) return 255;
            }
        }
        return max;
    }

    public void toGrayScale() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                Color c = new Color(image.getRGB(j, i));
                int red = (int) (c.getRed() * 0.299);
                int green = (int) (c.getGreen() * 0.587);
                int blue = (int) (c.getBlue() * 0.114);

                int sum = red + green + blue;
                int grey = new Color(sum, sum, sum).getRGB();

                image.setRGB(j, i, grey);
            }
        }

    }

    public void applyGaussian() {
        applyGaussian(5, 2.5);
    }

    public void applyGaussian(Integer kernelSize, Double sd) {
        BufferedImage extended = getExtendedImage(kernelSize - 1);
        BufferedImage imageCopy = extended;

        Integer k = kernelSize >> 1;

        Double[][] kernel = gaussianKernel(kernelSize, sd);

        for (int i = k; i < height + k; i++) {
            for (int j = k; j < width + k; j++) {
                double gray = 0.0;

                for (int h = 0; h < kernelSize; h++) {
                    for (int g = 0; g < kernelSize; g++) {

                        Color c = new Color(imageCopy.getRGB(j - k + g, i - k + h));
                        gray += kernel[h][g] * c.getRed();


                    }
                }
                extended.setRGB(j, i, new Color((int) gray, (int) gray, (int) gray).getRGB());
            }
        }

        setImage(extended.getSubimage(k, k, width, height));
    }

    private Double[][] gaussianKernel(Integer size, Double sd) {
        Double[][] kernel = new Double[size][size];

        int k = size >> 1;
        double multiplier = 1 / (2 * Math.PI * sd * sd);


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double exponent = -((i - k) * (i - k) + (j - k) * (j - k)) / (2 * sd * sd);
                kernel[j][i] = multiplier * Math.exp(exponent);
            }
        }

        return kernel;
    }


    public void applySobel() {
        Integer[][] Kx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        Integer[][] Ky = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

        Double[][] Gx = directionalSobel(Kx);
        Double[][] Gy = directionalSobel(Ky);

        Double[][] G = new Double[width][height];
        Double[][] dir = new Double[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                G[j][i] = Math.sqrt((Gx[j][i] * Gx[j][i] + Gy[j][i] * Gy[j][i]));
                int gray = Math.min(255, G[j][i].intValue());
                image.setRGB(j, i, new Color(gray, gray, gray).getRGB());
                dir[j][i] = Math.atan2(Math.abs(Gy[j][i]), Math.abs(Gx[j][i]));
            }
        }
        setSobel(dir);
    }

    private Double[][] directionalSobel(Integer[][] k) {
        BufferedImage extended = getExtendedImage(2);

        Double[][] gradient = new Double[width][height];

        BufferedImage imageCopy = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        imageCopy.setRGB(0, 0, width, height, image.getRGB(0, 0, width, height, null, 0, width), 0, width);

        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {

                double gray = 0.0;

                for (int h = 0; h < 3; h++) {
                    for (int g = 0; g < 3; g++) {
                        Color c = new Color(extended.getRGB(j - 1 + g, i - 1 + h));
                        gray += (k[g][h] * c.getRed());
                    }
                }
                gradient[j - 1][i - 1] = gray;
            }
        }

        return gradient;
    }


    public void applyNonMaxSuppression() {
        BufferedImage imageCopy = image;
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int q = new Color(255, 255, 255).getRGB();
                int r = new Color(255, 255, 255).getRGB();

                Double angle = sobel[j][i];


                try {
                    if ((0 <= angle && angle < 0.125 * Math.PI) || (0.875 * Math.PI <= angle && angle <= Math.PI)) {
                        q = imageCopy.getRGB(j, i + 1);
                        r = imageCopy.getRGB(j, i - 1);
                    } else if (0.125 * Math.PI <= angle && angle < 0.375 * Math.PI) {
                        q = imageCopy.getRGB(j + 1, i - 1);
                        r = imageCopy.getRGB(j - 1, i + 1);
                    } else if (0.375 * Math.PI <= angle && angle < 0.625 * Math.PI) {
                        q = imageCopy.getRGB(j + 1, i);
                        r = imageCopy.getRGB(j - 1, i);
                    } else if (0.625 * Math.PI <= angle && angle < 0.875 * Math.PI) {
                        q = imageCopy.getRGB(j - 1, i - 1);
                        r = imageCopy.getRGB(j + 1, i + 1);
                    }
                } catch (Exception e) {
                    q = 255;
                    r = 255;
                }


                if (imageCopy.getRGB(j, i) >= q && imageCopy.getRGB(j, i) >= r) {
                    result.setRGB(j, i, imageCopy.getRGB(j, i));
                } else {
                    result.setRGB(j, i, 0);
                }
            }
        }
        setImage(result);
    }

    public void applyDoubleThreshold() {
//        applyDoubleThreshold(0.3, 0.8);
        applyDoubleThreshold(0.55, 0.5);
    }

    public void applyDoubleThreshold(double lowThresholdRatio, double highThresholdRatio) {
        Integer maxRGB = getMaxRGB();

        int highThreshold = (int) (maxRGB * highThresholdRatio);
        int lowThreshold = (int) (highThreshold * lowThresholdRatio);


        int high = new Color(highThreshold, highThreshold, highThreshold).getRGB();
        int low = new Color(lowThreshold, lowThreshold, lowThreshold).getRGB();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = image.getRGB(j, i);
                if (grey >= high) {
                    image.setRGB(j, i, high);
                } else if (grey < low) {
                    image.setRGB(j, i, 0);
                } else {
                    image.setRGB(j, i, low);
                }
            }
        }
        applyHysteresis(low, high);
    }

    public void applyHysteresis(Integer weak, Integer strong) {
        BufferedImage extended = getExtendedImage(2);

        int white = new Color(255, 255, 255).getRGB();

        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                if (extended.getRGB(j, i) >= weak) {
                    if (
                            extended.getRGB(j - 1, i - 1) <= strong ||
                                    extended.getRGB(j, i - 1) <= strong ||
                                    extended.getRGB(j + 1, i - 1) <= strong ||
                                    extended.getRGB(j - 1, i) <= strong ||
                                    extended.getRGB(j + 1, i) <= strong ||
                                    extended.getRGB(j - 1, i + 1) <= strong ||
                                    extended.getRGB(j, i + 1) <= strong ||
                                    extended.getRGB(j + 1, i + 1) <= strong
                    ) {
                        extended.setRGB(j, i, white);
                    } else
                        extended.setRGB(j, i, 0);
                }
            }
        }
        setImage(extended.getSubimage(1, 1, width, height));
    }

    public void applyHarris() {

    }


    /**
     * Constructors
     **/

    public Painter() {
    }

    public Painter(BufferedImage image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }

    public BufferedImage getImage() {
        return image;
    }

    /**
     * Getters and Setters
     **/

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public Double[][] getSobelX() {
        return sobelX;
    }

    public void setSobelX(Double[][] sobelX) {
        this.sobelX = sobelX;
    }

    public Double[][] getSobelY() {
        return sobelY;
    }

    public void setSobelY(Double[][] sobelY) {
        this.sobelY = sobelY;
    }

    public Double[][] getSobel() {
        return sobel;
    }

    public void setSobel(Double[][] sobel) {
        this.sobel = sobel;
    }
}
