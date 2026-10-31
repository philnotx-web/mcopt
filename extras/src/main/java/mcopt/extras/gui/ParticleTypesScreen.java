package mcopt.extras.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import mcopt.extras.ExtrasConfig;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.layouts.LinearLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

/** One switch per particle type in the game (and in any mod that adds particles), sorted by name. */
public final class ParticleTypesScreen extends OptionsSubScreen {
	public ParticleTypesScreen(Screen lastScreen, Options options) {
		super(lastScreen, options, Component.literal("Particle Types"));
	}

	@Override
	protected void addOptions() {
		List<Identifier> ids = new ArrayList<>(BuiltInRegistries.PARTICLE_TYPE.keySet());
		ids.sort((a, b) -> label(a).compareToIgnoreCase(label(b)));
		List<OptionInstance<?>> opts = new ArrayList<>();
		for (Identifier id : ids) {
			String key = id.toString();
			opts.add(ExtrasOptions.bool(label(id), key, ExtrasConfig.particleTypes.getOrDefault(key, true), v -> ExtrasConfig.particleTypes.put(key, v)));
		}
		this.list.addSmall(opts.toArray(OptionInstance[]::new));
	}

	@Override
	protected void addFooter() {
		LinearLayout row = LinearLayout.horizontal().spacing(8);
		row.addChild(Button.builder(Component.literal("All On"), b -> setAll(true)).width(96).build());
		row.addChild(Button.builder(Component.literal("All Off"), b -> setAll(false)).width(96).build());
		row.addChild(Button.builder(CommonComponents.GUI_DONE, b -> this.onClose()).width(96).build());
		this.layout.addToFooter(row);
	}

	private void setAll(boolean on) {
		for (Identifier id : BuiltInRegistries.PARTICLE_TYPE.keySet()) ExtrasConfig.particleTypes.put(id.toString(), on);
		double scroll = this.list != null ? this.list.scrollAmount() : 0;
		this.layout.removeChildren();
		this.rebuildWidgets();
		if (this.list != null) this.list.setScrollAmount(scroll);
	}

	@Override
	public void removed() {
		ExtrasConfig.save();
		super.removed();
	}

	/** "minecraft:soul_fire_flame" -> "Soul Fire Flame"; other namespaces keep a prefix. */
	static String label(Identifier id) {
		StringBuilder sb = new StringBuilder();
		for (String w : id.getPath().split("[_/]")) {
			if (w.isEmpty()) continue;
			if (!sb.isEmpty()) sb.append(' ');
			sb.append(w.substring(0, 1).toUpperCase(Locale.ROOT)).append(w.substring(1));
		}
		return id.getNamespace().equals("minecraft") ? sb.toString() : id.getNamespace() + ": " + sb;
	}
}
