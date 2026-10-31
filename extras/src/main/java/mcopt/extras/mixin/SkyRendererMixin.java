package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.renderer.SkyRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(SkyRenderer.class)
abstract class SkyRendererMixin {
	@Inject(method = "render", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$sky(CallbackInfo ci) {
		if (!ExtrasConfig.sky) ci.cancel();
	}

	@Inject(method = {"renderSun", "renderMoon"}, at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$sunMoon(CallbackInfo ci) {
		if (!ExtrasConfig.sunMoon) ci.cancel();
	}

	@Inject(method = "renderStars", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$stars(CallbackInfo ci) {
		if (!ExtrasConfig.stars) ci.cancel();
	}
}
