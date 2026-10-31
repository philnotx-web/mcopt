package mcopt.extras.mixin;

import java.util.IdentityHashMap;
import java.util.Map;
import mcopt.extras.ExtrasConfig;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Particle switches: the master switch, rain splashes and the per-type list. */
@Mixin(ParticleEngine.class)
abstract class ParticleEngineMixin {
	@Unique private static final Map<ParticleType<?>, String> mcoptExtras$ids = new IdentityHashMap<>();

	@Inject(method = "createParticle", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$filter(ParticleOptions options, double x, double y, double z, double xa, double ya, double za,
		CallbackInfoReturnable<Particle> cir) {
		if (!ExtrasConfig.particles) {
			cir.setReturnValue(null);
			return;
		}
		String id = mcoptExtras$ids.computeIfAbsent(options.getType(), t -> {
			Identifier key = BuiltInRegistries.PARTICLE_TYPE.getKey(t);
			return key == null ? "" : key.toString();
		});
		if ((!ExtrasConfig.rainSplash && id.equals("minecraft:rain")) || !ExtrasConfig.particleOn(id)) cir.setReturnValue(null);
	}

	@Inject(method = "add", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$master(Particle particle, CallbackInfo ci) {
		if (!ExtrasConfig.particles) ci.cancel();
	}
}
