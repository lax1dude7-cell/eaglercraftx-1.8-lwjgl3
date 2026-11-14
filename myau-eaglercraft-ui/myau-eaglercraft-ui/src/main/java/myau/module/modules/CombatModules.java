package myau.module.modules;

import myau.module.Category;
import myau.module.Module;
import myau.module.ModuleManager;

/** Registers every Combat module (ported from OpenMyau+). Default enabled states match the reference screenshot. */
public final class CombatModules {
    private CombatModules() {}

    private static void reg(ModuleManager mm, Module m, boolean on) {
        mm.register(m, Category.COMBAT);
        if (on) m.setEnabledSilently(true); // no onEnabled() call: there is no world at startup
    }

    public static void register(ModuleManager mm) {
        reg(mm, new AimAssist(), false);
        reg(mm, new AntiFireball(), false);
        reg(mm, new AutoClicker(), false);
        reg(mm, new BackTrack(), true);
        reg(mm, new BlockHit(), false);
        reg(mm, new ClickAssits(), false);
        reg(mm, new Criticals(), true);
        reg(mm, new HitBox(), false);
        reg(mm, new HitSelect(), false);
        reg(mm, new KillAura(), false);
        reg(mm, new LagRange(), false);
        reg(mm, new MoreKB(), false);
        reg(mm, new NoHitDelay(), true);
        reg(mm, new Reach(), false);
        reg(mm, new Refill(), false);
        reg(mm, new ServerLag(), false);
        reg(mm, new SprintReset(), true);
        reg(mm, new TargetStrafe(), false);
        reg(mm, new ThrowAura(), false);
        reg(mm, new Velocity(), false);
        reg(mm, new Wtap(), false);
        // placeholder other modules reference (replaced when the Misc category is ported)
        mm.register(new BedNuker(), Category.MISC);
    }
}
