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
        // placeholders other modules reference (replaced when their categories are ported)
        mm.register(new Fly(), Category.MOVEMENT);
        mm.register(new Speed(), Category.MOVEMENT);
        mm.register(new NoSlow(), Category.MOVEMENT);
        mm.register(new LongJump(), Category.MOVEMENT);
        mm.register(new KeepSprint(), Category.MOVEMENT);
        mm.register(new Scaffold(), Category.PLAYER);
        mm.register(new AutoHeal(), Category.PLAYER);
        mm.register(new BedNuker(), Category.MISC);
    }
}
