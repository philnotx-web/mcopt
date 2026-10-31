package mcopt.extras.glass;

import mcopt.extras.ExtrasConfig;

/**
 * Liquid Glass panels drawn from plain rectangle fills: a rounded, tinted body with a vertical sheen, a bright specular rim
 * on the top edge, a faint lower rim, a soft drop shadow and an optional tinted "selection" lens. Corners are scanline
 * rounded with one anti-aliased pixel per row edge, so only {@link Filler#fill} is needed from the game.
 */
public final class Glass {
	/** The game's rectangle fill: [x1, x2) x [y1, y2) in GUI pixels, ARGB colour. */
	@FunctionalInterface
	public interface Filler {
		void fill(int x1, int y1, int x2, int y2, int argb);
	}

	private Glass() {
	}

	/** A panel in the configured style. */
	public static void panel(Filler g, int x, int y, int w, int h) {
		panel(g, x, y, w, h, ExtrasConfig.glassRadius, 1.0F);
	}

	/** A panel with its own radius; {@code opacity} scales every layer (fade in / out). */
	public static void panel(Filler g, int x, int y, int w, int h, int radius, float opacity) {
		if (w <= 0 || h <= 0 || opacity <= 0) return;
		int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));
		float frost = ExtrasConfig.glassFrost / 100.0F;
		float rim = ExtrasConfig.glassRim / 100.0F;
		boolean dark = ExtrasConfig.glassDark;

		// soft shadow: three widening, fading rings below the panel
		for (int i = 3; i >= 1; i--) {
			roundRect(g, x - i, y - i + 2, w + 2 * i, h + 2 * i, r + i, argb(opacity * 0.07F * (4 - i) / 3.0F, 0, 0, 0));
		}

		// body: tint with a gentle top-to-bottom sheen (lighter at the top, as if lit from above)
		int top = dark ? 0x2A2C33 : 0xFFFFFF;
		int bottom = dark ? 0x121318 : 0xE6E9F0;
		float aTop = opacity * (dark ? 0.30F + 0.45F * frost : 0.16F + 0.34F * frost);
		float aBottom = opacity * (dark ? 0.38F + 0.45F * frost : 0.10F + 0.30F * frost);
		int bands = Math.max(1, Math.min(h, 8));
		for (int b = 0; b < bands; b++) {
			int y1 = y + h * b / bands, y2 = y + h * (b + 1) / bands;
			float t = (b + 0.5F) / bands;
			roundRectRows(g, x, y, w, h, r, y1, y2, mix(top, bottom, t, aTop + (aBottom - aTop) * t));
		}

		// specular highlight: a bright 1px rim along the top, fading out along the corner curves
		if (rim > 0) {
			int hi = argb(opacity * (dark ? 0.55F : 0.85F) * rim, 255, 255, 255);
			roundRectOutlineTop(g, x, y, w, h, r, hi);
			// a broad faint glow under the rim: the "liquid" thickness
			int glow = argb(opacity * 0.10F * rim, 255, 255, 255);
			roundRectRows(g, x, y, w, h, r, y + 1, y + Math.max(2, h / 3), glow);
			// lower rim: light passing through the glass's bottom edge
			int lo = argb(opacity * 0.22F * rim, 255, 255, 255);
			int lr = r > 0 ? inset(r, 0.5F) : 0;
			g.fill(x + Math.max(lr, r / 2), y + h - 1, x + w - Math.max(lr, r / 2), y + h, lo);
		}
		// hairline border, so light glass stays visible over bright terrain
		roundRectOutline(g, x, y, w, h, r, argb(opacity * (dark ? 0.28F : 0.18F), dark ? 255 : 0, dark ? 255 : 0, dark ? 255 : 0));
	}

	/** A small lens marking a selection (the hotbar's chosen slot): brighter glass with a full rim. */
	public static void lens(Filler g, int x, int y, int w, int h, int radius, float opacity) {
		int r = Math.max(0, Math.min(radius, Math.min(w, h) / 2));
		roundRect(g, x, y, w, h, r, argb(opacity * 0.28F, 255, 255, 255));
		roundRectRows(g, x, y, w, h, r, y, y + h / 2, argb(opacity * 0.14F, 255, 255, 255));
		roundRectOutline(g, x, y, w, h, r, argb(opacity * 0.80F, 255, 255, 255));
	}

	/** A capsule-shaped progress track (experience, air, ...). {@code fill} in 0..1, {@code rgb} the bar's colour. */
	public static void capsule(Filler g, int x, int y, int w, int h, float fill, int rgb, float opacity) {
		int r = h / 2;
		roundRect(g, x, y, w, h, r, argb(opacity * 0.45F, 10, 10, 14));
		int fw = Math.round(w * Math.clamp(fill, 0.0F, 1.0F));
		if (fw > 0) {
			int fr = Math.min(r, fw / 2);
			roundRect(g, x, y, fw, h, fr, (Math.round(opacity * 230) << 24) | (rgb & 0xFFFFFF));
			roundRectRows(g, x, y, fw, h, fr, y, y + Math.max(1, h / 2), argb(opacity * 0.30F, 255, 255, 255));
		}
		roundRectOutline(g, x, y, w, h, r, argb(opacity * 0.35F, 255, 255, 255));
	}

	// ---------------------------------------------------------------- shapes

	public static void roundRect(Filler g, int x, int y, int w, int h, int r, int argb) {
		roundRectRows(g, x, y, w, h, r, y, y + h, argb);
	}

	/** The rows y1..y2 of a rounded rect, one anti-aliased edge pixel per side in the corner rows. */
	static void roundRectRows(Filler g, int x, int y, int w, int h, int r, int y1, int y2, int argb) {
		if (((argb >>> 24) & 0xFF) == 0) return;
		y1 = Math.max(y1, y);
		y2 = Math.min(y2, y + h);
		if (y1 >= y2) return;
		if (r <= 0) {
			g.fill(x, y1, x + w, y2, argb);
			return;
		}
		// straight middle part in one fill
		int midTop = Math.max(y1, y + r), midBottom = Math.min(y2, y + h - r);
		if (midTop < midBottom) g.fill(x, midTop, x + w, midBottom, argb);
		for (int row = y1; row < y2; row++) {
			int d; // distance of the row from the nearer corner's centre line, in rows
			if (row < y + r) d = y + r - row;
			else if (row >= y + h - r) d = row - (y + h - r) + 1;
			else continue;
			float exact = r - (float) Math.sqrt(Math.max(0, r * r - (d - 0.5F) * (d - 0.5F)));
			int in = (int) Math.ceil(exact);
			float cover = in - exact; // the partly covered pixel's coverage
			if (x + in < x + w - in) g.fill(x + in, row, x + w - in, row + 1, argb);
			if (in > 0 && cover > 0.05F) {
				int aa = scaleAlpha(argb, cover);
				g.fill(x + in - 1, row, x + in, row + 1, aa);
				g.fill(x + w - in, row, x + w - in + 1, row + 1, aa);
			}
		}
	}

	static void roundRectOutline(Filler g, int x, int y, int w, int h, int r, int argb) {
		if (r <= 0) {
			g.fill(x, y, x + w, y + 1, argb);
			g.fill(x, y + h - 1, x + w, y + h, argb);
			g.fill(x, y + 1, x + 1, y + h - 1, argb);
			g.fill(x + w - 1, y + 1, x + w, y + h - 1, argb);
			return;
		}
		g.fill(x + r, y, x + w - r, y + 1, argb);
		g.fill(x + r, y + h - 1, x + w - r, y + h, argb);
		g.fill(x, y + r, x + 1, y + h - r, argb);
		g.fill(x + w - 1, y + r, x + w, y + h - r, argb);
		arcs(g, x, y, w, h, r, argb, true, true);
	}

	/** Only the top edge and the upper halves of the top corners, fading towards the sides. */
	static void roundRectOutlineTop(Filler g, int x, int y, int w, int h, int r, int argb) {
		g.fill(x + r, y, x + w - r, y + 1, argb);
		if (r > 0) arcs(g, x, y, w, h, r, scaleAlpha(argb, 0.7F), true, false);
	}

	/** One-pixel corner arcs: each row's outermost pixel in the corner zone. */
	private static void arcs(Filler g, int x, int y, int w, int h, int r, int argb, boolean top, boolean bottom) {
		// d counts rows outwards from the straight side (d = 1 next to it, d = r the top or bottom row); each row's span
		// runs from its own edge to the previous row's, so the arc has no gaps where it is flat
		int prevIn = 0;
		for (int d = 1; d <= r; d++) {
			int in = inset(r, d - 0.5F);
			int from = Math.min(in, prevIn + 1), to = Math.max(in + 1, prevIn + 1);
			if (d == r) to = Math.max(to, r); // the top row reaches the straight top edge
			int x1 = x + from, x2 = x + to;
			int rx1 = x + w - to, rx2 = x + w - from;
			if (top) {
				int row = y + r - d;
				g.fill(x1, row, x2, row + 1, argb);
				g.fill(rx1, row, rx2, row + 1, argb);
			}
			if (bottom) {
				int row = y + h - r + d - 1;
				g.fill(x1, row, x2, row + 1, argb);
				g.fill(rx1, row, rx2, row + 1, argb);
			}
			prevIn = in;
		}
	}

	/** The corner's inset at distance {@code d} from the corner centre's row (0 at the side, r at the top). */
	private static int inset(int r, float d) {
		return Math.round(r - (float) Math.sqrt(Math.max(0, r * r - d * d)));
	}

	// ---------------------------------------------------------------- colour

	public static int argb(float alpha, int r, int g, int b) {
		int a = Math.round(Math.clamp(alpha, 0.0F, 1.0F) * 255);
		return a << 24 | (r & 255) << 16 | (g & 255) << 8 | (b & 255);
	}

	private static int mix(int rgbA, int rgbB, float t, float alpha) {
		int r = Math.round(((rgbA >> 16) & 255) * (1 - t) + ((rgbB >> 16) & 255) * t);
		int gr = Math.round(((rgbA >> 8) & 255) * (1 - t) + ((rgbB >> 8) & 255) * t);
		int b = Math.round((rgbA & 255) * (1 - t) + (rgbB & 255) * t);
		return argb(alpha, r, gr, b);
	}

	private static int scaleAlpha(int argb, float f) {
		int a = Math.round(((argb >>> 24) & 255) * Math.clamp(f, 0.0F, 1.0F));
		return a << 24 | (argb & 0xFFFFFF);
	}
}
