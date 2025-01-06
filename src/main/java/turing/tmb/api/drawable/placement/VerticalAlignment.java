package turing.tmb.api.drawable.placement;

public enum VerticalAlignment {
	TOP {
		@Override
		public int getYPos(int availableHeight, int elementHeight) {
			return 0;
		}
	},
	CENTER {
		@Override
		public int getYPos(int availableHeight, int elementHeight) {
			return Math.round((availableHeight - elementHeight) / 2f);
		}
	},
	BOTTOM {
		@Override
		public int getYPos(int availableHeight, int elementHeight) {
			return availableHeight - elementHeight;
		}
	};

	public abstract int getYPos(int availableHeight, int elementHeight);
}
