package pl.ipt.Painter;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;

public class Painter {
    private BufferedImage image;

    private int width;
    private int height;

    private Double[][] sobelDirection;


    /**
     * Methods
     **/


    private int getRed (BufferedImage img, int x, int y){
        Color c = new Color(img.getRGB(x,y));
        return c.getRed();
    }

    private int getBlue (BufferedImage img, int x, int y){
        Color c = new Color(img.getRGB(x,y));
        return c.getBlue();
    }

    private int getGreen (BufferedImage img, int x, int y){
        Color c = new Color(img.getRGB(x,y));
        return c.getGreen();
    }

    private BufferedImage getImageCopy(BufferedImage originalImage){
        ColorModel cm = originalImage.getColorModel();
        boolean alpha = originalImage.isAlphaPremultiplied();
        WritableRaster wr = originalImage.copyData(null);

        return new BufferedImage(cm, wr, alpha, null);
    }


    private BufferedImage getExtendedImage(int ext){
        BufferedImage e = new BufferedImage(width+ext, height+ext, BufferedImage.TYPE_INT_RGB);
        e.getGraphics().drawImage(image,ext/2, ext/2,null);
        return e;
    }

    private int getMaxRGB(){
        int max = 0;
        for (int i=0; i<height;i++){
            for (int j=0;j<width;j++){
                int red = getRed(image, j,i);
                if(red > max){
                    max = red;
                }
                if (max >=255)return 255;
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
        applyGaussian(5, 1.3);
    }

    public void applyGaussian(int kernelSize, Double sd) {
        BufferedImage extended = getExtendedImage(kernelSize-1);
        BufferedImage imageCopy = getImageCopy(extended);

        int k = kernelSize >> 1;

        Double[][] kernel = gaussianKernel(kernelSize, sd);

        for (int i = k; i < height + k; i++) {
            for (int j = k; j < width + k; j++) {
                double grey = 0.0;

                for (int h = 0; h < kernelSize; h++) {
                    for (int g = 0; g < kernelSize; g++) {

                        int red = getRed(imageCopy, j-k+g, i-k+h);
//                        Color c = new Color(imageCopy.getRGB(j - k + g, i - k + h));
                        grey += kernel[h][g] * red;
                    }
                }

                extended.setRGB(j, i, new Color((int) grey, (int) grey, (int) grey).getRGB());
            }
        }

        image.getGraphics().drawImage(extended.getSubimage(k, k, width, height),0,0,null);
//        setImage(extended.getSubimage(k, k, width, height));
    }

    private Double[][] gaussianKernel(int size, Double sd) {
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
        int[][] Kx = {{-1, 0, 1}, {-2, 0, 2}, {-1, 0, 1}};
        int[][] Ky = {{1, 2, 1}, {0, 0, 0}, {-1, -2, -1}};


        Double[][] Ix = directionalSobel(Kx);
        Double[][] Iy = directionalSobel(Ky);


        Double[][] G = new Double[width][height];
        Double[][] dir = new Double[width][height];

        BufferedImage imageCp = getImageCopy(image);

        double GMax =0.0;

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                G[j][i] = Math.sqrt((Ix[j][i] * Ix[j][i] + Iy[j][i] * Iy[j][i]));
                if (GMax< G[j][i]) GMax=G[j][i];
                dir[j][i] = Math.atan2(Math.abs(Iy[j][i]), Math.abs(Ix[j][i]));
//                int grey = (int) Math.min(255, G[j][i]);
////                imageCp.setRGB(j, i, new Color(grey, grey, grey).getRGB());

            }
        }
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = (int) (G[j][i]/GMax * 255);
                imageCp.setRGB(j, i, new Color(grey, grey, grey).getRGB());
            }
        }

        setImage(imageCp);
        setSobelDirection(dir);
    }

    private Double[][] directionalSobel(int[][] k) {
        BufferedImage extended = getExtendedImage(2);

        Double[][] gradient = new Double[width][height];

//        BufferedImage imageCopy = getImageCopy(extended);

        for (int i = 1; i < height + 1; i++) {
            for (int j = 1; j < width + 1; j++) {

                double grey = 0.0;

                for (int h = 0; h < 3; h++) {
                    for (int g = 0; g < 3; g++) {
                        int red = getRed(extended, j-1+g, i-1+h);
//                        Color c = new Color(extended.getRGB(j - 1 + g, i - 1 + h));
//                        grey += (k[g][h] * c.getRed());
                        grey += (k[g][h] * red);

                    }
                }

//                System.out.println(grey);
                gradient[j - 1][i - 1] = grey;
            }
        }
        return gradient;
    }


    public void applyNonMaxSuppression() {
        BufferedImage imageCopy = getImageCopy(image);
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int q = 255;
                int r = 255;

                Double angle = sobelDirection[j][i];

                try {
                    if ((0 <= angle && angle < 0.125 * Math.PI) || (0.875 * Math.PI <= angle && angle <= Math.PI)) {;
                        q = getRed(imageCopy,j,i+1);
                        r = getRed(imageCopy,j,i-1);
                    } else if (0.125 * Math.PI <= angle && angle < 0.375 * Math.PI) {
                        q = getRed(imageCopy,j+1,i-1);
                        r = getRed(imageCopy,j-1,i+1);
                    } else if (0.375 * Math.PI <= angle && angle < 0.625 * Math.PI) {
                        q = getRed(imageCopy,j+1,i);
                        r = getRed(imageCopy,j-1,i);
                    } else if (0.625 * Math.PI <= angle && angle < 0.875 * Math.PI) {
                        q = getRed(imageCopy,j-1,i-1);
                        r = getRed(imageCopy,j+1,i+1);
                    }
                } catch (Exception e) {
                    q = 255;
                    r = 255;
                }

                if (getRed(imageCopy, j,i) >= q && getRed(imageCopy, j,i) >= r) {
                    result.setRGB(j, i, imageCopy.getRGB(j, i));
                } else {
                    result.setRGB(j, i, 0);
                }
            }
        }
        setImage(result);
    }

    public void applyDoubleThreshold(){
        applyDoubleThreshold(
                0.02,
                0.2);
    }

    public void applyDoubleThreshold(double lowThresholdRatio, double highThresholdRatio) {
        int maxRGB = getMaxRGB();

        int highThreshold = (int)(maxRGB * highThresholdRatio);
        int lowThreshold = (int)(highThreshold * lowThresholdRatio);

        int high = new Color(255,255,255).getRGB();
        int low = new Color(lowThreshold,lowThreshold,lowThreshold).getRGB();

        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
//                int grey = new Color(image.getRGB(j,i)).getRed();
                int red = getRed(image, j, i);
                if (red >= highThreshold){
                    image.setRGB(j,i,high);
                } else if (red < lowThreshold) {
                    image.setRGB(j,i,0);
                }else{
                    image.setRGB(j,i,low);
                }
            }
        }
        applyHysteresis(lowThreshold,highThreshold);
    }

    public void applyHysteresis(int weak, int strong){
        BufferedImage extended = getExtendedImage(2);
        BufferedImage exCopy = getImageCopy(extended);

        int high = new Color(255,255,255).getRGB();

        for (int i = 1; i < height+1; i++) {
            for (int j = 1; j < width+1; j++) {
                if (getRed(exCopy, j, i)==weak){
                    if (
                            getRed(exCopy, j-1, i-1)>=strong ||
                            getRed(exCopy, j-1,i)>=strong ||
                            getRed(exCopy, j-1, i+1)>=strong ||
                            getRed(exCopy, j, i)>=strong ||
                            getRed(exCopy, j, i)>=strong ||
                            getRed(exCopy, j+1, i-1)>=strong ||
                            getRed(exCopy, j+1, i)>=strong ||
                            getRed(exCopy, j+1, i+1)>=strong
                    ){
//                        extended.setRGB(j,i,new Color(255,255,255).getRGB());
                        exCopy.setRGB(j,i,high);
                    }else
//                        extended.setRGB(j,i,0);
                        exCopy.setRGB(j,i,0);
                }
            }
        }

//        image.getGraphics().drawImage(extended.getSubimage(1,1,width,height),0,0,null);
        image.getGraphics().drawImage(exCopy.getSubimage(1,1,width,height),0,0,null);
//        setImage(exCopy.getSubimage(1,1,width,height));
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

    public void setSobelDirection(Double[][] sobelDirection) {
        this.sobelDirection = sobelDirection;
    }
}
