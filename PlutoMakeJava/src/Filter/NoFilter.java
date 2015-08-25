package Filter;

import java.awt.Color;

public final class NoFilter implements ColorFilter {

	@Override
	public final Color GetFilteredColor(Color p1, Color p2) {
		return p2;
	}

}
