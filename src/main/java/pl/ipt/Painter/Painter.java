package pl.ipt.Painter;

import java.awt.*;
import java.awt.image.BufferedImage;

public class Painter {
    private BufferedImage image;

    private Integer width;
    private Integer height;

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
        applyGaussian(5, 1.3);
    }

    public void applyGaussian(Integer kernelSize, Double sd) {
        BufferedImage extended = new BufferedImage(width + kernelSize - 1, height + kernelSize - 1, BufferedImage.TYPE_INT_RGB);

        Integer k = kernelSize >> 1;

        Graphics graphics = extended.getGraphics();
        graphics.drawImage(image, k, k, null);

        BufferedImage copy = extended;

        Double[][] kernel = gaussianKernel(kernelSize, sd);

        for (int i = k; i < height + k; i++) {
            for (int j = k; j < width + k; j++) {
                Double grey = 0.0;

                for (int h = 0; h < kernelSize; h++) {
                    for (int g = 0; g < kernelSize; g++) {

                        Color c = new Color(copy.getRGB(j - k + g, i - k + h));
                        grey += kernel[h][g] * c.getRed();


                    }
                }
                extended.setRGB(j, i, new Color(grey.intValue(), grey.intValue(), grey.intValue()).getRGB());
            }
        }

        setImage(extended.getSubimage(k, k, width, height));
    }

    private Double[][] gaussianKernel(Integer size, Double sd) {
        Double[][] kernel = new Double[size][size];

        Integer k = size >> 1;
        Double multiplier = 1 / (2 * Math.PI * sd * sd);


        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                Double exponent = -((i - k) * (i - k) + (j - k) * (j - k)) / (2 * sd * sd);
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
            for (int j=0; j < width; j++){
                G[j][i] = Math.sqrt((Gx[j][i]*Gx[j][i]+Gy[j][i]*Gy[j][i]));
                Integer grey = Math.min(255, G[j][i].intValue());
                image.setRGB(j,i,new Color(grey,grey,grey).getRGB());
                dir[j][i] = Math.atan2(Math.abs(Gy[j][i]),Math.abs(Gx[j][i]));
            }
        }


    }

    private Double[][] directionalSobel(Integer[][] k) {
        BufferedImage extended = new BufferedImage(width + 2, height + 2, BufferedImage.TYPE_INT_RGB);
        Graphics graphics = extended.getGraphics();

        graphics.drawImage(image, 1, 1, null);

        Double[][] gradient = new Double[width][height];

        BufferedImage copy = extended;

        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {

                Double grey = 0.0;

                for (int h = 0; h < 3; h++) {
                    for (int g = 0; g < 3; g++) {
                        Color c = new Color(copy.getRGB(j - 1 + g, i - 1 + h));
                        grey += (k[g][h] * c.getRed());
                    }
                }
                gradient[j - 1][i - 1] = grey;
            }
        }
        return gradient;
    }


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

    public void setImage(BufferedImage image) {
        this.image = image;
    }
}
