package Filter;
import java.awt.Color;

public class NoFilter implements ColorFilter {

	@Override
	public Color GetFilteredColor(Color p1, Color p2) {
		return p2;
	}

}
