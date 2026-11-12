package myau.font.ttf;

/**
 * Minimal pure-Java TrueType (glyf-based) parser. No java.awt, no reflection -> safe for TeaVM.
 * Supports: head, maxp, hhea, hmtx, cmap (formats 4 and 12), loca, glyf (simple + composite).
 * Does NOT support CFF-based .otf files (use a TrueType .ttf).
 */
public final class TTFFont {
    public static final class Outline {
        public float[] x = new float[0];
        public float[] y = new float[0];
        public boolean[] on = new boolean[0];
        public int[] ends = new int[0];
        public int n = 0;
    }

    private final byte[] d;
    public final int unitsPerEm, ascent, descent, lineGap, numGlyphs;
    public final int capHeight;
    private final int locaOff, glyfOff, hmtxOff, numHMetrics, locaFormat;
    private final int cmap4, cmap12;

    private TTFFont(byte[] data) {
        this.d = data;
        int base = 0;
        if (tag(0).equals("ttcf")) base = (int) u32(12);
        int numTables = u16(base + 4);
        int head = -1, maxp = -1, hhea = -1, hmtx = -1, cmap = -1, loca = -1, glyf = -1;
        for (int i = 0; i < numTables; i++) {
            int rec = base + 12 + i * 16;
            String t = tag(rec);
            int off = (int) u32(rec + 8);
            if (t.equals("head")) head = off;
            else if (t.equals("maxp")) maxp = off;
            else if (t.equals("hhea")) hhea = off;
            else if (t.equals("hmtx")) hmtx = off;
            else if (t.equals("cmap")) cmap = off;
            else if (t.equals("loca")) loca = off;
            else if (t.equals("glyf")) glyf = off;
        }
        if (head < 0 || maxp < 0 || hhea < 0 || hmtx < 0 || cmap < 0 || loca < 0 || glyf < 0) {
            throw new IllegalArgumentException("Not a TrueType (glyf) font - missing required tables");
        }
        unitsPerEm = u16(head + 18);
        locaFormat = i16(head + 50);
        numGlyphs = u16(maxp + 4);
        ascent = i16(hhea + 4);
        descent = i16(hhea + 6);
        lineGap = i16(hhea + 8);
        numHMetrics = u16(hhea + 34);
        hmtxOff = hmtx;
        locaOff = loca;
        glyfOff = glyf;

        int c4 = -1, c12 = -1, cAny4 = -1;
        int n = u16(cmap + 2);
        for (int i = 0; i < n; i++) {
            int rec = cmap + 4 + i * 8;
            int pid = u16(rec), eid = u16(rec + 2);
            int off = cmap + (int) u32(rec + 4);
            int fmt = u16(off);
            if (fmt == 12 && (pid == 3 && eid == 10 || pid == 0)) c12 = off;
            if (fmt == 4) {
                if (pid == 3 && eid == 1) c4 = off;
                else if (pid == 0 && cAny4 < 0) cAny4 = off;
            }
        }
        cmap12 = c12;
        cmap4 = c4 >= 0 ? c4 : cAny4;

        int h = glyphIndex('H');
        int cap = 0;
        if (h > 0) {
            int g = glyphOffset(h);
            if (g >= 0) cap = i16(g + 8);
        }
        capHeight = cap > 0 ? cap : (int) (unitsPerEm * 0.7f);
    }

    public static TTFFont parse(byte[] data) {
        return new TTFFont(data);
    }

    private int u8(int o) { return d[o] & 0xFF; }
    private int u16(int o) { return ((d[o] & 0xFF) << 8) | (d[o + 1] & 0xFF); }
    private int i16(int o) { return (short) u16(o); }
    private long u32(int o) { return ((long) u16(o) << 16) | u16(o + 2); }
    private String tag(int o) {
        return "" + (char) u8(o) + (char) u8(o + 1) + (char) u8(o + 2) + (char) u8(o + 3);
    }
    private float f2dot14(int o) { return i16(o) / 16384f; }

    public int glyphIndex(int cp) {
        if (cmap12 >= 0) {
            int ng = (int) u32(cmap12 + 12);
            for (int i = 0; i < ng; i++) {
                int g = cmap12 + 16 + i * 12;
                long s = u32(g), e = u32(g + 4);
                if (cp >= s && cp <= e) return (int) (u32(g + 8) + (cp - s));
            }
        }
        if (cmap4 >= 0 && cp <= 0xFFFF) {
            int segX2 = u16(cmap4 + 6);
            int endO = cmap4 + 14;
            int startO = endO + segX2 + 2;
            int deltaO = startO + segX2;
            int rangeO = deltaO + segX2;
            for (int i = 0; i < segX2; i += 2) {
                int end = u16(endO + i);
                if (cp <= end) {
                    int start = u16(startO + i);
                    if (cp < start) return 0;
                    int delta = i16(deltaO + i);
                    int ro = u16(rangeO + i);
                    if (ro == 0) return (cp + delta) & 0xFFFF;
                    int gi = u16(rangeO + i + ro + 2 * (cp - start));
                    return gi == 0 ? 0 : (gi + delta) & 0xFFFF;
                }
            }
        }
        return 0;
    }

    public float advance(int gid) {
        if (numHMetrics <= 0) return unitsPerEm * 0.5f;
        int g = gid < numHMetrics ? gid : numHMetrics - 1;
        return u16(hmtxOff + g * 4);
    }

    private int glyphOffset(int gid) {
        if (gid < 0 || gid >= numGlyphs) return -1;
        int a, b;
        if (locaFormat == 0) {
            a = u16(locaOff + gid * 2) * 2;
            b = u16(locaOff + gid * 2 + 2) * 2;
        } else {
            a = (int) u32(locaOff + gid * 4);
            b = (int) u32(locaOff + gid * 4 + 4);
        }
        if (a == b) return -1;
        return glyfOff + a;
    }

    public Outline getOutline(int gid) {
        return getOutline(gid, 0);
    }

    private Outline getOutline(int gid, int depth) {
        Outline o = new Outline();
        int g = glyphOffset(gid);
        if (g < 0 || depth > 6) return o;
        int nc = i16(g);
        if (nc >= 0) {
            int p = g + 10;
            int[] ends = new int[nc];
            for (int i = 0; i < nc; i++) { ends[i] = u16(p); p += 2; }
            int np = nc == 0 ? 0 : ends[nc - 1] + 1;
            int insLen = u16(p);
            p += 2 + insLen;
            int[] flags = new int[np];
            int i = 0;
            while (i < np) {
                int f = u8(p++);
                flags[i++] = f;
                if ((f & 8) != 0) {
                    int r = u8(p++);
                    while (r-- > 0 && i < np) flags[i++] = f;
                }
            }
            float[] xs = new float[np], ys = new float[np];
            boolean[] on = new boolean[np];
            int v = 0;
            for (i = 0; i < np; i++) {
                int f = flags[i];
                if ((f & 2) != 0) { int dx = u8(p++); v += (f & 16) != 0 ? dx : -dx; }
                else if ((f & 16) == 0) { v += i16(p); p += 2; }
                xs[i] = v;
                on[i] = (f & 1) != 0;
            }
            v = 0;
            for (i = 0; i < np; i++) {
                int f = flags[i];
                if ((f & 4) != 0) { int dy = u8(p++); v += (f & 32) != 0 ? dy : -dy; }
                else if ((f & 32) == 0) { v += i16(p); p += 2; }
                ys[i] = v;
            }
            o.x = xs; o.y = ys; o.on = on; o.ends = ends; o.n = np;
            return o;
        }
        // composite glyph
        int p = g + 10;
        int flags;
        do {
            flags = u16(p);
            int gi = u16(p + 2);
            p += 4;
            int a1, a2;
            if ((flags & 1) != 0) { a1 = i16(p); a2 = i16(p + 2); p += 4; }
            else { a1 = (byte) d[p]; a2 = (byte) d[p + 1]; p += 2; }
            float a = 1, b = 0, c = 0, dd = 1;
            if ((flags & 8) != 0) { a = dd = f2dot14(p); p += 2; }
            else if ((flags & 0x40) != 0) { a = f2dot14(p); dd = f2dot14(p + 2); p += 4; }
            else if ((flags & 0x80) != 0) { a = f2dot14(p); b = f2dot14(p + 2); c = f2dot14(p + 4); dd = f2dot14(p + 6); p += 8; }
            float dx = (flags & 2) != 0 ? a1 : 0, dy = (flags & 2) != 0 ? a2 : 0;
            Outline sub = getOutline(gi, depth + 1);
            append(o, sub, a, b, c, dd, dx, dy);
        } while ((flags & 0x20) != 0);
        return o;
    }

    private static void append(Outline dst, Outline s, float a, float b, float c, float d, float dx, float dy) {
        if (s.n == 0) return;
        int n0 = dst.n, nn = n0 + s.n;
        float[] x = new float[nn], y = new float[nn];
        boolean[] on = new boolean[nn];
        System.arraycopy(dst.x, 0, x, 0, n0);
        System.arraycopy(dst.y, 0, y, 0, n0);
        System.arraycopy(dst.on, 0, on, 0, n0);
        for (int i = 0; i < s.n; i++) {
            x[n0 + i] = a * s.x[i] + c * s.y[i] + dx;
            y[n0 + i] = b * s.x[i] + d * s.y[i] + dy;
            on[n0 + i] = s.on[i];
        }
        int[] ends = new int[dst.ends.length + s.ends.length];
        System.arraycopy(dst.ends, 0, ends, 0, dst.ends.length);
        for (int i = 0; i < s.ends.length; i++) ends[dst.ends.length + i] = s.ends[i] + n0;
        dst.x = x; dst.y = y; dst.on = on; dst.ends = ends; dst.n = nn;
    }
}
