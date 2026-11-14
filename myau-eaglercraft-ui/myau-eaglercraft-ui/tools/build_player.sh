#!/bin/bash
cd /home/claude
python3 tools/port.py AntiDebuff AutoBlockIn AutoHeal AutoSwap AutoTool ChestStealer FakeLag FastPlace FlagDetector AutoGapple GhostHand InvManager InvWalk MCF Scaffold SpeedMine > /tmp/port_pl.log 2>&1
python3 tools/fix_player.py
