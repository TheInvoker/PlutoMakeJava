package Filter;

import java.awt.Color;

public abstract interface ColorFilter
{
    public abstract Color GetFilteredColor(Color p1, Color p2);
}