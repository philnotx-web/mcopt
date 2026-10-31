package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.renderer.entity.ArmorStandRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorStandRenderer.class)
abstract class ArmorStandRendererMixin {
	@Inject(method = "shouldShowName(Lnet/minecraft/world/entity/decoration/ArmorStand;D)Z", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$name(CallbackInfoReturnable<Boolean> cir) {
		if (!ExtrasConfig.armorStandNames) cir.setReturnValue(false);
	}
}
