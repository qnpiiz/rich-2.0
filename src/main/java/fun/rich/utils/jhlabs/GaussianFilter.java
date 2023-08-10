package fun.rich.utils.jhlabs;

import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public class GaussianFilter extends ConvolveFilter {

    protected float radius;
    protected Kernel kernel;

    public GaussianFilter() {
        this(2.0f);
    }

    public GaussianFilter(final float radius) {
        this.setRadius(radius);
    }

    public void setRadius(final float radius) {
        this.radius = radius;
        this.kernel = makeKernel(radius);
    }

    public float getRadius() {
        return this.radius;
    }

    @Override
    public BufferedImage filter(final BufferedImage src, BufferedImage dst) {
        final int width = src.getWidth();
        final int height = src.getHeight();
        if (dst == null) {
            dst = this.createCompatibleDestImage(src, null);
        }
        final int[] inPixels = new int[width * height];
        final int[] outPixels = new int[width * height];
        src.getRGB(0, 0, width, height, inPixels, 0, width);
        if (this.radius > 0.0f) {
            convolveAndTranspose(this.kernel, inPixels, outPixels, width, height, this.alpha,
                    this.alpha && this.premultiplyAlpha, false, CLAMP_EDGES);
            convolveAndTranspose(this.kernel, outPixels, inPixels, height, width, this.alpha, false,
                    this.alpha && this.premultiplyAlpha, CLAMP_EDGES);
        }
        dst.setRGB(0, 0, width, height, inPixels, 0, width);
        return dst;
    }

    public static void convolveAndTranspose(final Kernel kernel, final int[] inPixels, final int[] outPixels,
                                            final int width, final int height, final boolean alpha, final boolean premultiply,
                                            final boolean unpremultiply, final int edgeAction) {
        final float[] matrix = kernel.getKernelData(null);
        final int cols = kernel.getWidth();
        final int cols2 = cols / 2;
        for (int y = 0; y < height; ++y) {
            int index = y;
            final int ioffset = y * width;
            for (int x = 0; x < width; ++x) {
                float r = 0.0f;
                float g = 0.0f;
                float b = 0.0f;
                float a = 0.0f;
                final int moffset = cols2;
                for (int col = -cols2; col <= cols2; ++col) {
                    final float f = matrix[moffset + col];
                    if (f != 0.0f) {
                        int ix = x + col;
                        if (ix < 0) {
                            if (edgeAction == CLAMP_EDGES) {
                                ix = 0;
                            } else if (edgeAction == WRAP_EDGES) {
                                ix = (x + width) % width;
                            }
                        } else if (ix >= width) {
                            if (edgeAction == CLAMP_EDGES) {
                                ix = width - 1;
                            } else if (edgeAction == WRAP_EDGES) {
                                ix = (x + width) % width;
                            }
                        }
                        final int rgb = inPixels[ioffset + ix];
                        final int pa = rgb >> 24 & 0xFF;
                        int pr = rgb >> 16 & 0xFF;
                        int pg = rgb >> 8 & 0xFF;
                        int pb = rgb & 0xFF;
                        if (premultiply) {
                            final float a2 = pa * 0.003921569f;
                            pr *= (int) a2;
                            pg *= (int) a2;
                            pb *= (int) a2;
                        }
                        a += f * pa;
                        r += f * pr;
                        g += f * pg;
                        b += f * pb;
                    }
                }
                if (unpremultiply && a != 0.0f && a != 255.0f) {
                    final float f2 = 255.0f / a;
                    r *= f2;
                    g *= f2;
                    b *= f2;
                }
                final int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 255;
                final int ir = PixelUtils.clamp((int) (r + 0.5));
                final int ig = PixelUtils.clamp((int) (g + 0.5));
                final int ib = PixelUtils.clamp((int) (b + 0.5));
                outPixels[index] = (ia << 24 | ir << 16 | ig << 8 | ib);
                index += height;
            }
        }
    }

    public static Kernel makeKernel(final float radius) {
        final int r = (int) Math.ceil(radius);
        final int rows = r * 2 + 1;
        final float[] matrix = new float[rows];
        final float sigma = radius / 3.0f;
        final float sigma2 = 2.0f * sigma * sigma;
        final float sigmaPi2 = 6.2831855f * sigma;
        final float sqrtSigmaPi2 = (float) Math.sqrt(sigmaPi2);
        final float radius2 = radius * radius;
        float total = 0.0f;
        int index = 0;
        for (int row = -r; row <= r; ++row) {
            final float distance = (float) (row * row);
            if (distance > radius2) {
                matrix[index] = 0.0f;
            } else {
                matrix[index] = (float) Math.exp(-distance / sigma2) / sqrtSigmaPi2;
            }
            total += matrix[index];
            ++index;
        }
        for (int i = 0; i < rows; ++i) {
            final float[] array = matrix;
            final int n = i;
            array[n] /= total;
        }
        return new Kernel(rows, 1, matrix);
    }

    @Override
    public String toString() {
        return "Blur/Gaussian Blur...";
    }
}
