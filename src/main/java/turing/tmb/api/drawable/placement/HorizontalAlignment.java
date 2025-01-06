package turing.tmb.api.drawable.placement;

public enum HorizontalAlignment {
	LEFT {
		@Override
		public int getXPos(int availableWidth, int elementWidth) {
			return 0;
		}
	},
	CENTER {
		@Override
		public int getXPos(int availableWidth, int elementWidth) {
			return Math.round((availableWidth - elementWidth) / 2f);
		}
	},
	RIGHT {
		@Override
		public int getXPos(int availableWidth, int elementWidth) {
			return availableWidth - elementWidth;
		}
	};

	public abstract int getXPos(int availableWidth, int elementWidth);
}
