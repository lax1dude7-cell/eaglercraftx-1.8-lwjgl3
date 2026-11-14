# Myau+ client layer for EaglercraftX 1.8.8

Package root `myau`.
- `font/`        pure-Java TTF parser/rasteriser, FontManager, TTFFontRenderer
- `ui/`          ClickGui (1:1 from OpenMyau+), components
- `event/`, `events/`  reflection-free event system (EventManager.call -> Module.onXxx)
- `management/`  Blink, Lag, PlayerState, Rotation(+State), Friend/Target lists
- `module/modules/`  HUD, DynamicIsland, ClickGui, the 21 Combat, 16 Movement and 16 Player modules; `BedNuker`
                 is a PLACEHOLDER (Misc category not ported yet) that other modules reference
- `Hooks.java`   all engine hooks; `util/Acc.java` replaces Mixin accessors
- `ENGINE_HOOKS.md`  every edit needed in the Eaglercraft source
Tools used to port: /tools/port.py + fix.py (converter from the original Forge sources).
