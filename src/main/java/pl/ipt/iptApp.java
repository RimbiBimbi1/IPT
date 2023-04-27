package pl.ipt;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import pl.ipt.Converter.Converter;
import pl.ipt.Painter.Painter;

import javax.imageio.ImageIO;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.io.*;




public class iptApp {
    static{ System.loadLibrary(Core.NATIVE_LIBRARY_NAME); }

    private static final String type ="jpg";


    public static void main( String[] args){

        try {
            FileInputStream fileInputStream = new FileInputStream("src/main/resources/".concat(type).concat("/1.").concat(type));
            FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/result.".concat(type));

            BufferedImage img = ImageIO.read(fileInputStream);
            BufferedImage result = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_INT_RGB);


            Mat matIn = Converter.BufferedImage2Mat(img);
            Mat matOut = Converter.BufferedImage2Mat(result);


            Imgproc.cvtColor(
                    matIn,
                    matOut,
                    Imgproc.COLOR_RGB2GRAY );

            matOut.copyTo(matIn);
            matOut = new Mat();
            Imgproc.GaussianBlur(
                    matIn,
                    matOut,
                    new Size(5,5),
                    1
            );

            matOut.copyTo(matIn);
            matOut = new Mat();
            Mat sobelX = new Mat();
            Mat sobelY = new Mat();

            Mat kernelX = new Mat(3,3, CvType.CV_32F){
                {
                    put(0,0,-1);
                    put(0,1,0);
                    put(0,2,1);

                    put(1,0,-2);
                    put(1,1,0);
                    put(1,2,2);

                    put(2,0,-1);
                    put(2,1,0);
                    put(2,2,1);
                }
            };

            Mat kernelY = new Mat(3,3, CvType.CV_32F){
                {
                    put(0,0,-1);
                    put(0,1,-2);
                    put(0,2,-1);

                    put(1,0,0);
                    put(1,1,0);
                    put(1,2,0);

                    put(2,0,1);
                    put(2,1,2);
                    put(2,2,1);
                }
            };



            Imgproc.filter2D(
                    matIn,
                    sobelX,
                    -1,
                    kernelX
            );


            Imgproc.filter2D(
                    matIn,
                    sobelY,
                    -1,
                    kernelY
            );

//            Imgproc.Sobel(
//                    matIn,
//                    sobelX,
//                    -1,
//                    1,
//                    0,
//                    3
//            );
//
//
//            Imgproc.Sobel(
//                    matIn,
//                    sobelY,
//                    -1,
//                    0,
//                    1,
//                    3
//            );


            Core.add(sobelX,sobelY,matOut);



            result = Converter.Mat2BufferedImage(matOut);



            ImageIO.write(result,type,fileOutputStream);



//            Painter painter = new Painter(img);
//
//            painter.toGrayScale();
//            painter.applyGaussian();
//            painter.applySobel();
//            painter.applyNonMaxSuppression();
//            painter.applyDoubleThreshold();

//            ImageIO.write(painter.getImage(),"png",fileOutputStream);


            fileInputStream.close();
            fileOutputStream.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
