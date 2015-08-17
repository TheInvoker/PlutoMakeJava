package Filter;


import java.awt.Color;

public class ColorCalculate {
	
    public static int Darken(int A, int B)
    {
        return Math.min(A, B);
    }

    public static int VividLight(int A, int B)
    {
        if (B >= 128)
        {
            return ColorDodge(A, (2 * (B - 128)));
        }
        else
        {
            return ColorBurn(A, 2 * B);
        }
    }

    public static int ColorDodge(int A, int B)
    {
        return ((B == 255) ? B : Math.min(255, ((A << 8) / (255 - B))));
    }

    public static int ColorBurn(int A, int B)
    {
        return ((B == 0) ? B : Math.max(0, (255 - ((255 - A) << 8) / B)));
    }

    public static Color Mix(Color B, Color A)
    {
        int rA = A.getRed();
        int gA = A.getGreen();
        int bA = A.getBlue();
        int aA = A.getAlpha();
        int rB = B.getRed();
        int gB = B.getGreen();
        int bB = B.getBlue();
        int aB = B.getAlpha();
        int rOut = (rA * aA / 255) + (rB * aB * (255 - aA) / (255 * 255));
        int gOut = (gA * aA / 255) + (gB * aB * (255 - aA) / (255 * 255));
        int bOut = (bA * aA / 255) + (bB * aB * (255 - aA) / (255 * 255));
        int aOut = aA + (aB * (255 - aA) / 255);
        return new Color(rOut, gOut, bOut, aOut);
    }
}
