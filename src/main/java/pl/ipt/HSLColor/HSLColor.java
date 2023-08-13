package pl.ipt.HSLColor;

import java.awt.*;

public class HSLColor {
    private float h;  //hue
    private float s;  //saturation
    private float l;  //luminance


    public HSLColor(float h, float s, float l) {
        this.h = Math.min(360,Math.max(0,h));
        this.s = Math.min(100,Math.max(0,s));
        this.l = Math.min(100,Math.max(0,l));
    }

    public HSLColor(Color rgbColor){
        float[] hsl = rgb2hsl(rgbColor);
        this.h = hsl[0];
        this.s = hsl[1];
        this.l = hsl[2];
    }




    public float getH() {
        return h;
    }

    public void setH(float h) {
        this.h = h;
    }

    public float getS() {
        return s;
    }

    public void setS(float s) {
        this.s = s;
    }

    public float getL() {
        return l;
    }

    public void setL(float l) {
        this.l = l;
    }

    private float[] rgb2hsl(Color rgbColor){
        float red = rgbColor.getRed();
        float green = rgbColor.getGreen();
        float blue = rgbColor.getBlue();

        return rgb2hsl(red, green, blue);
    }

    public static float[] rgb2hsl(float r, float g, float b){
        float h; float s; float l;

        float M = Math.max(Math.max(r,g),b);
        float m = Math.min(Math.min(r,g),b);

        if (M == m)
            {h=0;}
        else if (M == r)
            h = ((60 * (g - b) / (M - m)) + 360) % 360;
        else if (M == g)
            h = (60 * (b - r) / (M - m)) + 120;
        else
            h = (60 * (r - g) / (M - m)) + 240;

        l = (float) ((M+m)/5.1);

        if (M == m)
            s = 0;
        else if (l <= 50)
            s = Math.min(100,100 * (M - m) / (M + m));
        else
            s = Math.min(100,100 * (M - m) / (2 - M - m));

        return new float[]{h,s,l};
    }

    public static float[] hsl2rgb(float h, float s, float l){
        if (s < 0 || s > 100){
            return new float[]{0,0,0};
        }
        if (l <0 || l > 100){
            return new float[]{0,0,0};
        }

        h /= 360.0;
        s /= 100.0;
        l /= 100.0;

        float q = 0;

        if (l < 0.5)
            q = l * (1 + s);
        else
            q = (l + s) - (s * l);

        float p = 2 * l - q;

        float r = Math.max(0, HueToRGB(p, q, h + (1.0f / 3.0f)));
        float g = Math.max(0, HueToRGB(p, q, h));
        float b = Math.max(0, HueToRGB(p, q, h - (1.0f / 3.0f)));

        r = Math.min(r, 1.0f);
        g = Math.min(g, 1.0f);
        b = Math.min(b, 1.0f);

        return new float[]{r,g,b};
    }

    private static float HueToRGB(float p, float q, float h)
    {
        if (h < 0) h += 1;
        if (h > 1 ) h -= 1;
        if (6 * h < 1)
        {
            return p + ((q - p) * 6 * h);
        }
        if (2 * h < 1 )
        {
            return  q;
        }
        if (3 * h < 2)
        {
            return p + ( (q - p) * 6 * ((2.0f / 3.0f) - h) );
        }
        return p;
    }

    public float[] getRGB(){
        return rgb2hsl(h, s, l);
    }

    public Color getColor(){
        float[] rgb = rgb2hsl(h,s,l);
        return new Color(rgb[0],rgb[1], rgb[2]);
    }
}
