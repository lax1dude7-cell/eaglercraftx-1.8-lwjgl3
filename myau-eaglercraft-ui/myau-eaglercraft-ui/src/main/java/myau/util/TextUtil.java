package myau.util;

/** Small formatting helpers replacing Guava CaseFormat / DecimalFormat / String.format (not safe/portable under TeaVM). */
public final class TextUtil {
    private TextUtil() {}

    /** "LIGHT_BLUE" -> "LightBlue" (CaseFormat.UPPER_UNDERSCORE -> UPPER_CAMEL). */
    public static String camel(String s) {
        StringBuilder sb = new StringBuilder();
        boolean up = true;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == '_') { up = true; continue; }
            sb.append(up ? Character.toUpperCase(c) : Character.toLowerCase(c));
            up = false;
        }
        return sb.toString();
    }

    /** Rounds to at most maxDec decimals, keeps at least minDec ("0.0#" == fmt(v,1,2)). */
    public static String fmt(double v, int minDec, int maxDec) {
        boolean neg = v < 0;
        double a = Math.abs(v);
        double scale = Math.pow(10, maxDec);
        long r = Math.round(a * scale);
        long ip = r / (long) scale;
        long fp = r % (long) scale;
        String f = String.valueOf(fp);
        while (f.length() < maxDec) f = "0" + f;
        while (f.length() > minDec && f.endsWith("0")) f = f.substring(0, f.length() - 1);
        String out = (neg && r != 0 ? "-" : "") + ip + (f.length() > 0 ? "." + f : "");
        return out;
    }

    /** "+0.0;-0.0" style. */
    public static String signed(double v, int dec) {
        String s = fmt(v, dec, dec);
        return s.startsWith("-") ? s : "+" + s;
    }
}
