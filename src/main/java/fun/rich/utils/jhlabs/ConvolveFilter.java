package fun.rich.utils.jhlabs;

import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.ColorModel;
import java.awt.image.BufferedImage;
import java.awt.image.Kernel;

public class ConvolveFilter extends AbstractBufferedImageOp {

    public static int ZERO_EDGES;
    public static int CLAMP_EDGES;
    public static int WRAP_EDGES;
    protected Kernel kernel;
    protected boolean alpha;
    protected boolean premultiplyAlpha;
    private int edgeAction;

    static {
        ConvolveFilter.ZERO_EDGES = 0;
        ConvolveFilter.CLAMP_EDGES = 1;
        ConvolveFilter.WRAP_EDGES = 2;
    }

    public ConvolveFilter() {
        this(new float[9]);
    }

    public ConvolveFilter(final float[] matrix) {
        this(new Kernel(3, 3, matrix));
    }

    public ConvolveFilter(final int rows, final int cols, final float[] matrix) {
        this(new Kernel(cols, rows, matrix));
    }

    public ConvolveFilter(final Kernel kernel) {
        this.kernel = null;
        this.alpha = true;
        this.premultiplyAlpha = true;
        this.edgeAction = ConvolveFilter.CLAMP_EDGES;
        this.kernel = kernel;
    }

    public void setKernel(final Kernel kernel) {
        this.kernel = kernel;
    }

    public Kernel getKernel() {
        return this.kernel;
    }

    public void setEdgeAction(final int edgeAction) {
        this.edgeAction = edgeAction;
    }

    public int getEdgeAction() {
        return this.edgeAction;
    }

    public void setUseAlpha(final boolean useAlpha) {
        this.alpha = useAlpha;
    }

    public boolean getUseAlpha() {
        return this.alpha;
    }

    public void setPremultiplyAlpha(final boolean premultiplyAlpha) {
        this.premultiplyAlpha = premultiplyAlpha;
    }

    public boolean getPremultiplyAlpha() {
        return this.premultiplyAlpha;
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
        this.getRGB(src, 0, 0, width, height, inPixels);
        if (this.premultiplyAlpha) {
            ImageMath.premultiply(inPixels, 0, inPixels.length);
        }
        convolve(this.kernel, inPixels, outPixels, width, height, this.alpha, this.edgeAction);
        if (this.premultiplyAlpha) {
            ImageMath.unpremultiply(outPixels, 0, outPixels.length);
        }
        this.setRGB(dst, 0, 0, width, height, outPixels);
        return dst;
    }

    @Override
    public BufferedImage createCompatibleDestImage(final BufferedImage src, ColorModel dstCM) {
        if (dstCM == null) {
            dstCM = src.getColorModel();
        }
        return new BufferedImage(dstCM, dstCM.createCompatibleWritableRaster(src.getWidth(), src.getHeight()),
                dstCM.isAlphaPremultiplied(), null);
    }

    @Override
    public Rectangle2D getBounds2D(final BufferedImage src) {
        return new Rectangle(0, 0, src.getWidth(), src.getHeight());
    }

    @Override
    public Point2D getPoint2D(final Point2D srcPt, Point2D dstPt) {
        if (dstPt == null) {
            dstPt = new Point2D.Double();
        }
        dstPt.setLocation(srcPt.getX(), srcPt.getY());
        return dstPt;
    }

    @Override
    public RenderingHints getRenderingHints() {
        return null;
    }

    public static void convolve(final Kernel kernel, final int[] inPixels, final int[] outPixels, final int width,
                                final int height, final int edgeAction) {
        convolve(kernel, inPixels, outPixels, width, height, true, edgeAction);
    }

    public static void convolve(final Kernel kernel, final int[] inPixels, final int[] outPixels, final int width,
                                final int height, final boolean alpha, final int edgeAction) {
        if (kernel.getHeight() == 1) {
            convolveH(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
        } else if (kernel.getWidth() == 1) {
            convolveV(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
        } else {
            convolveHV(kernel, inPixels, outPixels, width, height, alpha, edgeAction);
        }
    }

    public static void convolveHV(final Kernel kernel, final int[] inPixels, final int[] outPixels, final int width,
                                  final int height, final boolean alpha, final int edgeAction) {
        int index = 0;
        final float[] matrix = kernel.getKernelData(null);
        final int rows = kernel.getHeight();
        final int cols = kernel.getWidth();
        final int rows2 = rows / 2;
        final int cols2 = cols / 2;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float r = 0.0f;
                float g = 0.0f;
                float b = 0.0f;
                float a = 0.0f;
                for (int row = -rows2; row <= rows2; ++row) {
                    final int iy = y + row;
                    int ioffset;
                    if (iy >= 0 && iy < height) {
                        ioffset = iy * width;
                    } else if (edgeAction == ConvolveFilter.CLAMP_EDGES) {
                        ioffset = y * width;
                    } else {
                        if (edgeAction != ConvolveFilter.WRAP_EDGES) {
                            continue;
                        }
                        ioffset = (iy + height) % height * width;
                    }
                    final int moffset = cols * (row + rows2) + cols2;
                    for (int col = -cols2; col <= cols2; ++col) {
                        final float f = matrix[moffset + col];
                        if (f != 0.0f) {
                            int ix = x + col;
                            if (ix < 0 || ix >= width) {
                                if (edgeAction == ConvolveFilter.CLAMP_EDGES) {
                                    ix = x;
                                } else {
                                    if (edgeAction != ConvolveFilter.WRAP_EDGES) {
                                        continue;
                                    }
                                    ix = (x + width) % width;
                                }
                            }
                            final int rgb = inPixels[ioffset + ix];
                            a += f * (rgb >> 24 & 0xFF);
                            r += f * (rgb >> 16 & 0xFF);
                            g += f * (rgb >> 8 & 0xFF);
                            b += f * (rgb & 0xFF);
                        }
                    }
                }
                final int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 255;
                final int ir = PixelUtils.clamp((int) (r + 0.5));
                final int ig = PixelUtils.clamp((int) (g + 0.5));
                final int ib = PixelUtils.clamp((int) (b + 0.5));
                outPixels[index++] = (ia << 24 | ir << 16 | ig << 8 | ib);
            }
        }
    }

    public static void convolveH(final Kernel kernel, final int[] inPixels, final int[] outPixels, final int width,
                                 final int height, final boolean alpha, final int edgeAction) {
        int index = 0;
        final float[] matrix = kernel.getKernelData(null);
        final int cols = kernel.getWidth();
        final int cols2 = cols / 2;
        for (int y = 0; y < height; ++y) {
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
                            if (edgeAction == ConvolveFilter.CLAMP_EDGES) {
                                ix = 0;
                            } else if (edgeAction == ConvolveFilter.WRAP_EDGES) {
                                ix = (x + width) % width;
                            }
                        } else if (ix >= width) {
                            if (edgeAction == ConvolveFilter.CLAMP_EDGES) {
                                ix = width - 1;
                            } else if (edgeAction == ConvolveFilter.WRAP_EDGES) {
                                ix = (x + width) % width;
                            }
                        }
                        final int rgb = inPixels[ioffset + ix];
                        a += f * (rgb >> 24 & 0xFF);
                        r += f * (rgb >> 16 & 0xFF);
                        g += f * (rgb >> 8 & 0xFF);
                        b += f * (rgb & 0xFF);
                    }
                }
                final int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 255;
                final int ir = PixelUtils.clamp((int) (r + 0.5));
                final int ig = PixelUtils.clamp((int) (g + 0.5));
                final int ib = PixelUtils.clamp((int) (b + 0.5));
                outPixels[index++] = (ia << 24 | ir << 16 | ig << 8 | ib);
            }
        }
    }

    public static void convolveV(final Kernel kernel, final int[] inPixels, final int[] outPixels, final int width,
                                 final int height, final boolean alpha, final int edgeAction) {
        int index = 0;
        final float[] matrix = kernel.getKernelData(null);
        final int rows = kernel.getHeight();
        final int rows2 = rows / 2;
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                float r = 0.0f;
                float g = 0.0f;
                float b = 0.0f;
                float a = 0.0f;
                for (int row = -rows2; row <= rows2; ++row) {
                    final int iy = y + row;
                    int ioffset;
                    if (iy < 0) {
                        if (edgeAction == ConvolveFilter.CLAMP_EDGES) {
                            ioffset = 0;
                        } else if (edgeAction == ConvolveFilter.WRAP_EDGES) {
                            ioffset = (y + height) % height * width;
                        } else {
                            ioffset = iy * width;
                        }
                    } else if (iy >= height) {
                        if (edgeAction == ConvolveFilter.CLAMP_EDGES) {
                            ioffset = (height - 1) * width;
                        } else if (edgeAction == ConvolveFilter.WRAP_EDGES) {
                            ioffset = (y + height) % height * width;
                        } else {
                            ioffset = iy * width;
                        }
                    } else {
                        ioffset = iy * width;
                    }
                    final float f = matrix[row + rows2];
                    if (f != 0.0f) {
                        final int rgb = inPixels[ioffset + x];
                        a += f * (rgb >> 24 & 0xFF);
                        r += f * (rgb >> 16 & 0xFF);
                        g += f * (rgb >> 8 & 0xFF);
                        b += f * (rgb & 0xFF);
                    }
                }
                final int ia = alpha ? PixelUtils.clamp((int) (a + 0.5)) : 255;
                final int ir = PixelUtils.clamp((int) (r + 0.5));
                final int ig = PixelUtils.clamp((int) (g + 0.5));
                final int ib = PixelUtils.clamp((int) (b + 0.5));
                outPixels[index++] = (ia << 24 | ir << 16 | ig << 8 | ib);
            }
        }
    }

    @Override
    public String toString() {
        return "Blur/Convolve...";
    }
}
