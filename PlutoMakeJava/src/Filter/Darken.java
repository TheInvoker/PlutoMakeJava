package Filter;


import java.awt.Color;

public class Darken implements ColorFilter {

	@Override
	public Color GetFilteredColor(Color p1, Color p2) {
        int newR = Math.max(0, Math.min(255, ColorCalculate.Darken(p1.getRed(), p2.getRed())));
        int newG = Math.max(0, Math.min(255, ColorCalculate.Darken(p1.getGreen(), p2.getGreen())));
        int newB = Math.max(0, Math.min(255, ColorCalculate.Darken(p1.getBlue(), p2.getBlue())));

        return new Color(newR, newG, newB, p2.getAlpha());
	}

}
