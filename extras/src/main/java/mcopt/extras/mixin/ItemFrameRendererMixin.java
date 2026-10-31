package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.renderer.entity.ItemFrameRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemFrameRenderer.class)
abstract class ItemFrameRendererMixin {
	@Inject(method = "shouldShowName(Lnet/minecraft/world/entity/decoration/ItemFrame;D)Z", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$name(CallbackInfoReturnable<Boolean> cir) {
		if (!ExtrasConfig.itemFrameNames) cir.setReturnValue(false);
	}
}
