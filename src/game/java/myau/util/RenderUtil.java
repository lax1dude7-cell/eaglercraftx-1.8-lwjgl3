package myau.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.client.renderer.Tessellator;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

/** Only uses vanilla Tessellator QUADS, so it works on every Eaglercraft runtime. Has a Y clip (replaces GL scissor). */
public final class RenderUtil {
    public static float clipTop = -100000f, clipBottom = 100000f;

    private RenderUtil() {}

    public static void setClip(float top, float bottom) { clipTop = top; clipBottom = bottom; }
    public static void clearClip() { clipTop = -100000f; clipBottom = 100000f; }

    public static void drawRect(float x1, float y1, float x2, float y2, int argb) {
        if (x2 < x1) { float t = x1; x1 = x2; x2 = t; }
        if (y2 < y1) { float t = y1; y1 = y2; y2 = t; }
        y1 = Math.max(y1, clipTop);
        y2 = Math.min(y2, clipBottom);
        if (y2 <= y1 || x2 <= x1) return;
        int a = (argb >>> 24) & 255; if (a == 0) a = 255;
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
        GlStateManager.color(((argb >> 16) & 255) / 255f, ((argb >> 8) & 255) / 255f, (argb & 255) / 255f, a / 255f);
        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION);
        wr.pos(x1, y2, 0).endVertex();
        wr.pos(x2, y2, 0).endVertex();
        wr.pos(x2, y1, 0).endVertex();
        wr.pos(x1, y1, 0).endVertex();
        t.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /** Rounded rect built from half-unit rows (no triangle fans needed). */
    public static void drawRoundedRect(float x, float y, float w, float h, float r, int argb) {
        r = Math.min(r, Math.min(w, h) / 2f);
        if (r <= 0.5f) { drawRect(x, y, x + w, y + h, argb); return; }
        float step = 0.5f;
        drawRect(x, y + r, x + w, y + h - r, argb);
        for (float dy = 0; dy < r; dy += step) {
            float cy = r - dy - step / 2f;
            float inset = r - (float) Math.sqrt(Math.max(0f, r * r - cy * cy));
            drawRect(x + inset, y + dy, x + w - inset, y + dy + step, argb);
            drawRect(x + inset, y + h - dy - step, x + w - inset, y + h - dy, argb);
        }
    }

    public static void drawHead(ResourceLocation skin, int x, int y, int size) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(skin);
        GlStateManager.enableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
        Gui.drawScaledCustomSizeModalRect(x, y, 8f, 8f, 8, 8, size, size, 64f, 64f);
        Gui.drawScaledCustomSizeModalRect(x, y, 40f, 8f, 8, 8, size, size, 64f, 64f);
        GlStateManager.disableBlend();
    }

    // ------------------------------------------------------------------ 3D (Render3DEvent) helpers
    private static Frustum cameraFrustum = new Frustum();

    public static double lerpDouble(double current, double previous, double t) {
        return previous + (current - previous) * t;
    }

    public static void enableRenderState() {
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.disableCull();
        GlStateManager.disableAlpha();
        GlStateManager.disableDepth();
    }

    public static void disableRenderState() {
        GlStateManager.enableDepth();
        GlStateManager.enableAlpha();
        GlStateManager.enableCull();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    private static void face(WorldRenderer wr, double x0, double y0, double z0, double x1, double y1, double z1, double x2, double y2, double z2, double x3, double y3, double z3, int r, int g, int b, int a) {
        wr.pos(x0, y0, z0).color(r, g, b, a).endVertex();
        wr.pos(x1, y1, z1).color(r, g, b, a).endVertex();
        wr.pos(x2, y2, z2).color(r, g, b, a).endVertex();
        wr.pos(x3, y3, z3).color(r, g, b, a).endVertex();
    }

    /** Translucent filled box (alpha 63, same as OpenMyau+). */
    public static void drawFilledBox(AxisAlignedBB bb, int red, int green, int blue) {
        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(7, DefaultVertexFormats.POSITION_COLOR);
        int a = 63;
        face(wr, bb.minX, bb.minY, bb.minZ, bb.minX, bb.minY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.minY, bb.minZ, red, green, blue, a);
        face(wr, bb.minX, bb.maxY, bb.minZ, bb.minX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.minZ, red, green, blue, a);
        face(wr, bb.minX, bb.minY, bb.minZ, bb.minX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, bb.maxX, bb.minY, bb.minZ, red, green, blue, a);
        face(wr, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, red, green, blue, a);
        face(wr, bb.minX, bb.minY, bb.minZ, bb.minX, bb.maxY, bb.minZ, bb.minX, bb.maxY, bb.maxZ, bb.minX, bb.minY, bb.maxZ, red, green, blue, a);
        face(wr, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, bb.maxX, bb.minY, bb.maxZ, red, green, blue, a);
        t.draw();
    }

    /** Outline box. lineWidth is ignored on WebGL (always 1px). */
    public static void drawBoundingBox(AxisAlignedBB bb, int red, int green, int blue, int alpha, float lineWidth) {
        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(1, DefaultVertexFormats.POSITION_COLOR);
        edge(wr, bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.minZ, red, green, blue, alpha);
        edge(wr, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.minY, bb.maxZ, red, green, blue, alpha);
        edge(wr, bb.maxX, bb.minY, bb.maxZ, bb.minX, bb.minY, bb.maxZ, red, green, blue, alpha);
        edge(wr, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.minY, bb.minZ, red, green, blue, alpha);
        edge(wr, bb.minX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, red, green, blue, alpha);
        edge(wr, bb.maxX, bb.maxY, bb.minZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
        edge(wr, bb.maxX, bb.maxY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, red, green, blue, alpha);
        edge(wr, bb.minX, bb.maxY, bb.maxZ, bb.minX, bb.maxY, bb.minZ, red, green, blue, alpha);
        edge(wr, bb.minX, bb.minY, bb.minZ, bb.minX, bb.maxY, bb.minZ, red, green, blue, alpha);
        edge(wr, bb.maxX, bb.minY, bb.minZ, bb.maxX, bb.maxY, bb.minZ, red, green, blue, alpha);
        edge(wr, bb.maxX, bb.minY, bb.maxZ, bb.maxX, bb.maxY, bb.maxZ, red, green, blue, alpha);
        edge(wr, bb.minX, bb.minY, bb.maxZ, bb.minX, bb.maxY, bb.maxZ, red, green, blue, alpha);
        t.draw();
    }

    private static void edge(WorldRenderer wr, double x1, double y1, double z1, double x2, double y2, double z2,
            int red, int green, int blue, int alpha) {
        wr.pos(x1, y1, z1).color(red, green, blue, alpha).endVertex();
        wr.pos(x2, y2, z2).color(red, green, blue, alpha).endVertex();
    }

    /** Horizontal circle (line loop) in camera-relative coordinates. */
    public static void drawCircle(double cx, double cy, double cz, double radius, int segments, int color) {
        int a = (color >>> 24) & 255; if (a == 0) a = 255;
        int r = (color >> 16) & 255, g = (color >> 8) & 255, b = color & 255;
        Tessellator t = Tessellator.getInstance();
        WorldRenderer wr = t.getWorldRenderer();
        wr.begin(3, DefaultVertexFormats.POSITION_COLOR);
        for (int i = 0; i <= segments; i++) {
            double ang = (double) i * (Math.PI * 2.0 / (double) segments);
            wr.pos(cx + Math.cos(ang) * radius, cy, cz + Math.sin(ang) * radius).color(r, g, b, a).endVertex();
        }
        t.draw();
    }

    public static void drawEntityCircle(Entity e, double radius, int segments, int color) {
        float pt = Acc.getTimer(Minecraft.getMinecraft()).renderPartialTicks;
        net.minecraft.client.renderer.entity.RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        double x = lerpDouble(e.posX, e.lastTickPosX, pt) - rm.viewerPosX;
        double y = lerpDouble(e.posY, e.lastTickPosY, pt) - rm.viewerPosY + e.getEyeHeight();
        double z = lerpDouble(e.posZ, e.lastTickPosZ, pt) - rm.viewerPosZ;
        drawCircle(x, y, z, radius, segments, color);
    }

    public static void drawEntityBox(Entity e, int red, int green, int blue) {
        float pt = Acc.getTimer(Minecraft.getMinecraft()).renderPartialTicks;
        net.minecraft.client.renderer.entity.RenderManager rm = Minecraft.getMinecraft().getRenderManager();
        double x = lerpDouble(e.posX, e.lastTickPosX, pt);
        double y = lerpDouble(e.posY, e.lastTickPosY, pt);
        double z = lerpDouble(e.posZ, e.lastTickPosZ, pt);
        drawFilledBox(e.getEntityBoundingBox().expand(0.1f, 0.1f, 0.1f)
                .offset(x - e.posX, y - e.posY, z - e.posZ)
                .offset(-rm.viewerPosX, -rm.viewerPosY, -rm.viewerPosZ), red, green, blue);
    }

    public static boolean isInViewFrustum(AxisAlignedBB bb, double expand) {
        Entity v = Minecraft.getMinecraft().getRenderViewEntity();
        cameraFrustum.setPosition(v.posX, v.posY, v.posZ);
        return cameraFrustum.isBoundingBoxInFrustum(bb.expand(expand, expand, expand));
    }
}
