package myau.util;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import net.lax1dude.eaglercraft.v1_8.opengl.EaglercraftGPU;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * EVERY engine-specific call that might differ between EaglercraftX forks lives here.
 * If something does not compile, fix it in this one file only.
 */
public final class Compat {
    private Compat() {}

    /** Reads a resource from the (epk) resource manager. */
    public static byte[] readResource(ResourceLocation loc) {
        try {
            InputStream in = Minecraft.getMinecraft().getResourceManager().getResource(loc).getInputStream();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] buf = new byte[8192];
            int n;
            while ((n = in.read(buf)) != -1) out.write(buf, 0, n);
            in.close();
            return out.toByteArray();
        } catch (Throwable t) {
            return null;
        }
    }

    /** Sets GL_LINEAR min/mag filtering on the given texture. VERIFY: EaglercraftGPU.glTexParameteri signature. */
    public static void setLinearFilter(int glTexture) {
        GlStateManager.bindTexture(glTexture);
        EaglercraftGPU.glTexParameteri(3553, 10241, 9729); // GL_TEXTURE_2D, MIN_FILTER, LINEAR
        EaglercraftGPU.glTexParameteri(3553, 10240, 9729); // GL_TEXTURE_2D, MAG_FILTER, LINEAR
    }

    /** VERIFY: EagRuntime.getStorage / setStorage (browser localStorage wrapper). */
    public static String storageGet(String key) {
        try {
            byte[] b = EagRuntime.getStorage(key);
            return b == null ? null : new String(b, "UTF-8");
        } catch (Throwable t) { return null; }
    }

    public static void storageSet(String key, String value) {
        try { EagRuntime.setStorage(key, value.getBytes("UTF-8")); } catch (Throwable t) { }
    }

    public static String keyName(int key) {
        if (key <= 0) return "NONE";
        try { return Keyboard.getKeyName(key); } catch (Throwable t) { return "KEY" + key; }
    }

    public static int eventWheel() { return Mouse.getEventDWheel(); }
}
