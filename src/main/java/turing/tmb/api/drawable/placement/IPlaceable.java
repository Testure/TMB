package turing.tmb.api.drawable.placement;

public interface IPlaceable<T extends IPlaceable<T>> {
	T setPosition(int x, int y);

	default T setPosition(int areaX, int areaY, int areaWidth, int areaHeight, HorizontalAlignment horizontalAlignment, VerticalAlignment verticalAlignment) {
		int x = areaX + horizontalAlignment.getXPos(areaWidth, getWidth());
		int y = areaY + verticalAlignment.getYPos(areaHeight, getHeight());
		return setPosition(x, y);
	}

	int getWidth();

	int getHeight();
}
