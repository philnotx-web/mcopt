package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.renderer.blockentity.BeaconRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(BeaconRenderer.class)
abstract class BeaconRendererMixin {
	@Inject(method = "submit", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$beam(CallbackInfo ci) {
		if (!ExtrasConfig.beaconBeam) ci.cancel();
	}
}
