# ENGINE_HOOKS.md - every edit to the Eaglercraft source (vanilla 1.8.8)

All logic lives in `myau.Hooks` (and `myau.util.Acc`). The engine only gets one-line calls. Vanilla method/field names below are MCP names;
Eaglercraft ships the same names. Where a name differs in this repo, find the equivalent spot - the *meaning* is what matters.

## A. Make these members PUBLIC (no other change)
| Class | Members |
|---|---|
| `Minecraft` | `timer` (the `Timer` instance), `rightClickDelayTimer` |
| `KeyBinding` | `pressed` |
| `Entity` | `isInWeb` |
| `EntityLivingBase` | `jumpTicks` |
| `EntityPlayer` | `itemInUse`, `itemInUseCount` |
| `EntityPlayerSP` | `lastReportedYaw`, `lastReportedPitch` (and `renderArmYaw`, `prevRenderArmYaw` if not already public) |
| `PlayerControllerMP` | `blockHitDelay`, `curBlockDamageMP`, `currentPlayerItem`, `isHittingBlock`, method `syncCurrentPlayItem()` |
| `EntityRenderer` | method `setupCameraTransform(float, int)` |
| `C03PacketPlayer` | `onGround` |
| `C0DPacketCloseWindow` | `windowId` |
| `C17PacketCustomPayload` | `data` |
| `S12PacketEntityVelocity` | `motionX`, `motionY`, `motionZ` |
| `RenderManager` | `viewerPosX/Y/Z` must be public (they are in vanilla 1.8.8) |

## B. Hooks (exact spots)

### Minecraft
1. `startGame()` - after `fontRendererObj` exists / resources loaded: `myau.Myau.init();`
2. `runTick()` - first statement: `myau.Hooks.runTickHead();` and last statement (every return path): `myau.Hooks.runTickReturn();`
3. `runTick()` - keyboard loop, for a pressed key while `currentScreen == null`: `myau.Hooks.key(Keyboard.getEventKey());`
4. `loadWorld(WorldClient, String)` - first statement: `myau.Hooks.loadWorld();`
5. `clickMouse()` - first statements:
   `if (myau.Hooks.noHitDelay()) this.leftClickCounter = 0;`
   `if (myau.Hooks.leftClick()) return;`
6. `rightClickMouse()` - first statement: `if (myau.Hooks.rightClick()) return;`
7. `sendClickBlockToController(boolean)` - first statement:
   `if (leftClick && myau.Hooks.hitBlock()) { this.playerController.resetBlockRemoving(); return; }` (cancel = skip the method)

### GuiIngame
8. `renderGameOverlay(float)` - LAST statement: `myau.Myau.onRender2D(partialTicks);`

### EntityRenderer
9. `renderWorldPass(int, float, long)` - right before `this.renderHand(...)` (after clouds/particles/weather): `myau.Hooks.render3D(partialTicks);`
10. `getMouseOver(float)` - after `double d0 = (double)this.mc.playerController.getBlockReachDistance();` add `d0 = myau.Hooks.raytraceRange(d0);`
    and replace the SECOND literal `3.0D` (the one in `vec3.distanceTo(vec33) > 3.0D`) with `myau.Hooks.pickRange(3.0D)`.
11. `updateCameraAndRender(float, long)` - first statement `myau.Hooks.renderFrameHead();` and before every return `myau.Hooks.renderFrameReturn();`

### EntityPlayerSP
12. `onUpdate()` - inside the `isBlockLoaded` block:
    - first statement of the method: `myau.Hooks.playerUpdateHead(this);`
    - immediately before `if (this.isRiding())`: `myau.Hooks.applyRotationOverride(this);`
    - immediately before `this.onUpdateWalkingPlayer();` (inside the else): `myau.Hooks.motionUpdate();`
    - last statement of the method: `myau.Hooks.playerUpdateReturn(this);`
13. `onLivingUpdate()` - right after `this.movementInput.updatePlayerMoveState();` add `myau.Hooks.moveInput();`
    and just before the final `super.onLivingUpdate();` add `myau.Hooks.livingUpdate();`

### EntityLivingBase
14. `moveEntityWithHeading(float, float)` - replace EVERY `this.moveFlying(a, b, c)` with `myau.Hooks.moveFlying(this, a, b, c)`.
15. `jump()` - the sprint-jump boost line `float f = this.rotationYaw * 0.017453292F;` becomes
    `float f = myau.Hooks.jumpYawRadians(this, this.rotationYaw * 0.017453292F);`

### Entity
16. `setAngles(float, float)` - first statement: `if (myau.Hooks.cancelSetAngles(this)) return;`
17. `setVelocity(double, double, double)` - first statement:
    `myau.events.KnockbackEvent ev = myau.Hooks.knockback(this, x, y, z); if (ev != null && ev.isCancelled()) { this.motionX = ev.getX(); this.motionY = ev.getY(); this.motionZ = ev.getZ(); return; }`
18. `getCollisionBorderSize()` - body becomes `return myau.Hooks.collisionBorderSize(this, 0.1F);`

### PlayerControllerMP
19. `attackEntity(EntityPlayer, Entity)` - immediately before `this.syncCurrentPlayItem();`: `myau.Hooks.attack(targetEntity);`
20. `onStoppedUsingItem(EntityPlayer)` - first statement: `if (myau.Hooks.cancelUse()) return;`

### Networking (Eaglercraft's own `NetworkManager`, no Netty)
21. OUTGOING: at the top of `NetworkManager.sendPacket(Packet)` (the single choke point every client packet goes through):
    `if (myau.Hooks.packetSend(packetIn)) return;`
    (`PacketUtil.sendPacketNoEvent` calls this same method with `PacketUtil.suppressSendEvents = true`, which `Hooks.packetSend` honours.)
22. INCOMING: where a received packet is about to be processed (`packet.processPacket(netHandler)`):
    `if (myau.Hooks.packetReceive(packet)) continue/return;`  (skip processing when it returns true)

## C. Checklist when it compiles but misbehaves
- Aura/rotations do nothing -> hooks 12 and 13 are missing or in the wrong order (head -> override -> motionUpdate -> return).
- No knockback control -> hook 17. Reach does nothing -> hook 10 (both constants). HitBox does nothing -> hook 18.
- Blink/LagRange/BackTrack/ServerLag do nothing -> hooks 21/22.
- Sword doesn't show the block pose with KillAura autoblock -> hook 11.
