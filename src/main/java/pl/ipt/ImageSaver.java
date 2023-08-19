package pl.ipt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

public class ImageSaver {
    public static void saveImageAs (BufferedImage image, String fileName, String format){
        try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Results/" + fileName + "." + format)) {
            ImageIO.write(image, format, fileOutputStream);
        } catch (Exception e) {
            System.out.println("ERROR: Unable to save the image");
        }
    }

    public static void saveImageAs (BufferedImage image, String filename){
        saveImageAs(image, filename, "jpg");
    }
}
