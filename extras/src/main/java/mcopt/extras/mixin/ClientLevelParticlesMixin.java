package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Block breaking (the burst when a block breaks) and cracking (the bits while mining) particles. */
@Mixin(ClientLevel.class)
abstract class ClientLevelParticlesMixin {
	@Inject(method = "addDestroyBlockEffect", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$destroy(CallbackInfo ci) {
		if (!ExtrasConfig.blockBreakParticles || !ExtrasConfig.particles) ci.cancel();
	}

	@Inject(method = "addBreakingBlockEffects", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$crack(CallbackInfo ci) {
		if (!ExtrasConfig.blockCrackParticles || !ExtrasConfig.particles) ci.cancel();
	}
}
