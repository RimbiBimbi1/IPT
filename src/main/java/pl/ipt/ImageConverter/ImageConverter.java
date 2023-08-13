package pl.ipt.ImageConverter;

import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.util.Arrays;
import java.util.List;

public class ImageConverter {
    public static int[] BufImg2IntArray(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();
        int[] result = new int[width * height];

        image.getRGB(
                0,
                0,
                width,
                height,
                result,
                0,
                width);
        return result;
    }

    public static Boolean[] BufImg2BoolArray(BufferedImage image) {
        int[] pixels = BufImg2IntArray(image);
        return (Boolean[]) Arrays.stream(pixels).mapToObj(i -> i != -1).toArray();
    }


    public static BufferedImage IntArray2BufImg(int[] pixels, int width, int height) {
        BufferedImage result = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
//        WritableRaster raster = result.getRaster();

//        int[] ar = new int[width*height];
//        raster.getPixels(0,0,width,height,ar);
//        raster.setPixels(0, 0, width, height, pixels);
        result.setRGB(0,0,width,height,pixels,0,width);
        return result;
    }

}
