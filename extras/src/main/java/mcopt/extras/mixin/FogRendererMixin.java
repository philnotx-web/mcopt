package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.Camera;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.fog.FogData;
import net.minecraft.client.renderer.fog.FogRenderer;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.material.FogType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * Fog controls, applied to the finished fog (after mcopt's far-terrain fog, which runs at priority 500). Blindness and
 * Darkness are left alone unless Effect Fog is off.
 */
@Mixin(value = FogRenderer.class, priority = 1100)
abstract class FogRendererMixin {
	@Unique private static final float FAR = 1.0E7F;

	@Inject(method = "setupFog", at = @At("RETURN"))
	private void mcoptExtras$fog(Camera camera, int renderDistanceInChunks, DeltaTracker deltaTracker, float darkenWorldAmount, ClientLevel level,
		CallbackInfoReturnable<FogData> cir) {
		FogData fog = cir.getReturnValue();
		if (!ExtrasConfig.fogRemoveEffects && camera.entity() instanceof LivingEntity living
			&& (living.hasEffect(MobEffects.BLINDNESS) || living.hasEffect(MobEffects.DARKNESS))) {
			return;
		}
		FogType fluid = camera.getFluidInCamera();
		boolean effect = camera.entity() instanceof LivingEntity living2 && (living2.hasEffect(MobEffects.BLINDNESS) || living2.hasEffect(MobEffects.DARKNESS));

		// distance fog at the render distance's edge
		if (!ExtrasConfig.fogTerrain) {
			fog.renderDistanceStart = FAR;
			fog.renderDistanceEnd = FAR + 1;
		} else if (ExtrasConfig.fogStartPercent != 90) {
			fog.renderDistanceStart = Math.min(fog.renderDistanceEnd - 1, fog.renderDistanceEnd * ExtrasConfig.fogStartPercent / 100.0F);
		}

		switch (fluid) {
			case WATER -> {
				float k = ExtrasConfig.fogWaterPercent / 100.0F;
				fog.environmentalEnd *= k;
				if (fog.environmentalStart > 0) fog.environmentalStart *= k;
			}
			case LAVA -> {
				if (!ExtrasConfig.fogLava) clear(fog);
			}
			case POWDER_SNOW -> {
				if (!ExtrasConfig.fogPowderSnow) clear(fog);
			}
			default -> {
				if (effect && ExtrasConfig.fogRemoveEffects) clear(fog);
				else if (!ExtrasConfig.fogEnvironment) clear(fog);
			}
		}
	}

	@Unique
	private static void clear(FogData fog) {
		fog.environmentalStart = FAR;
		fog.environmentalEnd = FAR + 1;
	}
}
