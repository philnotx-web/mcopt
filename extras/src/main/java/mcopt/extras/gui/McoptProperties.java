package mcopt.extras.gui;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.loader.api.FabricLoader;

/**
 * mcopt's own settings file, config/mcopt.properties, read and changed line by line so its comments and any other keys
 * stay as they are. mcopt reads it once at startup: changes here take effect after a restart.
 */
public final class McoptProperties {
	private McoptProperties() {
	}

	public static String get(String key, String fallback) {
		for (String line : read()) {
			String t = line.strip();
			if (t.startsWith(key + "=")) return t.substring(key.length() + 1).strip();
		}
		return fallback;
	}

	public static void set(String key, String value) {
		List<String> lines = new ArrayList<>(read());
		boolean done = false;
		for (int i = 0; i < lines.size(); i++) {
			String t = lines.get(i).strip();
			if (t.startsWith(key + "=")) {
				lines.set(i, key + "=" + value);
				done = true;
				break;
			}
		}
		if (!done) {
			// replace a commented-out example of the same key, else append
			for (int i = 0; i < lines.size(); i++) {
				if (lines.get(i).strip().startsWith("#" + key + "=")) {
					lines.set(i, key + "=" + value);
					done = true;
					break;
				}
			}
		}
		if (!done) lines.add(key + "=" + value);
		try {
			Files.createDirectories(file().getParent());
			Files.write(file(), lines, StandardCharsets.UTF_8);
		} catch (IOException e) {
			System.out.println("[mcopt-extras] can't write " + file() + ": " + e);
		}
	}

	private static List<String> read() {
		try {
			return Files.isRegularFile(file()) ? Files.readAllLines(file(), StandardCharsets.UTF_8) : List.of();
		} catch (IOException e) {
			return List.of();
		}
	}

	private static Path file() {
		return FabricLoader.getInstance().getConfigDir().resolve("mcopt.properties");
	}
}
