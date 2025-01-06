package turing.tmb.util;

import turing.tmb.api.drawable.ITickTimer;

public class TickTimer implements ITickTimer {
	private final int maxValue;
	private final int speed;
	private final boolean countDown;
	private int value = 0;
	private int sValue = 0;
	private long lastDraw;

	public TickTimer(int speed, int maxValue, boolean countDown) {
		this.maxValue = maxValue;
		this.countDown = countDown;
		this.speed = speed;
		this.lastDraw = System.currentTimeMillis();
	}

	@Override
	public int getValue() {
		return value;
	}

	@Override
	public int getMaxValue() {
		return maxValue;
	}

	public void onDraw() {
		if ((System.currentTimeMillis() - lastDraw) <= 1) {
			return;
		}
		lastDraw = System.currentTimeMillis();
		sValue++;
		if (sValue >= (100 / speed)) {
			sValue = 0;
			if (countDown) {
				value -= 1;
				if (value < 0) {
					value = maxValue;
				}
			} else {
				value += 1;
				if (value > maxValue) {
					value = 0;
				}
			}
		}
	}
}
