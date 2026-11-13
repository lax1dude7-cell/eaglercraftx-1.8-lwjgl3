package myau.font;

import myau.font.ttf.GlyphRasterizer;
import myau.font.ttf.TTFFont;
import myau.util.Compat;
import myau.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

import java.util.Arrays;

/**
 * Renders a real .ttf at a given size. Glyphs are rasterised lazily by our own rasteriser into one atlas
 * (re-built automatically when the GUI scale changes so text is always pixel-crisp).
 * size = em size in GUI units.
 */
public final class TTFFontRenderer implements UIFont {
    private static final int ATLAS = 512;

    private static final class Glyph {
        float u0, v0, u1, v1;
        int offX, offY, w, h;
        float adv;
    }

    private final TTFFont font;
    private final float size;
    private final GlyphRasterizer rasterizer = new GlyphRasterizer();
    private final Glyph[] glyphs = new Glyph[65536];
    private final int[] atlas = new int[ATLAS * ATLAS];
    private DynamicTexture tex;
    private boolean dirty;
    private int scale = -1;
    private float rs; // raster scale: pixels per font unit
    private int penX, penY, rowH;

    public TTFFontRenderer(TTFFont font, float size) {
        this.font = font;
        this.size = size;
    }

    private void ensure() {
        int s = FontManager.getGuiScale();
        if (s != scale) {
            scale = s;
            rs = size * s / font.unitsPerEm;
            clear();
        }
    }

    private void clear() {
        Arrays.fill(glyphs, null);
        Arrays.fill(atlas, 0);
        penX = 1; penY = 1; rowH = 0;
        dirty = true;
    }

    private Glyph glyph(char c) {
        Glyph g = glyphs[c];
        if (g != null) return g;
        int gid = font.glyphIndex(c);
        if (gid == 0 && c != '?' && c > 32) return glyph('?');
        g = new Glyph();
        g.adv = font.advance(gid) * rs;
        if (c > 32) {
            GlyphRasterizer.Bitmap b = rasterizer.rasterize(font.getOutline(gid), rs);
            if (b != null) {
                if (penX + b.w + 1 > ATLAS) { penX = 1; penY += rowH + 1; rowH = 0; }
                if (penY + b.h + 1 > ATLAS) { clear(); }
                for (int y = 0; y < b.h; y++) {
                    for (int x = 0; x < b.w; x++) {
                        int a = b.alpha[y * b.w + x] & 0xFF;
                        atlas[(penY + y) * ATLAS + penX + x] = (a << 24) | 0x00FFFFFF;
                    }
                }
                g.w = b.w; g.h = b.h; g.offX = b.offX; g.offY = b.offY;
                g.u0 = penX / (float) ATLAS; g.v0 = penY / (float) ATLAS;
                g.u1 = (penX + b.w) / (float) ATLAS; g.v1 = (penY + b.h) / (float) ATLAS;
                penX += b.w + 1;
                rowH = Math.max(rowH, b.h);
                dirty = true;
            }
        }
        glyphs[c] = g;
        return g;
    }

    private void upload() {
        if (tex == null) tex = new DynamicTexture(ATLAS, ATLAS);
        System.arraycopy(atlas, 0, tex.getTextureData(), 0, atlas.length);
        tex.updateDynamicTexture();
        Compat.setLinearFilter(tex.getGlTextureId());
        dirty = false;
    }

    public float getStringWidth(String s) {
        if (s == null || s.length() == 0) return 0f;
        ensure();
        float w = 0;
        for (int i = 0; i < s.length(); i++) w += glyph(s.charAt(i)).adv;
        return w / scale;
    }

    public float getHeight() { ensure(); return font.capHeight * rs / scale; }

    public float getLineHeight() { ensure(); return (font.ascent - font.descent + font.lineGap) * rs / scale; }

    public float drawStringWithShadow(String s, float x, float y, int color) {
        ensure();
        int a = (color >>> 24) & 255; if (a == 0) a = 255;
        int sh = ((color & 0xFCFCFC) >> 2) | (a << 24);
        float o = 1f / scale;
        draw(s, x + o, y + o, sh);
        return draw(s, x, y, color);
    }

    public float drawString(String s, float x, float y, int color) { return draw(s, x, y, color); }

    private float draw(String s, float x, float y, int color) {
        if (s == null || s.length() == 0) return 0f;
        ensure();
        for (int i = 0; i < s.length(); i++) glyph(s.charAt(i));
        if (dirty || tex == null) upload();

        int a = (color >>> 24) & 255; if (a == 0) a = 255;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.enableTexture2D();
        GlStateManager.bindTexture(tex.getGlTextureId());
        GlStateManager.color(((color >> 16) & 255) / 255f, ((color >> 8) & 255) / 255f, (color & 255) / 255f, a / 255f);

        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_TEX);
        float pen = x * scale;
        float base = Math.round(y * scale + font.capHeight * rs);
        for (int i = 0; i < s.length(); i++) {
            Glyph g = glyph(s.charAt(i));
            if (g.w > 0) {
                float dx = Math.round(pen + g.offX), dy = base + g.offY;
                quad(wr, dx / scale, dy / scale, (dx + g.w) / scale, (dy + g.h) / scale, g);
            }
            pen += g.adv;
        }
        t.draw();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        return (pen - x * scale) / scale;
    }

    private static void quad(WorldRenderer wr, float x0, float y0, float x1, float y1, Glyph g) {
        float v0 = g.v0, v1 = g.v1;
        float ct = RenderUtil.clipTop, cb = RenderUtil.clipBottom;
        if (y1 <= ct || y0 >= cb) return;
        float h = y1 - y0;
        if (y0 < ct) { v0 += (ct - y0) / h * (g.v1 - g.v0); y0 = ct; }
        if (y1 > cb) { v1 -= (y1 - cb) / h * (g.v1 - g.v0); y1 = cb; }
        wr.pos(x0, y1, 0).tex(g.u0, v1).endVertex();
        wr.pos(x1, y1, 0).tex(g.u1, v1).endVertex();
        wr.pos(x1, y0, 0).tex(g.u1, v0).endVertex();
        wr.pos(x0, y0, 0).tex(g.u0, v0).endVertex();
    }
}
