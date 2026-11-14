package myau.util;

/** Minimal java.awt.Color replacement (java.awt does not exist under TeaVM). */
public class Color {
    public static final Color WHITE = new Color(255, 255, 255);
    public static final Color BLACK = new Color(0, 0, 0);
    public static final Color RED = new Color(255, 0, 0);
    public static final Color GREEN = new Color(0, 255, 0);
    public static final Color BLUE = new Color(0, 0, 255);
    public static final Color CYAN = new Color(0, 255, 255);
    private final int argb;

    public Color(int rgb) { this.argb = 0xFF000000 | rgb; }
    public Color(int rgba, boolean hasAlpha) { this.argb = hasAlpha ? rgba : (0xFF000000 | rgba); }
    public Color(int r, int g, int b) { this(r, g, b, 255); }
    public Color(int r, int g, int b, int a) {
        this.argb = ((a & 255) << 24) | ((r & 255) << 16) | ((g & 255) << 8) | (b & 255);
    }
    public Color(float r, float g, float b) { this((int) (r * 255f + 0.5f), (int) (g * 255f + 0.5f), (int) (b * 255f + 0.5f), 255); }
    public Color(float r, float g, float b, float a) { this((int) (r * 255f + 0.5f), (int) (g * 255f + 0.5f), (int) (b * 255f + 0.5f), (int) (a * 255f + 0.5f)); }

    public int getRGB() { return argb; }
    public int getRed() { return (argb >> 16) & 255; }
    public int getGreen() { return (argb >> 8) & 255; }
    public int getBlue() { return argb & 255; }
    public int getAlpha() { return (argb >>> 24) & 255; }

    public static int HSBtoRGB(float h, float s, float b) { return ColorUtil.hsbToRGB(h, s, b); }
    public static Color getHSBColor(float h, float s, float b) { return new Color(ColorUtil.hsbToRGB(h, s, b)); }
    public static float[] RGBtoHSB(int r, int g, int b, float[] out) {
        float[] v = ColorUtil.rgbToHSB((r << 16) | (g << 8) | b);
        if (out == null) return v;
        out[0] = v[0]; out[1] = v[1]; out[2] = v[2];
        return out;
    }
    public Color darker() { return new Color(Math.max((int) (getRed() * 0.7), 0), Math.max((int) (getGreen() * 0.7), 0), Math.max((int) (getBlue() * 0.7), 0), getAlpha()); }
    public Color brighter() { return new Color(Math.min((int) (getRed() / 0.7), 255), Math.min((int) (getGreen() / 0.7), 255), Math.min((int) (getBlue() / 0.7), 255), getAlpha()); }
}
