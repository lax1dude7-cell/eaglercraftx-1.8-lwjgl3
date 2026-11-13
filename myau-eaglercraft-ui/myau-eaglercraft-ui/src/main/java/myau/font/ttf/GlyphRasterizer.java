package myau.font.ttf;

/** Scanline anti-aliased rasterizer for TrueType outlines (non-zero winding, 4x vertical oversampling, exact horizontal coverage). */
public final class GlyphRasterizer {
    public static final class Bitmap {
        public int w, h;
        /** Offset of the bitmap's top-left corner from the pen origin on the baseline, in pixels (y is DOWN, so offY is usually negative). */
        public int offX, offY;
        public byte[] alpha;
    }

    private static final int SS = 4;

    private float[] ex0 = new float[256], ey0 = new float[256], ex1 = new float[256], ey1 = new float[256];
    private int[] edir = new int[256];
    private int ne;

    private void edge(float x0, float y0, float x1, float y1) {
        if (y0 == y1) return;
        if (ne == ex0.length) {
            int n = ne * 2;
            ex0 = java.util.Arrays.copyOf(ex0, n); ey0 = java.util.Arrays.copyOf(ey0, n);
            ex1 = java.util.Arrays.copyOf(ex1, n); ey1 = java.util.Arrays.copyOf(ey1, n);
            edir = java.util.Arrays.copyOf(edir, n);
        }
        if (y0 < y1) { ex0[ne] = x0; ey0[ne] = y0; ex1[ne] = x1; ey1[ne] = y1; edir[ne] = 1; }
        else { ex0[ne] = x1; ey0[ne] = y1; ex1[ne] = x0; ey1[ne] = y0; edir[ne] = -1; }
        ne++;
    }

    private void quad(float x0, float y0, float cx, float cy, float x1, float y1) {
        float dev = Math.abs(x0 - 2 * cx + x1) + Math.abs(y0 - 2 * cy + y1);
        int steps = Math.max(2, Math.min(16, (int) (Math.sqrt(dev) * 2) + 2));
        float px = x0, py = y0;
        for (int i = 1; i <= steps; i++) {
            float t = i / (float) steps, u = 1 - t;
            float nx = u * u * x0 + 2 * u * t * cx + t * t * x1;
            float ny = u * u * y0 + 2 * u * t * cy + t * t * y1;
            edge(px, py, nx, ny);
            px = nx; py = ny;
        }
    }

    /** @param scale pixels per font unit */
    public Bitmap rasterize(TTFFont.Outline o, float scale) {
        if (o == null || o.n == 0) return null;
        ne = 0;
        int start = 0;
        for (int c = 0; c < o.ends.length; c++) {
            int end = o.ends[c];
            int n = end - start + 1;
            if (n >= 2) contour(o, start, n, scale);
            start = end + 1;
        }
        if (ne == 0) return null;

        float minX = Float.MAX_VALUE, maxX = -Float.MAX_VALUE, minY = Float.MAX_VALUE, maxY = -Float.MAX_VALUE;
        for (int i = 0; i < ne; i++) {
            minX = Math.min(minX, Math.min(ex0[i], ex1[i]));
            maxX = Math.max(maxX, Math.max(ex0[i], ex1[i]));
            minY = Math.min(minY, ey0[i]);
            maxY = Math.max(maxY, ey1[i]);
        }
        int left = (int) Math.floor(minX) - 1, top = (int) Math.floor(minY) - 1;
        int w = (int) Math.ceil(maxX) - left + 1, h = (int) Math.ceil(maxY) - top + 1;
        if (w <= 0 || h <= 0 || w > 256 || h > 256) return null;

        float[] slope = new float[ne];
        for (int i = 0; i < ne; i++) slope[i] = (ex1[i] - ex0[i]) / (ey1[i] - ey0[i]);

        byte[] out = new byte[w * h];
        float[] cov = new float[w];
        float[] cx = new float[ne];
        int[] cd = new int[ne];
        for (int row = 0; row < h; row++) {
            java.util.Arrays.fill(cov, 0f);
            for (int s = 0; s < SS; s++) {
                float sy = top + row + (s + 0.5f) / SS;
                int nc = 0;
                for (int e = 0; e < ne; e++) {
                    if (sy >= ey0[e] && sy < ey1[e]) {
                        cx[nc] = ex0[e] + (sy - ey0[e]) * slope[e] - left;
                        cd[nc] = edir[e];
                        nc++;
                    }
                }
                for (int i = 1; i < nc; i++) { // insertion sort
                    float kx = cx[i]; int kd = cd[i]; int j = i - 1;
                    while (j >= 0 && cx[j] > kx) { cx[j + 1] = cx[j]; cd[j + 1] = cd[j]; j--; }
                    cx[j + 1] = kx; cd[j + 1] = kd;
                }
                int wind = 0; float sx = 0;
                for (int i = 0; i < nc; i++) {
                    int prev = wind;
                    wind += cd[i];
                    if (prev == 0 && wind != 0) sx = cx[i];
                    else if (prev != 0 && wind == 0) span(cov, w, sx, cx[i]);
                }
            }
            for (int i = 0; i < w; i++) {
                int a = Math.round(Math.min(1f, cov[i] / SS) * 255f);
                out[row * w + i] = (byte) a;
            }
        }
        Bitmap b = new Bitmap();
        b.w = w; b.h = h; b.offX = left; b.offY = top; b.alpha = out;
        return b;
    }

    private static void span(float[] cov, int w, float a, float b) {
        if (a < 0) a = 0;
        if (b > w) b = w;
        if (b <= a) return;
        int ia = (int) a, ib = (int) b;
        if (ia == ib) { cov[ia] += b - a; return; }
        cov[ia] += (ia + 1) - a;
        for (int i = ia + 1; i < ib; i++) cov[i] += 1f;
        if (ib < w) cov[ib] += b - ib;
    }

    private void contour(TTFFont.Outline o, int s, int n, float scale) {
        float[] px = new float[n * 2], py = new float[n * 2];
        boolean[] pon = new boolean[n * 2];
        int m = 0;
        for (int i = 0; i < n; i++) {
            int a = s + i, b = s + (i + 1) % n;
            float ax = o.x[a] * scale, ay = -o.y[a] * scale;
            float bx = o.x[b] * scale, by = -o.y[b] * scale;
            px[m] = ax; py[m] = ay; pon[m] = o.on[a]; m++;
            if (!o.on[a] && !o.on[b]) { px[m] = (ax + bx) / 2; py[m] = (ay + by) / 2; pon[m] = true; m++; }
        }
        int k = -1;
        for (int i = 0; i < m; i++) if (pon[i]) { k = i; break; }
        if (k < 0) return;
        float cx = px[k], cy = py[k];
        int i = 1;
        while (i <= m) {
            int idx = (k + i) % m;
            if (pon[idx]) {
                edge(cx, cy, px[idx], py[idx]);
                cx = px[idx]; cy = py[idx];
                i++;
            } else {
                int nidx = (k + i + 1) % m;
                quad(cx, cy, px[idx], py[idx], px[nidx], py[nidx]);
                cx = px[nidx]; cy = py[nidx];
                i += 2;
            }
        }
    }
}
