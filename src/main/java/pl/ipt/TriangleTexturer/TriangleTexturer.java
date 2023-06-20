package pl.ipt.TriangleTexturer;

import pl.ipt.Triangle.Triangle;

import java.awt.image.BufferedImage;

public class TriangleTexturer {
    private BufferedImage texture;
    private BufferedImage canvas;

    private final int textureW;
    private final int textureH;
    private final int canvasW;
    private final int canvasH;

    private Triangle from;
    private Triangle to;


    public TriangleTexturer(BufferedImage canvas, BufferedImage texture, Triangle from, Triangle to) {
        this.canvas = canvas;
        this.texture = texture;
        this.from = from;
        this.to = to;

        this.textureW = texture.getWidth();
        this.textureH = texture.getHeight();
        this.canvasW = canvas.getWidth();
        this.canvasH = canvas.getHeight();
    }


    public void texture() {
        int det = ((to.b.x - to.a.x) * (to.c.y - to.a.y))
                - ((to.b.y - to.a.y) * (to.c.x - to.a.x));

        for (int y = 0; y < canvasH; y++) {
            for (int x = 0; x < canvasW; x++) {
                double v = (double) ((
                        (x - to.a.x) * (to.c.y - to.a.y)
                ) - (
                        (y - to.a.y) * (to.c.x - to.a.x)
                )) / det;

                double w = (double) ((
                        (y - to.a.y) * (to.b.x - to.a.x)
                ) - (
                        (x - to.a.x) * (to.b.y - to.a.y)
                )) / det;

                double u = 1.0 - v - w;
                if (0 <= u && 1 >= u &&
                        0 <= v && 1 >= v &&
                        0 <= w && 1 >= w) {
                    double a = u * from.a.x +
                            v * from.b.x +
                            w * from.c.x;
                    double b = u * from.a.y +
                            v * from.b.y +
                            w * from.c.y;
                    double xT = Math.floor(a);
                    double yT = Math.floor(b);

                    a = a-xT;
                    b = b-yT;

                    if (xT>0 && xT<textureW &&
                    yT>0 && yT<textureH){
                        canvas.setRGB(x,y, texture.getRGB((int) xT, (int) yT));
                    }
                }
            }
        }

    }


    public BufferedImage getCanvas() {
        return canvas;
    }

    public void setCanvas(BufferedImage canvas) {
        this.canvas = canvas;
    }

    public BufferedImage getTexture() {
        return texture;
    }

    public void setTexture(BufferedImage texture) {
        this.texture = texture;
    }

    public Triangle getFrom() {
        return from;
    }

    public void setFrom(Triangle from) {
        this.from = from;
    }

    public Triangle getTo() {
        return to;
    }

    public void setTo(Triangle to) {
        this.to = to;
    }
}

