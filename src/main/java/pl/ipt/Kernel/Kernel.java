package pl.ipt.Kernel;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class Kernel {


    public static int[][] imagePixelNeighbourhood(BufferedImage image, int kSize) {
        int radius = kSize >> 1;
        int width = image.getWidth();
        int[][] offsets = new int[kSize][kSize];

        for (int i = 0; i < kSize; i++) {
            for (int j = 0; j < kSize; j++) {
                int xOffset = i - radius;
                int yOffset = j - radius;
                offsets[i][j] = width * yOffset + xOffset;
            }
        }
        return offsets;

    }

    public static int[] roundKernel(BufferedImage image, int kRadius) {
        int width = image.getWidth();
        int kSize = kRadius * 2 + 1;
        float radiusWithError = (float) (0.5 + kRadius);


        List<Integer> temp = new LinkedList<>();

        for (int y = -kRadius; y <= kRadius; y++) {
            for (int x = -kRadius; x <= kRadius; x++) {
                int yw = y * width;
                if ((y * y + x * x) <= (radiusWithError * radiusWithError)) {
                    temp.add(yw + x);
                }
            }
        }
        return temp.stream().mapToInt(i -> i).toArray();
    }

    public static ArrayList<Point> roundKernel(int kRadius) {
        float radiusWithError = (float) (0.5 + kRadius);

        ArrayList<Point> temp = new ArrayList<>();

        for (int y = -kRadius; y <= kRadius; y++) {
            for (int x = -kRadius; x <= kRadius; x++) {
                if ((x * x + y * y) <= (radiusWithError * radiusWithError)) {
                    temp.add(new Point(x, y));
                }
            }
        }
        return temp;
    }

}
