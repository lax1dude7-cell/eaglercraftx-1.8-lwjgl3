# Myau+ ClickGUI / HUD / TTF font manager for EaglercraftX 1.8.8

Package root `myau`. See COPILOT_PROMPT.md for integration.

- `myau/font/ttf/*`      pure-Java TrueType parser + anti-aliased rasteriser (no java.awt, TeaVM-safe)
- `myau/font/*`          FontManager (register any .ttf, get(name,size)), TTFFontRenderer, UIFont
- `myau/ui/*`            ClickGui (1:1 geometry from OpenMyau+), components (module, bool, slider, mode, colour, text, bind)
- `myau/module/modules`  HUD (arraylist/info line/head), DynamicIsland, ClickGUIModule, StubModules (placeholders)
- `myau/util/Compat.java` every engine-specific call in one file
