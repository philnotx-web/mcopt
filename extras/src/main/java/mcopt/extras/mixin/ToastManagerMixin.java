package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import net.minecraft.client.gui.components.toasts.ToastManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ToastManager.class)
abstract class ToastManagerMixin {
	@Inject(method = "addToast", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$toast(CallbackInfo ci) {
		if (!ExtrasConfig.toasts) ci.cancel();
	}
}
