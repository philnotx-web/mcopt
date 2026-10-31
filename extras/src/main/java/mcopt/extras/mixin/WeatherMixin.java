package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.WeatherEffectRenderer;
import net.minecraft.client.renderer.state.level.WeatherRenderState;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Rain and snow not drawn (the weather, its sounds and its darker sky stay). */
@Mixin(WeatherEffectRenderer.class)
abstract class WeatherMixin {
	@Inject(method = "extractRenderState", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$weather(ClientLevel level, float partialTicks, Vec3 cameraPos, WeatherRenderState renderState, CallbackInfo ci) {
		if (ExtrasConfig.weather) return;
		renderState.reset();
		renderState.intensity = 0.0F;
		ci.cancel();
	}
}
