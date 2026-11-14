# Myau+ client layer for EaglercraftX 1.8.8

Package root `myau`.
- `font/`        pure-Java TTF parser/rasteriser, FontManager, TTFFontRenderer
- `ui/`          ClickGui (1:1 from OpenMyau+), components
- `event/`, `events/`  reflection-free event system (EventManager.call -> Module.onXxx)
- `management/`  Blink, Lag, PlayerState, Rotation(+State), Friend/Target lists
- `module/modules/`  HUD, DynamicIsland, ClickGui, the 21 Combat and 16 Movement modules; `Scaffold/AutoHeal/BedNuker`
                 are PLACEHOLDERS (Player/Misc categories not ported yet) that other modules reference
- `Hooks.java`   all engine hooks; `util/Acc.java` replaces Mixin accessors
- `ENGINE_HOOKS.md`  every edit needed in the Eaglercraft source
Tools used to port: /tools/port.py + fix.py (converter from the original Forge sources).
