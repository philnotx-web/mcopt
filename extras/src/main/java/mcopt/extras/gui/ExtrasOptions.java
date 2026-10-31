package mcopt.extras.gui;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import mcopt.extras.ExtrasConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jspecify.annotations.Nullable;

/**
 * The sections the Video Settings screen gains below vanilla's own. Captions are plain English: without Fabric API a
 * mod's language files are never loaded, and a translation key with no translation is shown as written.
 */
public final class ExtrasOptions {
	private static final Component RESTART = Component.literal("Takes effect after restarting Minecraft.");

	private ExtrasOptions() {
	}

	public static void addSections(OptionsList list, Screen screen, Options options) {
		// ---- mcopt's own switches (config/mcopt.properties, read at startup)
		list.addHeader(header("mcopt Performance"));
		list.addSmall(
			choice("Profile", "mcopt's set of optimizations. indie: Metal renderer (default). recommended: also far terrain. "
					+ "alpha: the older set. none: all off. " + RESTART.getString(),
				List.of("indie", "recommended", "alpha", "none"), McoptProperties.get("profile", "indie"),
				v -> McoptProperties.set("profile", v)),
			bool("Metal Renderer", "Draw with Apple Metal instead of OpenGL. Turn off for mods that call OpenGL directly. " + RESTART.getString(),
				!"false".equals(McoptProperties.get("mcopt.metal", "true")), v -> McoptProperties.set("mcopt.metal", String.valueOf(v))),
			bool("Far Terrain", "Experimental low-detail terrain beyond the render distance. Best on Macs with 10 or more GPU cores. "
					+ RESTART.getString(),
				"true".equals(McoptProperties.get("mcopt.lod", "false")), v -> McoptProperties.set("mcopt.lod", String.valueOf(v)))
		);

		// ---- particles
		list.addHeader(header("Particles"));
		list.addSmall(
			bool("All Particles", "Off hides every particle.", ExtrasConfig.particles, v -> ExtrasConfig.particles = v),
			bool("Rain Splashes", null, ExtrasConfig.rainSplash, v -> ExtrasConfig.rainSplash = v),
			bool("Block Breaking", "The burst of pieces when a block breaks.", ExtrasConfig.blockBreakParticles, v -> ExtrasConfig.blockBreakParticles = v),
			bool("Block Cracking", "The small pieces while you mine a block.", ExtrasConfig.blockCrackParticles, v -> ExtrasConfig.blockCrackParticles = v)
		);
		list.addBig(Button.builder(Component.literal("Particle Types..."),
			b -> Minecraft.getInstance().gui.setScreen(new ParticleTypesScreen(screen, options))).build());

		// ---- animated textures
		list.addHeader(header("Animated Textures"));
		list.addSmall(
			bool("All Animations", "Off stops every animated block texture.", ExtrasConfig.animations, v -> ExtrasConfig.animations = v),
			bool("Water", null, ExtrasConfig.animWater, v -> ExtrasConfig.animWater = v),
			bool("Lava", null, ExtrasConfig.animLava, v -> ExtrasConfig.animLava = v),
			bool("Fire", null, ExtrasConfig.animFire, v -> ExtrasConfig.animFire = v),
			bool("Portals", null, ExtrasConfig.animPortal, v -> ExtrasConfig.animPortal = v),
			bool("Sculk", null, ExtrasConfig.animSculk, v -> ExtrasConfig.animSculk = v),
			bool("Other Blocks", "Sea lanterns, prismarine, magma, command blocks and the rest.", ExtrasConfig.animBlocks, v -> ExtrasConfig.animBlocks = v)
		);

		// ---- details
		list.addHeader(header("Details"));
		list.addSmall(
			bool("Sky", "Off leaves the fog colour where the sky would be.", ExtrasConfig.sky, v -> ExtrasConfig.sky = v),
			bool("Sun and Moon", null, ExtrasConfig.sunMoon, v -> ExtrasConfig.sunMoon = v),
			bool("Stars", null, ExtrasConfig.stars, v -> ExtrasConfig.stars = v),
			bool("Rain and Snow", "The falling rain and snow (the weather itself still happens).", ExtrasConfig.weather, v -> ExtrasConfig.weather = v),
			bool("Beacon Beams", null, ExtrasConfig.beaconBeam, v -> ExtrasConfig.beaconBeam = v),
			bool("Item Frame Names", null, ExtrasConfig.itemFrameNames, v -> ExtrasConfig.itemFrameNames = v),
			bool("Armor Stand Names", null, ExtrasConfig.armorStandNames, v -> ExtrasConfig.armorStandNames = v),
			bool("Toasts", "The pop-ups for advancements, recipes and tutorials.", ExtrasConfig.toasts, v -> ExtrasConfig.toasts = v)
		);

		// ---- fog
		list.addHeader(header("Fog"));
		list.addSmall(
			bool("Distance Fog", "The fog at the edge of your render distance.", ExtrasConfig.fogTerrain, v -> ExtrasConfig.fogTerrain = v),
			slider("Fog Start", "Where the distance fog begins, in percent of the render distance.", 0, 100, ExtrasConfig.fogStartPercent, "%",
				v -> ExtrasConfig.fogStartPercent = v),
			bool("Biome and Dimension Fog", "The thick haze in the Nether, the End, swamps and during boss fights.",
				ExtrasConfig.fogEnvironment, v -> ExtrasConfig.fogEnvironment = v),
			slider("Underwater View", "How far you see underwater, in percent of vanilla.", 25, 500, ExtrasConfig.fogWaterPercent, "%",
				v -> ExtrasConfig.fogWaterPercent = v),
			bool("Lava Fog", null, ExtrasConfig.fogLava, v -> ExtrasConfig.fogLava = v),
			bool("Powder Snow Fog", null, ExtrasConfig.fogPowderSnow, v -> ExtrasConfig.fogPowderSnow = v),
			bool("Effect Fog", "Blindness and Darkness fog. Off removes it, which changes how those effects play.",
				!ExtrasConfig.fogRemoveEffects, v -> ExtrasConfig.fogRemoveEffects = !v)
		);

		// ---- this screen's own look
		list.addHeader(header("Glass Style"));
		list.addSmall(
			bool("Liquid Glass", "Draw Video Settings in the glass style.", ExtrasConfig.glassScreen, v -> ExtrasConfig.glassScreen = v),
			choice("Glass Tint", null, List.of("Dark", "Light"), ExtrasConfig.glassDark ? "Dark" : "Light", v -> ExtrasConfig.glassDark = v.equals("Dark")),
			slider("Frost", "How strongly the glass is tinted.", 0, 100, ExtrasConfig.glassFrost, "%", v -> ExtrasConfig.glassFrost = v),
			slider("Rim Light", "The bright edge along the top of each panel.", 0, 100, ExtrasConfig.glassRim, "%", v -> ExtrasConfig.glassRim = v),
			slider("Corner Radius", null, 0, 12, ExtrasConfig.glassRadius, "", v -> ExtrasConfig.glassRadius = v),
			bool("Background Blur", "Blur the world or panorama behind the screen (uses Menu Background Blur's strength).",
				ExtrasConfig.glassBlur, v -> ExtrasConfig.glassBlur = v)
		);
	}

	// ---------------------------------------------------------------- builders

	static Component header(String text) {
		return Component.literal(text).withStyle(ChatFormatting.UNDERLINE, ChatFormatting.BOLD);
	}

	static OptionInstance<Boolean> bool(String caption, @Nullable String tooltip, boolean initial, Consumer<Boolean> set) {
		return OptionInstance.createBoolean(caption, tip(tooltip), initial, set::accept);
	}

	static OptionInstance<Integer> slider(String caption, @Nullable String tooltip, int min, int max, int initial, String unit, IntConsumer set) {
		return new OptionInstance<>(caption, tip(tooltip), (cap, v) -> Options.genericValueLabel(cap, Component.literal(v + unit)),
			new OptionInstance.IntRange(min, max), Math.clamp(initial, min, max), v -> set.accept(v));
	}

	static OptionInstance<String> choice(String caption, @Nullable String tooltip, List<String> values, String initial, Consumer<String> set) {
		return new OptionInstance<>(caption, tip(tooltip), (cap, v) -> Options.genericValueLabel(cap, Component.literal(v)),
			new OptionInstance.Enum<>(values, Codec.STRING), values.contains(initial) ? initial : values.getFirst(), set::accept);
	}

	private static <T> OptionInstance.TooltipSupplier<T> tip(@Nullable String text) {
		return text == null ? OptionInstance.noTooltip() : OptionInstance.cachedConstantTooltip(Component.literal(text));
	}
}
