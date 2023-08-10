package fun.rich.utils.math;

public class EasingHelper {

    public static double easeInSine(int n) {
        return 1.0D - Math.cos(n * Math.PI / 2.0D);
    }

    public static double easeOutSine(int n) {
        return Math.sin(n * Math.PI / 2.0D);
    }

    public static double easeInOutSine(int n) {
        return -(Math.cos(Math.PI * n) - 1.0D) / 2.0D;
    }

    public static double easeInCubic(int n) {
        return (n ^ 0x3);
    }

    public static double easeOutCubic(int n) {
        return 1.0D - Math.pow((1 - n), 3.0D);
    }

    public static double easeInOutCubic(int n) {
        return (n < 0.5D) ? (4 * n ^ 0x3) : (1.0D - Math.pow((-2 * n + 2), 3.0D) / 2.0D);
    }

    public static double easeInQuint(int n) {
        return (n ^ 0x5);
    }

    public static double easeOutQuint(int n) {
        return 1.0D - Math.pow((1 - n), 5.0D);
    }

    public static double easeInOutQuint(int n) {
        return (n < 0.5D) ? (16 * n ^ 0x5) : (1.0D - Math.pow((-2 * n + 2), 5.0D) / 2.0D);
    }

    public static double easeInCirc(int n) {
        return 1.0D - Math.sqrt(1.0D - Math.pow(n, 2.0D));
    }

    public static double easeOutCirc(int n) {
        return Math.sqrt(1.0D - Math.pow((n - 1), 2.0D));
    }

    public static double easeInOutCirc(int n) {
        return (n < 0.5D) ? ((1.0D - Math.sqrt(1.0D - Math.pow((2 * n), 2.0D))) / 2.0D) : ((Math.sqrt(1.0D - Math.pow((-2 * n + 2), 2.0D)) + 1.0D) / 2.0D);
    }

    public static double easeInElastic(int n) {
        double c4 = 2.0943951023931953D;
        return (n == 0) ? 0.0D : ((n == 1) ? 1.0D : (-Math.pow(2.0D, (10 * n - 10)) * Math.sin(((n * 10) - 10.75D) * 2.0943951023931953D)));
    }

    public static double easeOutElastic(int n) {
        double c4 = 2.0943951023931953D;
        return (n == 0) ? 0.0D : ((n == 1) ? 1.0D : (Math.pow(2.0D, (-10 * n - 10)) * Math.sin(((n * 10) - 0.75D) * 2.0943951023931953D) + 1.0D));
    }

    public static double easeInOutElastic(int n) {
        double c5 = 1.3962634015954636D;
        return (n == 0) ? 0.0D : ((n == 1) ? 1.0D : ((n < 0.5D) ? (-(Math.pow(2.0D, (20 * n - 10)) * Math.sin(((20 * n) - 11.125D) * 1.3962634015954636D)) / 2.0D) : (Math.pow(2.0D, (-20 * n + 10)) * Math.sin(((20 * n) - 11.125D) * 1.3962634015954636D) / 2.0D + 1.0D)));
    }

    public static double easeInQuad(int n) {
        return (n ^ 0x2);
    }

    public static double easeOutQuad(int n) {
        return (1 - (1 - n) * (1 - n));
    }

    public static double easeInOutQuad(int n) {
        return (n < 0.5D) ? (2 * n ^ 0x2) : (1.0D - Math.pow((-2 * n + 2), 2.0D) / 2.0D);
    }

    public static double easeInQuart(int n) {
        return (n ^ 0x4);
    }

    public static double easeOutQuart(float n) {
        return 1.0D - Math.pow((1.0F - n), 4.0D);
    }

    public static double easeInOutQuart(int n) {
        return (n < 0.5D) ? (8 * n ^ 0x4) : (1.0D - Math.pow((-2 * n + 2), 4.0D) / 2.0D);
    }

    public static double easeInExpo(int n) {
        return (n == 0) ? 0.0D : Math.pow(2.0D, (10 * n - 10));
    }

    public static double easeOutExpo(int n) {
        return (n == 1) ? 1.0D : (1.0D - Math.pow(2.0D, (-10 * n)));
    }

    public static double easeInOutExpo(int n) {
        return (n == 0) ? 0.0D : ((n == 1) ? 1.0D : ((n < 0.5D) ? (Math.pow(2.0D, (20 * n - 10)) / 2.0D) : ((2.0D - Math.pow(2.0D, (-20 * n + 10))) / 2.0D)));
    }

    public static double easeInBack(int n) {
        double c1 = 1.70158D;
        double c3 = 2.70158D;
        return 2.70158D * n * n * n - 1.70158D * n * n;
    }

    public static double easeOutBack(float n) {
        double c1 = 1.70158D;
        double c3 = 2.70158D;
        return 1.0D + 2.70158D * Math.pow((n - 1), 3.0D) + 1.70158D * Math.pow((n - 1), 2.0D);
    }

    public static double easeInOutBack(int n) {
        double c1 = 1.70158D;
        double c2 = 2.5949095D;
        return (n < 0.5D) ? (Math.pow((2 * n), 2.0D) * (7.189819D * n - 2.5949095D) / 2.0D) : ((Math.pow((2 * n - 2), 2.0D) * (3.5949095D * (n * 2 - 2) + 2.5949095D) + 2.0D) / 2.0D);
    }

    public static double easeOutBounce(int n) {
        double n1 = 7.5625D;
        double d1 = 2.75D;
        if (n < 0.36363636363636365D)
            return 7.5625D * n * n;
        if (n < 0.7272727272727273D)
            return 7.5625D * (n = (int)(n - 0.5454545454545454D)) * n + 0.75D;
        if (n < 0.9090909090909091D)
            return 7.5625D * (n = (int)(n - 0.8181818181818182D)) * n + 0.9375D;
        return 7.5625D * (n = (int)(n - 0.9545454545454546D)) * n + 0.984375D;
    }

    public static double easeInBounce(int n) {
        return 1.0D - easeOutBounce(1 - n);
    }

    public static double easeInOutBounce(int n) {
        return (n < 0.5D) ? ((1.0D - easeOutBounce(1 - 2 * n)) / 2.0D) : ((1.0D + easeOutBounce(2 * n - 1)) / 2.0D);
    }
}
