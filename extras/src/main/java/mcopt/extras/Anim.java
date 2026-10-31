package mcopt.extras;

import net.minecraft.resources.Identifier;

/** Animated-texture groups the settings switch, and which group a sprite belongs to. */
public final class Anim {
	public static final int OTHER = 0, WATER = 1, LAVA = 2, FIRE = 3, PORTAL = 4, SCULK = 5, BLOCK = 6;

	private Anim() {
	}

	/** Implemented by SpriteContents.AnimationState (mixin): the group its sprite was classified into. */
	public interface Tagged {
		int mcoptExtras$group();

		void mcoptExtras$setGroup(int group);
	}

	public static int classify(Identifier name) {
		String p = name.getPath();
		if (p.startsWith("block/")) {
			String b = p.substring("block/".length());
			if (b.startsWith("water_")) return WATER;
			if (b.startsWith("lava_")) return LAVA;
			if (b.startsWith("fire_") || b.startsWith("soul_fire_") || b.startsWith("campfire_fire") || b.startsWith("soul_campfire_fire")) return FIRE;
			if (b.startsWith("nether_portal") || b.startsWith("end_portal")) return PORTAL;
			if (b.startsWith("sculk")) return SCULK;
			return BLOCK;
		}
		return OTHER;
	}

	/** Whether sprites of this group animate with the current settings. */
	public static boolean on(int group) {
		if (!ExtrasConfig.animations) return group == OTHER; // items, mob effects, GUI keep animating
		return switch (group) {
			case WATER -> ExtrasConfig.animWater;
			case LAVA -> ExtrasConfig.animLava;
			case FIRE -> ExtrasConfig.animFire;
			case PORTAL -> ExtrasConfig.animPortal;
			case SCULK -> ExtrasConfig.animSculk;
			case BLOCK -> ExtrasConfig.animBlocks;
			default -> true;
		};
	}
}
