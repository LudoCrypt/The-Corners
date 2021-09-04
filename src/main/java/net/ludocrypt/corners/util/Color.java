package net.ludocrypt.corners.util;

import net.minecraft.util.math.MathHelper;

public class Color {
	private int value;

	private Color(int rgb) {
		value = 0xff000000 | rgb;
	}

	private Color(int r, int g, int b) {
		this(r, g, b, 255);
	}

	private Color(int r, int g, int b, int a) {
		value = ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF) << 0);
	}

	public static Color colorOf(int rgb) {
		return new Color(rgb);
	}

	public static Color colorOf(int r, int g, int b) {
		return new Color(r, g, b);
	}

	public static Color colorOf(int r, int g, int b, int a) {
		return new Color(r, g, b, a);
	}

	public static int of(int rgb) {
		return new Color(rgb).getRGB();
	}

	public static int of(int r, int g, int b) {
		return new Color(r, g, b).getRGB();
	}

	public static int of(int r, int g, int b, int a) {
		return new Color(r, g, b, a).getRGB();
	}

	public Color sepia() {
		int p = this.getRGB();

		int a = (p >> 24) & 0xff;
		int r = (p >> 16) & 0xff;
		int g = (p >> 8) & 0xff;
		int b = p & 0xff;

		// calculate tr, tg, tb
		int tr = (int) (0.393 * r + 0.769 * g + 0.189 * b);
		int tg = (int) (0.349 * r + 0.686 * g + 0.168 * b);
		int tb = (int) (0.272 * r + 0.534 * g + 0.131 * b);

		// check condition
		if (tr > 255) {
			r = 255;
		} else {
			r = tr;
		}

		if (tg > 255) {
			g = 255;
		} else {
			g = tg;
		}

		if (tb > 255) {
			b = 255;
		} else {
			b = tb;
		}

		// set new RGB value
		p = (a << 24) | (r << 16) | (g << 8) | b;

		return colorOf(p);
	}

	public Color boost(int r, int g, int b) {
		return new Color(MathHelper.clamp(this.getRed() + r, 0, 255), MathHelper.clamp(this.getGreen() + g, 0, 255), MathHelper.clamp(this.getBlue() + b, 0, 255));
	}

	public int getRed() {
		return (getRGB() >> 16) & 0xFF;
	}

	public int getGreen() {
		return (getRGB() >> 8) & 0xFF;
	}

	public int getRGB() {
		return value;
	}

	public int getBlue() {
		return (getRGB()) & 0xFF;
	}

	public int getAlpha() {
		return (getRGB() >> 24) & 0xff;
	}
}
