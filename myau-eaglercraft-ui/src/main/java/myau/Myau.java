package myau;

import myau.font.FontManager;
import myau.module.Module;
import myau.module.ModuleManager;
import myau.module.modules.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public final class Myau {
    public static final String NAME = "Myau+";
    public static final String INFO_NAME = "OpenMyau";
    public static final String VERSION = "1.6+3";
    public static final String CREDIT = "dev, nespola";
    public static final long START_TIME = System.currentTimeMillis();

    /** Put your .ttf at  assets/minecraft/myau/fonts/main.ttf  (must be a TrueType/glyf font, not CFF .otf). */
    public static final ResourceLocation FONT_MAIN = new ResourceLocation("minecraft", "myau/fonts/main.ttf");

    public static ModuleManager moduleManager;
    private static boolean initialised;

    private Myau() {}

    /** Call once after the resource manager is ready (Minecraft.startGame, after fontRendererObj is created). */
    public static void init() {
        if (initialised) return;
        initialised = true;
        FontManager.register(FontManager.MAIN, FONT_MAIN);

        moduleManager = new ModuleManager();
        moduleManager.register(new HUD());
        moduleManager.register(new DynamicIsland());
        moduleManager.register(new ClickGUIModule());
        StubModules.register(moduleManager); // delete when real modules exist
    }

    /** Call at the END of GuiIngame.renderGameOverlay. */
    public static void onRender2D(float partialTicks) {
        if (!initialised) return;
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.gameSettings.hideGUI) return;
        ScaledResolution sr = new ScaledResolution(mc);
        FontManager.updateScale(sr);
        GlStateManager.disableDepth();
        GlStateManager.disableLighting();
        for (Module m : moduleManager.all()) if (m.isEnabled()) m.onRender2D(partialTicks, sr);
        GlStateManager.enableDepth();
        GlStateManager.color(1f, 1f, 1f, 1f);
    }

    /** Call when a key is pressed in-game (currentScreen == null). */
    public static void onKeyPress(int key) {
        if (!initialised || key <= 0) return;
        for (Module m : moduleManager.all()) if (m.getKey() == key) m.toggle();
    }

    public static void onTick() {
        if (!initialised) return;
        for (Module m : moduleManager.all()) if (m.isEnabled()) m.onTick();
    }
}
