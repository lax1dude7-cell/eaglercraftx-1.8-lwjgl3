# Prompt for GitHub Copilot (paste into Copilot Chat / agent mode in the Codespace)

You are working in the EaglercraftX 1.8.8 repo (arlenhaghighatjou/eaglercraftx-1.8-lwjgl3). I am adding a client UI layer.
I already wrote all the code - it is in `myau-eaglercraft-ui/src/main/java/myau/**`. YOUR JOB IS ONLY INTEGRATION. Do not redesign or rewrite the UI.

## Step 1 - copy the code
Copy the folder `myau/` into the repo's game source root, next to `net/minecraft/` (find the folder that contains `net/minecraft/client/Minecraft.java` and put `myau/` beside `net/`). Keep package names (`myau.*`).

## Step 2 - fix API mismatches (ONLY in `myau/util/Compat.java` where possible)
The code was compile-checked against stubs, so the only expected errors are Eaglercraft API names. Make each of these compile against THIS repo, keeping the same behaviour:
- `Compat.setLinearFilter` -> sets GL_LINEAR min+mag filter on the given GL texture id (use whatever EaglercraftGPU/GlStateManager/TextureUtil offers).
- `Compat.storageGet/storageSet` -> persist a String under a key (EagRuntime storage or the repo's virtual filesystem).
- `Compat.keyName`, `Compat.eventWheel` -> `Keyboard.getKeyName`, `Mouse.getEventDWheel` from the Eaglercraft input classes.
- `ClickGui.handleMouseInput` uses `Mouse.getEventX()/getEventY()` - fix the import/class if different.
- Any `GlStateManager`, `Tessellator`/`WorldRenderer`, `DynamicTexture`, `DefaultVertexFormats` call that does not exist: replace with the Eaglercraft equivalent. Do NOT add java.awt / javax.imageio / reflection / threads - the code must stay TeaVM-compatible (Java 8 source level).

## Step 3 - font file
Copy my TrueType font to `<resources root>/assets/minecraft/myau/fonts/main.ttf` (the same place `assets/minecraft/textures/...` lives, so it is packed into the EPK / resource bundle). It must be a glyph-based TrueType `.ttf` (not CFF .otf). If it is not found the UI falls back to the vanilla font and prints "[Myau] Font not found" - make sure that message does not appear.

## Step 4 - hooks (4 small edits, nothing else)
1. `Minecraft.startGame()` - after `fontRendererObj` is created / resource manager is ready: `myau.Myau.init();`
2. `GuiIngame.renderGameOverlay(float partialTicks)` - at the very END of the method: `myau.Myau.onRender2D(partialTicks);`
3. `Minecraft.runTick()` - in the keyboard event loop, when a key is pressed and `currentScreen == null`: `myau.Myau.onKeyPress(Keyboard.getEventKey());` (RIGHT SHIFT opens the ClickGui)
4. `Minecraft.runTick()` - once per tick: `myau.Myau.onTick();`

## Step 5 - verify
- Build the web (TeaVM) AND desktop (LWJGL3) runtimes; both must compile.
- In game: Right Shift opens the ClickGui with 5 panels (Combat, Movement, Render, Player, Misc); left-click toggles a module, right-click expands its settings, drag headers, scroll long panels, click the +/- to collapse.
- HUD shows: top-left info line, player head + name, rainbow arraylist with a left bar, and the DynamicIsland pill top-centre.
- Text must be crisp and use my TTF at GUI scale 1, 2, 3 and 4 (change Video Settings > GUI Scale).
- Report every file you changed and every API you had to substitute.

Do NOT add modules, rename classes, change visuals, or "improve" the architecture. `StubModules.java` is intentional placeholder data for visual parity and will be deleted later.
