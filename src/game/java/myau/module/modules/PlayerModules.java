package myau.module.modules;

import myau.module.Category;
import myau.module.Module;
import myau.module.ModuleManager;

/** Registers every Player module (ported from OpenMyau+). "Gapple" is OpenMyau+'s AutoGapple class. Defaults match the reference screenshot. */
public final class PlayerModules {
    private PlayerModules() {}

    private static void reg(ModuleManager mm, Module m, boolean on) {
        mm.register(m, Category.PLAYER);
        if (on) m.setEnabledSilently(true); // no onEnabled() call: there is no world at startup
    }

    public static void register(ModuleManager mm) {
        reg(mm, new AntiDebuff(), false);
        reg(mm, new AutoBlockIn(), false);
        reg(mm, new AutoHeal(), false);
        reg(mm, new AutoSwap(), false);
        reg(mm, new AutoTool(), true);
        reg(mm, new ChestStealer(), false);
        reg(mm, new FakeLag(), false);
        reg(mm, new FastPlace(), true);
        FlagDetector fd = new FlagDetector();
        fd.setHidden(false); // visible in the arraylist in the reference screenshot
        reg(mm, fd, true);
        reg(mm, new AutoGapple(), false);
        reg(mm, new GhostHand(), false);
        reg(mm, new InvManager(), true);
        reg(mm, new InvWalk(), true);
        reg(mm, new MCF(), false);
        reg(mm, new Scaffold(), false);
        reg(mm, new SpeedMine(), false);
    }
}
