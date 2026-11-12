package myau.module.modules;

import myau.module.Category;
import myau.module.ModuleManager;

/**
 * VISUAL-PARITY STUBS ONLY. These have no logic - they are the module names/states from the reference screenshot
 * so the ClickGUI and arraylist can be checked 1:1. Delete this file (and its call in Myau.init) once real modules exist;
 * real modules simply replace the stub of the same name.
 */
public final class StubModules {
    private StubModules() {}

    private static void add(ModuleManager mm, Category c, String names, String enabled, String hidden, String... suffixPairs) {
        String en = "," + enabled + ",", hid = "," + hidden + ",";
        for (String n : names.split(",")) {
            if (mm.has(n)) continue;
            String suf = null;
            for (int i = 0; i + 1 < suffixPairs.length; i += 2) if (suffixPairs[i].equals(n)) suf = suffixPairs[i + 1];
            mm.register(suf == null
                    ? new StubModule(n, c, en.contains("," + n + ","), hid.contains("," + n + ","))
                    : new StubModule(n, c, en.contains("," + n + ","), hid.contains("," + n + ","), suf));
        }
    }

    public static void register(ModuleManager mm) {
        add(mm, Category.COMBAT,
                "AimAssist,AntiFireball,AutoClicker,BackTrack,BlockHit,ClickAssits,Criticals,HitBox,HitSelect,KillAura,LagRange,MoreKB,NoHitDelay,Reach,Refill,ServerLag,SprintReset,TargetStrafe,ThrowAura,Velocity,Wtap",
                "BackTrack,Criticals,NoHitDelay,SprintReset", "NoHitDelay", "Criticals", "tphop");
        add(mm, Category.MOVEMENT,
                "AntiAFK,AntiVoid,Blink,Eagle,FastBow,Fly,Jesus,KeepSprint,LongJump,NoFall,NoJumpDelay,NoSlow,SafeWalk,Speed,Sprint,Timer",
                "FastBow,NoJumpDelay,NoSlow,Sprint", "Sprint", "FastBow", "instant", "NoJumpDelay", "3");
        add(mm, Category.RENDER,
                "BedESP,Chams,ChestESP,ESP,FPScounter,Fullbright,HitParticleEffects,Indicators,ItemESP,NameTags,NoHurtCam,Radar,SeasonDisplay,TargetHUD,TeamHealthDisplay,Tracers,Trajectories,ViewClip,WaterMark,WaterMark2,Xray",
                "BedESP,Chams,ESP,HitParticleEffects,TargetHUD", "TargetHUD");
        add(mm, Category.PLAYER,
                "AntiDebuff,AutoBlockIn,AutoHeal,AutoSwap,AutoTool,ChestStealer,FakeLag,FastPlace,FlagDetector,Gapple,GhostHand,InvManager,InvWalk,MCF,Scaffold,SpeedMine",
                "AutoTool,FastPlace,FlagDetector,InvManager,InvWalk", "", "FastPlace", "1.9", "InvWalk", "legit");
        add(mm, Category.MISC,
                "AntiObbyTrap,AntiObfuscate,AutoAnduril,AutoHypixel,BedNuker,BedTracker,ClientSpoofer,Disabler,InventoryClicker,LightningTracker,NickHider,NoRotate,Spammer",
                "AutoHypixel,ClientSpoofer", "");
    }
}
