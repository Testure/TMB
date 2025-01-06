package turing.tmb.util;

import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Keyboard;

import java.util.List;

public class CycleTimer {
	private static final int cycleTime = 1000;
	private long startTime;
	private long drawTime;
	private long pausedDuration;

	public CycleTimer(int offset) {
		long time = System.currentTimeMillis();
		this.startTime = time - ((long) offset * cycleTime);
		this.drawTime = time;
	}

	@Nullable
	public <T> T getCycledItem(List<T> list) {
		if (list.isEmpty()) return null;
		long index = ((drawTime - startTime) / cycleTime) % list.size();
		return list.get(Math.toIntExact(index));
	}

	public void onDraw() {
		if (!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) && !Keyboard.isKeyDown(Keyboard.KEY_RSHIFT)) {
			if (pausedDuration > 0) {
				startTime += pausedDuration;
				pausedDuration = 0;
			}
			drawTime = System.currentTimeMillis();
		} else {
			pausedDuration = System.currentTimeMillis() - drawTime;
		}
	}
}
