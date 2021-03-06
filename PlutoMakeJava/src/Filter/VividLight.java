package Filter;

import java.awt.Color;

public final class VividLight implements ColorFilter {

	@Override
	public final Color GetFilteredColor(Color p1, Color p2) {
        int newR = Math.max(0, Math.min(255, ColorCalculate.VividLight(p1.getRed(), p2.getRed())));
        int newG = Math.max(0, Math.min(255, ColorCalculate.VividLight(p1.getGreen(), p2.getGreen())));
        int newB = Math.max(0, Math.min(255, ColorCalculate.VividLight(p1.getBlue(), p2.getBlue())));

        return new Color(newR, newG, newB, p2.getAlpha());
	}

}
