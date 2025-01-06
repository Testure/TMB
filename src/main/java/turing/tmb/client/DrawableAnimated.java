package turing.tmb.client;

import turing.tmb.api.drawable.IDrawableAnimated;
import turing.tmb.api.drawable.IDrawableStatic;
import turing.tmb.api.drawable.ITickTimer;
import turing.tmb.api.drawable.gui.IGuiHelper;
import turing.tmb.util.TickTimer;

public class DrawableAnimated implements IDrawableAnimated {
	private final IDrawableStatic drawable;
	private final ITickTimer timer;
	private final StartDirection direction;

	public DrawableAnimated(IDrawableStatic drawable, int timerSpeed, StartDirection direction, boolean inverted) {
		StartDirection startDirection = direction;
		if (inverted) {
			switch (direction) {
				case TOP:
					startDirection = StartDirection.BOTTOM;
					break;
				case LEFT:
					startDirection = StartDirection.RIGHT;
					break;
				case BOTTOM:
					startDirection = StartDirection.TOP;
					break;
				default:
					startDirection = StartDirection.LEFT;
					break;
			}
		}
		int tickTimerMaxValue;
		if (startDirection == StartDirection.TOP || startDirection == StartDirection.BOTTOM) {
			tickTimerMaxValue = drawable.getHeight();
		} else {
			tickTimerMaxValue = drawable.getWidth();
		}
		this.drawable = drawable;
		this.timer = new TickTimer(timerSpeed, tickTimerMaxValue, !inverted);
		this.direction = startDirection;
	}

	@Override
	public int getWidth() {
		return drawable.getWidth();
	}

	@Override
	public int getHeight() {
		return drawable.getHeight();
	}

	@Override
	public void draw(IGuiHelper helper) {
		int maskLeft = 0;
		int maskRight = 0;
		int maskTop = 0;
		int maskBottom = 0;
		int animationValue = timer.getValue();
		timer.onDraw();

		switch (direction) {
			case TOP:
				maskBottom = animationValue;
				break;
			case BOTTOM:
				maskTop = animationValue;
				break;
			case LEFT:
				maskRight = animationValue;
				break;
			case RIGHT:
				maskLeft = animationValue;
				break;
		}

		drawable.draw(helper, 0, 0, maskTop, maskBottom, maskLeft, maskRight);
	}
}
