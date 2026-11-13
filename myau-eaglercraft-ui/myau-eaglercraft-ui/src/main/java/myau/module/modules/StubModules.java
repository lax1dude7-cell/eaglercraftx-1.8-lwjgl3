package myau.module.modules;

import myau.module.Category;
import myau.module.ModuleManager;

/**
 * VISUAL-PARITY STUBS ONLY (no logic). Delete this file and its call in Myau.init once real modules exist.
 * Entry format: "Name" or "Name:suffix"; prefix '+' = enabled, '-' = enabled+hidden from arraylist, '~' = hidden only.
 */
public final class StubModules {
    private StubModules() {}

    private static final String[] COMBAT = {"AimAssist", "AntiFireball", "AutoClicker", "+BackTrack", "BlockHit", "ClickAssits", "+Criticals:tphop", "HitBox", "HitSelect", "KillAura", "LagRange", "MoreKB", "-NoHitDelay", "Reach", "Refill", "ServerLag", "+SprintReset", "TargetStrafe", "ThrowAura", "Velocity", "Wtap"};
    private static final String[] MOVEMENT = {"AntiAFK", "AntiVoid", "Blink", "Eagle", "+FastBow:instant", "Fly", "Jesus", "KeepSprint", "LongJump", "NoFall", "+NoJumpDelay:3", "+NoSlow", "SafeWalk", "Speed", "-Sprint", "Timer"};
    private static final String[] RENDER = {"+BedESP", "+Chams", "ChestESP", "+ESP", "FPScounter", "Fullbright", "+HitParticleEffects", "Indicators", "ItemESP", "NameTags", "NoHurtCam", "Radar", "SeasonDisplay", "-TargetHUD", "TeamHealthDisplay", "Tracers", "Trajectories", "ViewClip", "WaterMark", "WaterMark2", "Xray"};
    private static final String[] PLAYER = {"AntiDebuff", "AutoBlockIn", "AutoHeal", "AutoSwap", "+AutoTool", "ChestStealer", "FakeLag", "+FastPlace:1.9", "+FlagDetector", "Gapple", "GhostHand", "+InvManager", "+InvWalk:legit", "MCF", "Scaffold", "SpeedMine"};
    private static final String[] MISC = {"AntiObbyTrap", "AntiObfuscate", "AutoAnduril", "+AutoHypixel", "BedNuker", "BedTracker", "+ClientSpoofer", "Disabler", "InventoryClicker", "LightningTracker", "NickHider", "NoRotate", "Spammer"};

    private static void add(ModuleManager mm, Category c, String[] entries) {
        for (int i = 0; i < entries.length; i++) {
            String e = entries[i];
            boolean enabled = false, hidden = false;
            char f = e.charAt(0);
            if (f == '+') { enabled = true; e = e.substring(1); }
            else if (f == '-') { enabled = true; hidden = true; e = e.substring(1); }
            String[] suffix = new String[0];
            int colon = e.indexOf(':');
            if (colon >= 0) { suffix = new String[]{e.substring(colon + 1)}; e = e.substring(0, colon); }
            if (mm.has(e)) continue;
            mm.register(new StubModule(e, c, enabled, hidden, suffix));
        }
    }

    public static void register(ModuleManager mm) {
        add(mm, Category.COMBAT, COMBAT);
        add(mm, Category.MOVEMENT, MOVEMENT);
        add(mm, Category.RENDER, RENDER);
        add(mm, Category.PLAYER, PLAYER);
        add(mm, Category.MISC, MISC);
    }
}
