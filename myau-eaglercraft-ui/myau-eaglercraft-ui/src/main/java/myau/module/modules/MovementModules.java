package myau.module.modules;

import myau.module.Category;
import myau.module.Module;
import myau.module.ModuleManager;

/** Registers every Movement module (ported from OpenMyau+). Default enabled states match the reference screenshot. */
public final class MovementModules {
    private MovementModules() {}

    private static void reg(ModuleManager mm, Module m, boolean on) {
        mm.register(m, Category.MOVEMENT);
        if (on) m.setEnabledSilently(true); // no onEnabled() call: there is no world at startup
    }

    public static void register(ModuleManager mm) {
        reg(mm, new AntiAFK(), false);
        reg(mm, new AntiVoid(), false);
        reg(mm, new Blink(), false);
        reg(mm, new Eagle(), false);
        reg(mm, new FastBow(), true);
        reg(mm, new Fly(), false);
        reg(mm, new Jesus(), false);
        reg(mm, new KeepSprint(), false);
        reg(mm, new LongJump(), false);
        reg(mm, new NoFall(), false);
        reg(mm, new NoJumpDelay(), true);
        reg(mm, new NoSlow(), true);
        reg(mm, new SafeWalk(), false);
        reg(mm, new Speed(), false);
        reg(mm, new Sprint(), true);
        reg(mm, new Timer(), false);
    }
}
