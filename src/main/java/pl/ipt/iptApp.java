package pl.ipt;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;
import pl.ipt.Converter.Converter;
import pl.ipt.Painter.Painter;

import javax.imageio.ImageIO;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;

import static org.opencv.core.CvType.CV_64F;


public class iptApp {
    static {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    }

    private static final String type = "jpg";


    public static void main(String[] args) {

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/".concat(type).concat("/1.").concat(type));
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.".concat(type));

            BufferedImage img = ImageIO.read(fileInputStream);
            Mat matIn = Converter.BufferedImage2Mat(img);





            Mat greyscale = new Mat();
            Imgproc.cvtColor(
                    matIn,
                    greyscale,
                    Imgproc.COLOR_RGB2GRAY);


//            float[] dstNormData = new float[(int) (cornersNorm.total() * cornersNorm.channels())];
//            cornersNorm.get(0, 0, dstNormData);
//
//            for (int i = 0; i < corners.rows(); i++) {
//                for (int j = 0; j < corners.cols(); j++) {
//                    if ((int) dstNormData[i * corners.cols() + j] > 127) {
//                        Imgproc.circle(matOut, new Point(j, i), 2, new Scalar(0), 5, 8, 0);
//                    }
//                }
//            }



            ;

//
//
            Mat blurred = new Mat();
            Imgproc.GaussianBlur(
                    greyscale,
                    blurred,
                    new Size(5, 5),
                    2
            );

            Mat sobelX = new Mat();
            Mat sobelY = new Mat();

            Imgproc.Sobel(
                    blurred,
                    sobelX,
                    CV_64F,
                    1,
                    0,
                    3
            );


            Imgproc.Sobel(
                    blurred,
                    sobelY,
                    CV_64F,
                    0,
                    1,
                    3
            );

            Mat sobelX2 = new Mat();
            Core.multiply(sobelX,sobelX,sobelX2);

            Mat sobelY2 = new Mat();
            Core.multiply(sobelY,sobelY,sobelY2);

            Mat sum = new Mat();
            Core.add(sobelX2,sobelY2,sum);

            Mat magnitude = new Mat();
            Core.sqrt(sum,magnitude);

            Mat angle = new MatOfFloat();

            Core.phase(sobelX,sobelY, angle, true);


//            System.out.println(angle.dump());

            BufferedImage magnitudeBI = Converter.Mat2BufferedImage(magnitude);

            int width = magnitudeBI.getWidth();
            int height = magnitudeBI.getHeight();

            BufferedImage result = new BufferedImage(width,height, BufferedImage.TYPE_INT_RGB);

            int max = new Color(255,255,255).getRGB();

            for (int i = 0; i < height; i++) {
                for (int j = 0; j < width; j++) {
                    int q = max;
                    int r = max;

                    Double deg = angle.at(Double.class,j,i).getV();

                    try {
                        if ((157.5 <= deg && deg <= 202.5) || (deg<=22.5) || (337.5 <= deg)) {;
                            q = magnitudeBI.getRGB(j, i+1);
                            r = magnitudeBI.getRGB(j,i-1);
                        } else if ((22.5 <= deg && deg < 67.5) || (202.5 <= deg && deg <= 247.5)) {
                            q = magnitudeBI.getRGB(j+1,i+1);
                            r = magnitudeBI.getRGB(j-1,i-1);
                        } else if ((67.5 <= deg && deg < 112.5) || (247.5 <= deg && deg < 292.5)) {
                            q = magnitudeBI.getRGB(j+1,i);
                            r = magnitudeBI.getRGB(j-1,i);
                        } else if ((112.5 <= deg && deg < 157.5) || (292.5 <= deg && deg <= 337.5)) {
                            q = magnitudeBI.getRGB(j+1,i-1);
                            r = magnitudeBI.getRGB(j-1,i+1);
                        }

//                        if ((0 <= deg && deg < 22.5) || (157.5 <= deg && deg <= 180)) {;
//                            q = magnitudeBI.getRGB(j, i+1);
//                            r = magnitudeBI.getRGB(j,i-1);
//                        } else if (22.5 <= deg && deg < 67.5) {
//                            q = magnitudeBI.getRGB(j+1,i-1);
//                            r = magnitudeBI.getRGB(j-1,i+1);
//                        } else if (67.5 <= deg && deg < 112.5) {
//                            q = magnitudeBI.getRGB(j+1,i);
//                            r = magnitudeBI.getRGB(j-1,i);
//                        } else if (112.5 <= deg && deg < 157.5) {
//                            q = magnitudeBI.getRGB(j-1,i-1);
//                            r = magnitudeBI.getRGB(j+1,i+1);
//                        }
                    } catch (Exception e) {
                        q = max;
                        r = max;
                    }

                    if (magnitudeBI.getRGB(j,i) >= r && magnitudeBI.getRGB(j,i) >= q) {
                        result.setRGB(j, i, magnitudeBI.getRGB(j,i));
                    } else {
                        result.setRGB(j, i, new Color(0,0,0).getRGB());
                    }
                }
            }

            Mat resultMat = Converter.BufferedImage2Mat(result);

            Mat masked = new Mat();

            Core.inRange(
                    resultMat,
                    Scalar.all(127),
                    Scalar.all(255),
                    masked
            );



            ImageIO.write(Converter.Mat2BufferedImage(masked), type, fileOutputStream);


//            Painter painter = new Painter(result);
//
//            painter.toGrayScale();
//            painter.applyGaussian();
//            painter.applySobel();
//            painter.applyNonMaxSuppression();
//            painter.applyDoubleThreshold();
//
//            ImageIO.write(painter.getImage(),type,fileOutputStream);


            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
