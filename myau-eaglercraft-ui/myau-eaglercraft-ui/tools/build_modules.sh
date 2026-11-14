#!/bin/bash
cd /home/claude
python3 tools/port.py AimAssist AntiFireball AutoClicker BackTrack BlockHit ClickAssits Criticals HitBox HitSelect KillAura LagRange MoreKB NoHitDelay Reach Refill ServerLag SprintReset TargetStrafe ThrowAura Velocity Wtap > /tmp/port.log 2>&1
python3 tools/fix.py
