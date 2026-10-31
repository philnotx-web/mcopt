package mcopt.extras.mixin;

import mcopt.extras.Anim;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/**
 * A switched-off animation holds its current frame: it neither advances nor asks to be drawn. Both vanilla's atlas upload
 * and mcopt's Metal animation path decide what to draw through needsToDraw, so this works with either.
 */
@Mixin(targets = "net.minecraft.client.renderer.texture.SpriteContents$AnimationState")
abstract class AnimationStateMixin implements Anim.Tagged {
	@Shadow private boolean isDirty;
	@Unique private int mcoptExtras$group;

	@Override
	public int mcoptExtras$group() {
		return this.mcoptExtras$group;
	}

	@Override
	public void mcoptExtras$setGroup(int group) {
		this.mcoptExtras$group = group;
	}

	@Inject(method = "tick", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$hold(CallbackInfo ci) {
		if (!Anim.on(this.mcoptExtras$group)) {
			this.isDirty = false;
			ci.cancel();
		}
	}

	@Inject(method = "needsToDraw", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$noDraw(CallbackInfoReturnable<Boolean> cir) {
		if (!Anim.on(this.mcoptExtras$group)) cir.setReturnValue(false);
	}
}
