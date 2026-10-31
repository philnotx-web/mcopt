package mcopt.extras.gui;

import mcopt.extras.ExtrasConfig;
import mcopt.extras.glass.Glass;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;

/**
 * The Liquid Glass look for Video Settings and its sub-screens only. {@link #active} is true while one of those screens
 * draws (set around Screen.extractRenderStateWithTooltipAndSubtitles), and the widget mixins draw glass instead of
 * vanilla's sprites only then, so every other screen and the in-game HUD stay vanilla.
 */
public final class GlassUi {
	private static boolean active;

	private GlassUi() {
	}

	public static boolean styles(Screen screen) {
		return ExtrasConfig.glassScreen && (screen instanceof VideoSettingsScreen || screen instanceof ParticleTypesScreen);
	}

	public static void begin(Screen screen) {
		active = styles(screen);
	}

	public static void end() {
		active = false;
	}

	public static boolean active() {
		return active;
	}

	public static Glass.Filler filler(GuiGraphicsExtractor g) {
		return g::fill;
	}

	/** The tint over the (blurred) background. */
	public static void backdrop(GuiGraphicsExtractor g, int width, int height) {
		if (ExtrasConfig.glassDark) g.fillGradient(0, 0, width, height, 0x70101218, 0x900A0B10);
		else g.fillGradient(0, 0, width, height, 0x40FFFFFF, 0x26DDE3EE);
	}

	/** The large panel behind the options list. */
	public static void listPanel(GuiGraphicsExtractor g, int x, int y, int w, int h) {
		Glass.panel(filler(g), x, y, w, h, ExtrasConfig.glassRadius + 4, 1.0F);
	}

	/** A button: a small glass capsule, brighter when hovered or focused, dimmed when inactive. */
	public static void button(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean enabled, boolean hot) {
		Glass.Filler f = filler(g);
		int r = Math.min(ExtrasConfig.glassRadius, h / 2);
		float o = enabled ? 1.0F : 0.45F;
		Glass.panel(f, x, y, w, h, r, o);
		if (hot && enabled) Glass.lens(f, x, y, w, h, r, 0.75F);
	}

	/** A slider: a glass track, its filled part tinted, and a round lens for the handle. */
	public static void sliderTrack(GuiGraphicsExtractor g, int x, int y, int w, int h, double value, boolean enabled) {
		Glass.Filler f = filler(g);
		int r = Math.min(ExtrasConfig.glassRadius, h / 2);
		float o = enabled ? 1.0F : 0.45F;
		Glass.panel(f, x, y, w, h, r, o);
		int fw = (int) Math.round((w - 4) * Math.clamp(value, 0.0, 1.0));
		if (fw > 0) {
			int accent = ExtrasConfig.glassDark ? 0x3D8BFF : 0x2F7CF6;
			Glass.roundRect(f, x + 2, y + 2, fw, h - 4, Math.max(0, r - 2), (Math.round(o * 110) << 24) | accent);
		}
	}

	public static void sliderHandle(GuiGraphicsExtractor g, int x, int y, int w, int h, boolean hot, boolean enabled) {
		Glass.Filler f = filler(g);
		float o = enabled ? 1.0F : 0.45F;
		int r = Math.min(ExtrasConfig.glassRadius, w / 2);
		Glass.roundRect(f, x, y + 1, w, h - 2, r, Glass.argb(o * (hot ? 0.95F : 0.80F), 255, 255, 255));
		Glass.lens(f, x, y + 1, w, h - 2, r, o);
	}

	/** The scrollbar: a thin capsule track and a brighter thumb. */
	public static void scrollbar(GuiGraphicsExtractor g, int x, int y, int w, int h, int thumbY, int thumbH) {
		Glass.Filler f = filler(g);
		int tw = Math.max(2, w - 2), tx = x + (w - tw) / 2;
		Glass.roundRect(f, tx, y, tw, h, tw / 2, Glass.argb(0.25F, 0, 0, 0));
		Glass.roundRect(f, tx, thumbY, tw, thumbH, tw / 2, Glass.argb(0.70F, 255, 255, 255));
	}
}
