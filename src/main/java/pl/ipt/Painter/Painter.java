package pl.ipt.Painter;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Stack;

public class Painter {
    private BufferedImage image;

    private Integer width;
    private Integer height;


    private Double[][] sobelX;
    private Double[][] sobelY;
    private Double[][] sobel;

    private Integer lowThreshold;
    private Integer highThreshold;


    /**
     * Methods
     **/


    private BufferedImage getExtendedImage(Integer ext) {
        return getExtendedImage(ext, image);
    }

    public static BufferedImage getExtendedImage(Integer ext, BufferedImage img) {
        int w = img.getWidth();
        int h = img.getHeight();
        BufferedImage e = new BufferedImage(w + ext, h + ext, BufferedImage.TYPE_INT_RGB);
        e.setRGB(ext / 2, ext / 2, w, h, img.getRGB(0, 0, w, h, null, 0, w), 0, w);
//        e.getGraphics().drawImage(image, ext / 2, ext / 2, null);
        return e;
    }



    public Integer getMaxRed() {
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

    public Integer getMinRed() {
        int min = 255;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int red = new Color(image.getRGB(j, i)).getRed();
                if (red < min) {
                    min = red;
                }
                if (min <= 0) return 0;
            }
        }
        return min;
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
        applyGaussian(3, 1.3);
    }

    public void applyGaussian(Integer kernelSize, Double sd) {
        BufferedImage extended = getExtendedImage(kernelSize - 1);
        BufferedImage imageCopy = getExtendedImage(0, extended);

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


        double sum = 0.0;

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                double exponent = -((i - k) * (i - k) + (j - k) * (j - k)) / (2 * sd * sd);
                kernel[j][i] = multiplier * Math.exp(exponent);
                sum += kernel[j][i];
            }
        }

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                kernel[j][i] = kernel[j][i] / sum;
            }
        }


        return kernel;
    }


    public void applySobel() {
        Integer[][] Kx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        Integer[][] Ky = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};

//        Double[][] Gx = directionalSobel(Kx);
//        Double[][] Gy = directionalSobel(Ky);

        setSobelX(directionalSobel(Kx));
        setSobelY(directionalSobel(Ky));

        Double[][] G = new Double[width][height];
        Double[][] dir = new Double[width][height];

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                G[j][i] = Math.sqrt((sobelX[j][i] * sobelX[j][i] + sobelY[j][i] * sobelY[j][i]));
                int gray = Math.min(255, G[j][i].intValue());
                image.setRGB(j, i, new Color(gray, gray, gray).getRGB());
                dir[j][i] = Math.atan2(Math.abs(sobelY[j][i]), Math.abs(sobelX[j][i]));
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
        applyDoubleThreshold(0.25, 0.25);
    }

    public void applyDoubleThreshold(double lowThresholdRatio, double highThresholdRatio) {
        Integer maxRed = getMaxRed();

        highThreshold = (int) (maxRed * highThresholdRatio);
        lowThreshold = (int) (highThreshold * lowThresholdRatio);
        int highRGB = new Color(highThreshold,highThreshold,highThreshold).getRGB();
        int lowRGB = new Color(lowThreshold,lowThreshold,lowThreshold).getRGB();


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = new Color(image.getRGB(j, i)).getRed();
                if (grey >= highThreshold) {
                    image.setRGB(j, i, highRGB);
                } else if (grey < lowThreshold ) {
                    image.setRGB(j, i, 0);
                } else {
                    image.setRGB(j, i, lowRGB);
                }
            }
        }
    }

    public void applyHysteresis(){
        BufferedImage extended = getExtendedImage(2);
        int white = new Color(255, 255, 255).getRGB();
        int highRGB = new Color(highThreshold,highThreshold,highThreshold).getRGB();

        Stack<Point> tracked = new Stack<>();
        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                int grey = new Color(extended.getRGB(j,i)).getRed();

                if (grey>=this.highThreshold){
                    tracked.push(new Point(j,i));
                }
            }
        }

        while(!tracked.isEmpty()){
            Point p = tracked.pop();
            extended.setRGB(p.x,p.y, highRGB);
            for (int i=-1;i<2;i++){
                for (int j=-1;j<2;j++){
                    if (i==0 && j==0){
                        continue;
                    }
                    Integer pRGB =  new Color(extended.getRGB(p.x+i,p.y+j)).getRed();
                    if ( this.lowThreshold <= pRGB && pRGB < this.highThreshold){
                        tracked.push(new Point(p.x+i,p.y+j));
                    }
                }
            }
        }

        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {
                if (new Color(extended.getRGB(j, i)).getRed() == this.highThreshold) {
                    extended.setRGB(j, i, white);
                }
                else extended.setRGB(j, i, 0);
            }
        }

        setImage(extended.getSubimage(1, 1, width, height));
    }

    public void applyHarris() {
        applyHarris(7, 2.5);
    }

    public void applyHarris(int kSize, double sd) {
        Double[][] harrisResponse = new Double[width][height];

        int k = kSize >> 1;
        Double[][] kernel = gaussianKernel(kSize, sd);


        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                double[][] tensor = new double[2][2];
                for (int g = 0; g < kSize; g++) {
                    for (int h = 0; h < kSize; h++) {
                        double checkX = 0;
                        double checkY = 0;

                        try {
                            checkX = sobelX[j - k + g][i - k + h];
                        } catch (Exception ignored) {
                        }

                        try {
                            checkY = sobelY[j - k + g][i - k + g];
                        } catch (Exception ignored) {
                        }
                        tensor[0][0] += kernel[g][h] * checkX * checkX;
                        tensor[0][1] += kernel[g][h] * checkX * checkY;
                        tensor[1][0] += kernel[g][h] * checkX * checkY;
                        tensor[1][1] += kernel[g][h] * checkY * checkY;
                    }
                }

                double tensorDet = tensor[0][0] * tensor[1][1] - tensor[0][1] * tensor[1][0];
                double tensorTrace = tensor[0][0] * tensor[1][1];

                harrisResponse[j][i] = tensorDet / tensorTrace;
            }
        }

        int white = new Color(255, 225, 255).getRGB();
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 1; i < height - 1; i++) {
            for (int j = 1; j < width - 1; j++) {
                Double middle = harrisResponse[j][i];
                if (harrisResponse[j - 1][i - 1] < middle
                        && harrisResponse[j][i - 1] < middle
                        && harrisResponse[j + 1][i - 1] < middle
                        && harrisResponse[j - 1][i] < middle
                        && harrisResponse[j + 1][i] < middle
                        && harrisResponse[j - 1][i + 1] < middle
                        && harrisResponse[j][i + 1] < middle
                        && harrisResponse[j + 1][i + 1] < middle) {
                    result.setRGB(j, i, white);
                }
            }
        }
        setImage(result);
    }


    /**
     * Constructors
     **/

    public Painter() {
    }

    public Painter(BufferedImage image) {
        this.image = getExtendedImage(0, image);
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
        this.image = getExtendedImage(0, image);
        this.width = image.getWidth();
        this.height = image.getHeight();
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
