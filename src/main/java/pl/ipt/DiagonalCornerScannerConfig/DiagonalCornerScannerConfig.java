package pl.ipt.DiagonalCornerScannerConfig;

public class DiagonalCornerScannerConfig {
    public int xStart;
    public int yStart;
    public int xEnd;
    public int yEnd;
    public int xJump;
    public int yJump;
    public int yStep;
    public int xStep;


    public DiagonalCornerScannerConfig() {
    }

    public DiagonalCornerScannerConfig(int corner, int width, int height) {
        switch (corner) {
            case 0 -> {
                this.xStart = 0;
                this.yStart = 0;

                this.xEnd = width;
                this.yEnd = height;

                this.xJump = 1;
                this.yJump = 1;

                this.xStep = -1;
                this.yStep = 1;
            }
            case 1 -> {
                this.xStart = width;
                this.yStart = 0;

                this.xEnd = 0;
                this.yEnd = height;

                this.xJump = 1;
                this.yJump = 1;

                this.xStep = -1;
                this.yStep = -1;
            }
            case 2 -> {
                this.xStart = width;
                this.yStart = height;

                this.xEnd = 0;
                this.yEnd = 0;

                this.xJump = -1;
                this.yJump = -1;

                this.xStep = 1;
                this.yStep = -1;
            }
            default -> {
                this.xStart = 0;
                this.yStart = height;

                this.xEnd = width;
                this.yEnd = 0;

                this.xJump = 1;
                this.yJump = -1;

                this.xStep = 1;
                this.yStep = 1;
            }
        }
    }
}


