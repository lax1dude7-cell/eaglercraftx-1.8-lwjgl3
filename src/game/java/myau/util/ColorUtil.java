package myau.util;

/** java.awt.Color replacement (java.awt does not exist under TeaVM). */
public final class ColorUtil {
    private ColorUtil() {}

    public static int hsbToRGB(float hue, float sat, float bri) {
        int r = 0, g = 0, b = 0;
        if (sat == 0) {
            r = g = b = (int) (bri * 255f + 0.5f);
        } else {
            float h = (hue - (float) Math.floor(hue)) * 6f;
            float f = h - (float) Math.floor(h);
            float p = bri * (1f - sat), q = bri * (1f - sat * f), t = bri * (1f - sat * (1f - f));
            switch ((int) h) {
                case 0: r = c(bri); g = c(t); b = c(p); break;
                case 1: r = c(q); g = c(bri); b = c(p); break;
                case 2: r = c(p); g = c(bri); b = c(t); break;
                case 3: r = c(p); g = c(q); b = c(bri); break;
                case 4: r = c(t); g = c(p); b = c(bri); break;
                default: r = c(bri); g = c(p); b = c(q); break;
            }
        }
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }

    private static int c(float v) { return (int) (v * 255f + 0.5f); }

    public static float[] rgbToHSB(int rgb) {
        int r = (rgb >> 16) & 255, g = (rgb >> 8) & 255, b = rgb & 255;
        int max = Math.max(r, Math.max(g, b)), min = Math.min(r, Math.min(g, b));
        float bri = max / 255f;
        float sat = max == 0 ? 0 : (max - min) / (float) max;
        float hue;
        if (sat == 0) hue = 0;
        else {
            float rc = (max - r) / (float) (max - min), gc = (max - g) / (float) (max - min), bc = (max - b) / (float) (max - min);
            if (r == max) hue = bc - gc;
            else if (g == max) hue = 2f + rc - bc;
            else hue = 4f + gc - rc;
            hue /= 6f;
            if (hue < 0) hue += 1f;
        }
        return new float[]{hue, sat, bri};
    }

    public static Color scale(Color color, float scaleFactor, int alpha) {
        return new Color(Math.min(Math.max((int) ((float) color.getRed() * scaleFactor), 0), 255), Math.min(Math.max((int) ((float) color.getGreen() * scaleFactor), 0), 255), Math.min(Math.max((int) ((float) color.getBlue() * scaleFactor), 0), 255), alpha);
    }

    public static Color darker(Color color, float factor) {
        return scale(color, factor, color.getAlpha());
    }

    public static int rgb(int r, int g, int b) { return 0xFF000000 | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255); }

    public static int interpolate(float t, int c1, int c2) {
        t = Math.max(0f, Math.min(1f, t));
        int r = (int) (((c1 >> 16) & 255) + (((c2 >> 16) & 255) - ((c1 >> 16) & 255)) * t);
        int g = (int) (((c1 >> 8) & 255) + (((c2 >> 8) & 255) - ((c1 >> 8) & 255)) * t);
        int b = (int) ((c1 & 255) + ((c2 & 255) - (c1 & 255)) * t);
        return 0xFF000000 | (r << 16) | (g << 8) | b;
    }
}
