package mcopt.extras;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.PreLaunchEntrypoint;

/**
 * Everything the extras screen changes, kept in {@code config/mcopt-extras.properties}. Every public static non-final
 * field is a setting: its name is the key, its type (boolean, int, float, String) decides how the value is read.
 * Particle types switched off are kept as {@code particle.<namespace:path>=false}. Read once before the game starts,
 * written when the settings screen closes.
 */
public final class ExtrasConfig implements PreLaunchEntrypoint {
	// ---- Particles
	/** Master switch: false hides every particle. */
	public static boolean particles = true;
	public static boolean rainSplash = true;
	public static boolean blockBreakParticles = true;
	public static boolean blockCrackParticles = true;
	/** Per particle type, keyed by its id ("minecraft:flame"); a type missing from the map is shown. */
	public static final Map<String, Boolean> particleTypes = new ConcurrentHashMap<>();

	// ---- Animated textures
	public static boolean animations = true;
	public static boolean animWater = true;
	public static boolean animLava = true;
	public static boolean animFire = true;
	public static boolean animPortal = true;
	public static boolean animSculk = true;
	/** Every other animated block texture (sea lanterns, prismarine, command blocks, ...). */
	public static boolean animBlocks = true;

	// ---- Details
	public static boolean sky = true;
	public static boolean sunMoon = true;
	public static boolean stars = true;
	public static boolean weather = true;
	public static boolean beaconBeam = true;
	public static boolean pistonAnimation = true;
	public static boolean itemFrameNames = true;
	public static boolean armorStandNames = true;
	public static boolean toasts = true;
	public static boolean vignette = true;

	// ---- Fog
	/** Distance fog at the edge of the render distance. */
	public static boolean fogTerrain = true;
	/** Where the edge fog starts, in percent of the render distance (vanilla is about 90). */
	public static int fogStartPercent = 90;
	/** Biome / dimension haze (the nether's and the end's thick fog, swamps, ...). */
	public static boolean fogEnvironment = true;
	/** Underwater visibility, in percent of vanilla's (100 = vanilla, 300 = three times as far). */
	public static int fogWaterPercent = 100;
	public static boolean fogLava = true;
	public static boolean fogPowderSnow = true;
	/** Blindness and Darkness fog are gameplay and stay unless this is on. */
	public static boolean fogRemoveEffects = false;

	// ---- Liquid Glass HUD
	public static boolean glassHud = true;
	public static boolean glassFps = true;
	/** What the info pill shows besides fps: "fps", "fps+coords", "fps+coords+biome". */
	public static String glassFpsMode = "fps";
	public static boolean glassHotbar = true;
	public static boolean glassStatus = true;
	public static boolean glassChat = true;
	public static boolean glassTooltips = true;
	/** 0..100: how strongly the glass is tinted (0 = clear, 100 = frosted). */
	public static int glassFrost = 55;
	/** 0..100: the specular rim's brightness. */
	public static int glassRim = 70;
	/** Corner radius in GUI pixels. */
	public static int glassRadius = 8;
	/** Light or dark glass. */
	public static boolean glassDark = true;

	// ---- Video settings screen
	/** Replace vanilla's Video Settings with the tabbed screen. */
	public static boolean tabbedVideoSettings = true;

	private static boolean loaded;

	@Override
	public void onPreLaunch() {
		load();
	}

	public static synchronized void load() {
		if (loaded) return;
		loaded = true;
		Path file = file();
		if (!Files.isRegularFile(file)) {
			save();
			return;
		}
		Properties p = new Properties();
		try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
			p.load(r);
		} catch (IOException e) {
			System.out.println("[mcopt-extras] can't read " + file + ": " + e);
			return;
		}
		for (Field f : settings()) {
			String v = p.getProperty(f.getName());
			if (v == null) continue;
			try {
				v = v.trim();
				Class<?> t = f.getType();
				if (t == boolean.class) f.setBoolean(null, Boolean.parseBoolean(v));
				else if (t == int.class) f.setInt(null, Integer.parseInt(v));
				else if (t == float.class) f.setFloat(null, Float.parseFloat(v));
				else if (t == String.class) f.set(null, v);
			} catch (ReflectiveOperationException | RuntimeException e) {
				System.out.println("[mcopt-extras] bad value " + f.getName() + "=" + v + " (ignored)");
			}
		}
		for (String k : p.stringPropertyNames()) {
			if (k.startsWith("particle.")) particleTypes.put(k.substring("particle.".length()), Boolean.parseBoolean(p.getProperty(k).trim()));
		}
	}

	public static synchronized void save() {
		Map<String, String> out = new TreeMap<>();
		try {
			for (Field f : settings()) out.put(f.getName(), String.valueOf(f.get(null)));
		} catch (IllegalAccessException e) {
			throw new IllegalStateException(e);
		}
		particleTypes.forEach((k, v) -> {
			if (!v) out.put("particle." + k, "false");
		});
		Path file = file();
		try {
			Files.createDirectories(file.getParent());
			try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
				w.write("# mcopt extras: set from Options > Video Settings. Edit freely while the game is closed.\n");
				for (Map.Entry<String, String> e : out.entrySet()) w.write(e.getKey() + "=" + e.getValue() + "\n");
			}
		} catch (IOException e) {
			System.out.println("[mcopt-extras] can't write " + file + ": " + e);
		}
	}

	/** True when particles of this type should be spawned. */
	public static boolean particleOn(String id) {
		if (!particles) return false;
		Boolean on = particleTypes.get(id);
		return on == null || on;
	}

	private static java.util.List<Field> settings() {
		java.util.List<Field> list = new java.util.ArrayList<>();
		for (Field f : ExtrasConfig.class.getDeclaredFields()) {
			int m = f.getModifiers();
			if (Modifier.isPublic(m) && Modifier.isStatic(m) && !Modifier.isFinal(m)) list.add(f);
		}
		return list;
	}

	private static Path file() {
		try {
			return FabricLoader.getInstance().getConfigDir().resolve("mcopt-extras.properties");
		} catch (Throwable t) {
			return Path.of("config", "mcopt-extras.properties");
		}
	}
}
