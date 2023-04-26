package pl.ipt;

import org.opencv.core.Core;
import org.opencv.core.Mat;
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





            Imgproc.cvtColor(
                    Converter.BufferedImage2Mat(img),
                    Converter.BufferedImage2Mat(result),
                    Imgproc.COLOR_RGB2GRAY );

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
