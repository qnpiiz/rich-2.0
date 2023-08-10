package fun.rich.utils.jhlabs;

public class ImageMath
{
    public static final float PI = 3.1415927f;
    public static final float HALF_PI = 1.5707964f;
    public static final float QUARTER_PI = 0.7853982f;
    public static final float TWO_PI = 6.2831855f;
    private static final float m00 = -0.5f;
    private static final float m01 = 1.5f;
    private static final float m02 = -1.5f;
    private static final float m03 = 0.5f;
    private static final float m10 = 1.0f;
    private static final float m11 = -2.5f;
    private static final float m12 = 2.0f;
    private static final float m13 = -0.5f;
    private static final float m20 = -0.5f;
    private static final float m21 = 0.0f;
    private static final float m22 = 0.5f;
    private static final float m23 = 0.0f;
    private static final float m30 = 0.0f;
    private static final float m31 = 1.0f;
    private static final float m32 = 0.0f;
    private static final float m33 = 0.0f;

    public static float bias(final float a, final float b) {
        return a / ((1.0f / b - 2.0f) * (1.0f - a) + 1.0f);
    }

    public static float gain(final float a, final float b) {
        final float c = (1.0f / b - 2.0f) * (1.0f - 2.0f * a);
        if (a < 0.5) {
            return a / (c + 1.0f);
        }
        return (c - a) / (c - 1.0f);
    }

    public static float step(final float a, final float x) {
        return (x < a) ? 0.0f : 1.0f;
    }

    public static float pulse(final float a, final float b, final float x) {
        return (x < a || x >= b) ? 0.0f : 1.0f;
    }

    public static float smoothPulse(final float a1, final float a2, final float b1, final float b2, float x) {
        if (x < a1 || x >= b2) {
            return 0.0f;
        }
        if (x < a2) {
            x = (x - a1) / (a2 - a1);
            return x * x * (3.0f - 2.0f * x);
        }
        if (x < b1) {
            return 1.0f;
        }
        x = (x - b1) / (b2 - b1);
        return 1.0f - x * x * (3.0f - 2.0f * x);
    }

    public static float smoothStep(final float a, final float b, float x) {
        if (x < a) {
            return 0.0f;
        }
        if (x >= b) {
            return 1.0f;
        }
        x = (x - a) / (b - a);
        return x * x * (3.0f - 2.0f * x);
    }

    public static float circleUp(float x) {
        x = 1.0f - x;
        return (float)Math.sqrt(1.0f - x * x);
    }

    public static float circleDown(final float x) {
        return 1.0f - (float)Math.sqrt(1.0f - x * x);
    }

    public static float clamp(final float x, final float a, final float b) {
        return (x < a) ? a : ((x > b) ? b : x);
    }

    public static int clamp(final int x, final int a, final int b) {
        return (x < a) ? a : ((x > b) ? b : x);
    }

    public static double mod(double a, final double b) {
        final int n = (int)(a / b);
        a -= n * b;
        if (a < 0.0) {
            return a + b;
        }
        return a;
    }

    public static float mod(float a, final float b) {
        final int n = (int)(a / b);
        a -= n * b;
        if (a < 0.0f) {
            return a + b;
        }
        return a;
    }

    public static int mod(int a, final int b) {
        final int n = a / b;
        a -= n * b;
        if (a < 0) {
            return a + b;
        }
        return a;
    }

    public static float triangle(final float x) {
        final float r = mod(x, 1.0f);
        return 2.0f * ((r < 0.5) ? r : (1.0f - r));
    }

    public static float lerp(final float t, final float a, final float b) {
        return a + t * (b - a);
    }

    public static int lerp(final float t, final int a, final int b) {
        return (int)(a + t * (b - a));
    }

    public static int mixColors(final float t, final int rgb1, final int rgb2) {
        int a1 = rgb1 >> 24 & 0xFF;
        int r1 = rgb1 >> 16 & 0xFF;
        int g1 = rgb1 >> 8 & 0xFF;
        int b1 = rgb1 & 0xFF;
        final int a2 = rgb2 >> 24 & 0xFF;
        final int r2 = rgb2 >> 16 & 0xFF;
        final int g2 = rgb2 >> 8 & 0xFF;
        final int b2 = rgb2 & 0xFF;
        a1 = lerp(t, a1, a2);
        r1 = lerp(t, r1, r2);
        g1 = lerp(t, g1, g2);
        b1 = lerp(t, b1, b2);
        return a1 << 24 | r1 << 16 | g1 << 8 | b1;
    }

    public static int bilinearInterpolate(final float x, final float y, final int nw, final int ne, final int sw, final int se) {
        final int a0 = nw >> 24 & 0xFF;
        final int r0 = nw >> 16 & 0xFF;
        final int g0 = nw >> 8 & 0xFF;
        final int b0 = nw & 0xFF;
        final int a2 = ne >> 24 & 0xFF;
        final int r2 = ne >> 16 & 0xFF;
        final int g2 = ne >> 8 & 0xFF;
        final int b2 = ne & 0xFF;
        final int a3 = sw >> 24 & 0xFF;
        final int r3 = sw >> 16 & 0xFF;
        final int g3 = sw >> 8 & 0xFF;
        final int b3 = sw & 0xFF;
        final int a4 = se >> 24 & 0xFF;
        final int r4 = se >> 16 & 0xFF;
        final int g4 = se >> 8 & 0xFF;
        final int b4 = se & 0xFF;
        final float cx = 1.0f - x;
        final float cy = 1.0f - y;
        float m0 = cx * a0 + x * a2;
        float m2 = cx * a3 + x * a4;
        final int a5 = (int)(cy * m0 + y * m2);
        m0 = cx * r0 + x * r2;
        m2 = cx * r3 + x * r4;
        final int r5 = (int)(cy * m0 + y * m2);
        m0 = cx * g0 + x * g2;
        m2 = cx * g3 + x * g4;
        final int g5 = (int)(cy * m0 + y * m2);
        m0 = cx * b0 + x * b2;
        m2 = cx * b3 + x * b4;
        final int b5 = (int)(cy * m0 + y * m2);
        return a5 << 24 | r5 << 16 | g5 << 8 | b5;
    }

    public static int brightnessNTSC(final int rgb) {
        final int r = rgb >> 16 & 0xFF;
        final int g = rgb >> 8 & 0xFF;
        final int b = rgb & 0xFF;
        return (int)(r * 0.299f + g * 0.587f + b * 0.114f);
    }

    public static float spline(float x, final int numKnots, final float[] knots) {
        final int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        x = clamp(x, 0.0f, 1.0f) * numSpans;
        int span = (int)x;
        if (span > numKnots - 4) {
            span = numKnots - 4;
        }
        x -= span;
        final float k0 = knots[span];
        final float k2 = knots[span + 1];
        final float k3 = knots[span + 2];
        final float k4 = knots[span + 3];
        final float c3 = -0.5f * k0 + 1.5f * k2 + -1.5f * k3 + 0.5f * k4;
        final float c4 = 1.0f * k0 + -2.5f * k2 + 2.0f * k3 + -0.5f * k4;
        final float c5 = -0.5f * k0 + 0.0f * k2 + 0.5f * k3 + 0.0f * k4;
        final float c6 = 0.0f * k0 + 1.0f * k2 + 0.0f * k3 + 0.0f * k4;
        return ((c3 * x + c4) * x + c5) * x + c6;
    }

    public static float spline(final float x, final int numKnots, final int[] xknots, final int[] yknots) {
        final int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        int span;
        for (span = 0; span < numSpans && xknots[span + 1] <= x; ++span) {}
        if (span > numKnots - 3) {
            span = numKnots - 3;
        }
        float t = (x - xknots[span]) / (xknots[span + 1] - xknots[span]);
        if (--span < 0) {
            span = 0;
            t = 0.0f;
        }
        final float k0 = (float)yknots[span];
        final float k2 = (float)yknots[span + 1];
        final float k3 = (float)yknots[span + 2];
        final float k4 = (float)yknots[span + 3];
        final float c3 = -0.5f * k0 + 1.5f * k2 + -1.5f * k3 + 0.5f * k4;
        final float c4 = 1.0f * k0 + -2.5f * k2 + 2.0f * k3 + -0.5f * k4;
        final float c5 = -0.5f * k0 + 0.0f * k2 + 0.5f * k3 + 0.0f * k4;
        final float c6 = 0.0f * k0 + 1.0f * k2 + 0.0f * k3 + 0.0f * k4;
        return ((c3 * t + c4) * t + c5) * t + c6;
    }

    public static int colorSpline(float x, final int numKnots, final int[] knots) {
        final int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        x = clamp(x, 0.0f, 1.0f) * numSpans;
        int span = (int)x;
        if (span > numKnots - 4) {
            span = numKnots - 4;
        }
        x -= span;
        int v = 0;
        for (int i = 0; i < 4; ++i) {
            final int shift = i * 8;
            final float k0 = (float)(knots[span] >> shift & 0xFF);
            final float k2 = (float)(knots[span + 1] >> shift & 0xFF);
            final float k3 = (float)(knots[span + 2] >> shift & 0xFF);
            final float k4 = (float)(knots[span + 3] >> shift & 0xFF);
            final float c3 = -0.5f * k0 + 1.5f * k2 + -1.5f * k3 + 0.5f * k4;
            final float c4 = 1.0f * k0 + -2.5f * k2 + 2.0f * k3 + -0.5f * k4;
            final float c5 = -0.5f * k0 + 0.0f * k2 + 0.5f * k3 + 0.0f * k4;
            final float c6 = 0.0f * k0 + 1.0f * k2 + 0.0f * k3 + 0.0f * k4;
            int n = (int)(((c3 * x + c4) * x + c5) * x + c6);
            if (n < 0) {
                n = 0;
            }
            else if (n > 255) {
                n = 255;
            }
            v |= n << shift;
        }
        return v;
    }

    public static int colorSpline(final int x, final int numKnots, final int[] xknots, final int[] yknots) {
        final int numSpans = numKnots - 3;
        if (numSpans < 1) {
            throw new IllegalArgumentException("Too few knots in spline");
        }
        int span;
        for (span = 0; span < numSpans && xknots[span + 1] <= x; ++span) {}
        if (span > numKnots - 3) {
            span = numKnots - 3;
        }
        float t = (x - xknots[span]) / (float)(xknots[span + 1] - xknots[span]);
        if (--span < 0) {
            span = 0;
            t = 0.0f;
        }
        int v = 0;
        for (int i = 0; i < 4; ++i) {
            final int shift = i * 8;
            final float k0 = (float)(yknots[span] >> shift & 0xFF);
            final float k2 = (float)(yknots[span + 1] >> shift & 0xFF);
            final float k3 = (float)(yknots[span + 2] >> shift & 0xFF);
            final float k4 = (float)(yknots[span + 3] >> shift & 0xFF);
            final float c3 = -0.5f * k0 + 1.5f * k2 + -1.5f * k3 + 0.5f * k4;
            final float c4 = 1.0f * k0 + -2.5f * k2 + 2.0f * k3 + -0.5f * k4;
            final float c5 = -0.5f * k0 + 0.0f * k2 + 0.5f * k3 + 0.0f * k4;
            final float c6 = 0.0f * k0 + 1.0f * k2 + 0.0f * k3 + 0.0f * k4;
            int n = (int)(((c3 * t + c4) * t + c5) * t + c6);
            if (n < 0) {
                n = 0;
            }
            else if (n > 255) {
                n = 255;
            }
            v |= n << shift;
        }
        return v;
    }

    public static void resample(final int[] source, final int[] dest, final int length, final int offset, final int stride, final float[] out) {
        int srcIndex = offset;
        int destIndex = offset;
        final int lastIndex = source.length;
        final float[] in = new float[length + 2];
        int i = 0;
        for (int j = 0; j < length; ++j) {
            while (out[i + 1] < j) {
                ++i;
            }
            in[j] = i + (j - out[i]) / (out[i + 1] - out[i]);
        }
        in[length] = (float)length;
        in[length + 1] = (float)length;
        float inSegment = 1.0f;
        float sizfac;
        float outSegment = sizfac = in[1];
        float bSum;
        float gSum;
        float aSum;
        float rSum = aSum = (gSum = (bSum = 0.0f));
        int rgb = source[srcIndex];
        int a = rgb >> 24 & 0xFF;
        int r = rgb >> 16 & 0xFF;
        int g = rgb >> 8 & 0xFF;
        int b = rgb & 0xFF;
        srcIndex += stride;
        rgb = source[srcIndex];
        int nextA = rgb >> 24 & 0xFF;
        int nextR = rgb >> 16 & 0xFF;
        int nextG = rgb >> 8 & 0xFF;
        int nextB = rgb & 0xFF;
        srcIndex += stride;
        i = 1;
        while (i <= length) {
            final float aIntensity = inSegment * a + (1.0f - inSegment) * nextA;
            final float rIntensity = inSegment * r + (1.0f - inSegment) * nextR;
            final float gIntensity = inSegment * g + (1.0f - inSegment) * nextG;
            final float bIntensity = inSegment * b + (1.0f - inSegment) * nextB;
            if (inSegment < outSegment) {
                aSum += aIntensity * inSegment;
                rSum += rIntensity * inSegment;
                gSum += gIntensity * inSegment;
                bSum += bIntensity * inSegment;
                outSegment -= inSegment;
                inSegment = 1.0f;
                a = nextA;
                r = nextR;
                g = nextG;
                b = nextB;
                if (srcIndex < lastIndex) {
                    rgb = source[srcIndex];
                }
                nextA = (rgb >> 24 & 0xFF);
                nextR = (rgb >> 16 & 0xFF);
                nextG = (rgb >> 8 & 0xFF);
                nextB = (rgb & 0xFF);
                srcIndex += stride;
            }
            else {
                aSum += aIntensity * outSegment;
                rSum += rIntensity * outSegment;
                gSum += gIntensity * outSegment;
                bSum += bIntensity * outSegment;
                dest[destIndex] = ((int)Math.min(aSum / sizfac, 255.0f) << 24 | (int)Math.min(rSum / sizfac, 255.0f) << 16 | (int)Math.min(gSum / sizfac, 255.0f) << 8 | (int)Math.min(bSum / sizfac, 255.0f));
                destIndex += stride;
                rSum = (aSum = (gSum = (bSum = 0.0f)));
                inSegment -= outSegment;
                outSegment = (sizfac = in[i + 1] - in[i]);
                ++i;
            }
        }
    }

    public static void premultiply(final int[] p, final int offset, int length) {
        length += offset;
        for (int i = offset; i < length; ++i) {
            final int rgb = p[i];
            final int a = rgb >> 24 & 0xFF;
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            final float f = a * 0.003921569f;
            r *= (int)f;
            g *= (int)f;
            b *= (int)f;
            p[i] = (a << 24 | r << 16 | g << 8 | b);
        }
    }

    public static void unpremultiply(final int[] p, final int offset, int length) {
        length += offset;
        for (int i = offset; i < length; ++i) {
            final int rgb = p[i];
            final int a = rgb >> 24 & 0xFF;
            int r = rgb >> 16 & 0xFF;
            int g = rgb >> 8 & 0xFF;
            int b = rgb & 0xFF;
            if (a != 0 && a != 255) {
                final float f = 255.0f / a;
                r *= (int)f;
                g *= (int)f;
                b *= (int)f;
                if (r > 255) {
                    r = 255;
                }
                if (g > 255) {
                    g = 255;
                }
                if (b > 255) {
                    b = 255;
                }
                p[i] = (a << 24 | r << 16 | g << 8 | b);
            }
        }
    }
}
