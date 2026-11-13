# Prompt for GitHub Copilot (agent mode) - Combat modules delivery

You are working in the EaglercraftX 1.8.8 repo. I added a client layer in `myau-eaglercraft-ui/src/main/java/myau/**` (ClickGui, HUD, TTF font
manager, event system, managers, utilities and ALL Combat modules ported from OpenMyau+). YOUR JOB IS ONLY INTEGRATION. Do not redesign,
rename, "improve", or rewrite any of it. Keep Java 8 source level and TeaVM compatibility (no reflection, no java.awt, no threads, no lambdas, no streams).

1. Replace the old `myau/` folder (beside `net/`) with the new one. Keep package names.
2. Follow `ENGINE_HOOKS.md` EXACTLY: section A (make listed members public) and section B (hooks 1-22). Each hook is a one-line call into `myau.Hooks`
   / `myau.Myau`; do not copy logic into the engine classes. Skip a hook only if the spot truly doesn't exist and tell me which.
3. Fix API-name mismatches with this repo's Eaglercraft classes. Prefer fixing in `myau/util/Compat.java`, `myau/util/Acc.java`, `myau/util/RenderUtil.java`
   (Tessellator/GlStateManager names) and `myau/util/PacketUtil.java` (`NetworkManager.sendPacket`). Remaining expected mismatches are small
   (e.g. `Keyboard`/`Mouse` classes, `EagRuntime.getStorage/setStorage`, `EaglercraftGPU.glTexParameteri`, Guava `Multimap` in `ItemUtil`).
4. Copy my `.ttf` to `<resources>/assets/minecraft/myau/fonts/main.ttf`.
5. Build BOTH the TeaVM (WASM-GC and JS) and the LWJGL3 desktop runtimes. If WASM-GC fails validation ("local.set expected type ref"), report the
   offending Java method (enable TeaVM debug/name section) instead of guessing.
6. Test in game (singleplayer): RIGHT SHIFT opens the ClickGui; Combat panel lists 21 modules; right-click a module -> its settings;
   enable KillAura, Reach, Velocity, HitBox, AimAssist, AutoClicker and verify they react. Report every file you changed and every API you substituted.
