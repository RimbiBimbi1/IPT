package pl.ipt;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;

public class ImageSaver {
    private static int counter = 0;


    public static void saveImageAs (BufferedImage image, String fileName, String format){
        String counterStr = String.format("%03d", counter++);
        try (FileOutputStream fileOutputStream = new FileOutputStream("src/main/resources/Results/" + counterStr + " " + fileName + "." + format)) {
            ImageIO.write(image, format, fileOutputStream);
        } catch (Exception e) {
            System.out.println("ERROR: Unable to save the image");
        }
    }

    public static void saveImageAs (BufferedImage image, String filename){
        saveImageAs(image, filename, "jpg");
    }
}
