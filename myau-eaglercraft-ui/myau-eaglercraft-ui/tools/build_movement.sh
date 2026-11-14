#!/bin/bash
cd /home/claude
python3 tools/port.py AntiAFK AntiVoid Blink Eagle FastBow Fly Jesus KeepSprint LongJump NoFall NoJumpDelay NoSlow SafeWalk Speed Sprint Timer > /tmp/port_mv.log 2>&1
python3 tools/fix_movement.py
