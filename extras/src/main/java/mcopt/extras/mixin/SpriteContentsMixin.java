package mcopt.extras.mixin;

import com.mojang.renderpearl.api.buffers.GpuBufferSlice;
import mcopt.extras.Anim;
import net.minecraft.client.renderer.texture.SpriteContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

/** Each animation is tagged with its sprite's group (water, lava, fire, ...) when the atlas creates it. */
@Mixin(SpriteContents.class)
abstract class SpriteContentsMixin {
	@Inject(method = "createAnimationState", at = @At("RETURN"))
	private void mcoptExtras$tag(GpuBufferSlice uboSlice, int spriteUboSize, CallbackInfoReturnable<SpriteContents.AnimationState> cir) {
		SpriteContents.AnimationState state = cir.getReturnValue();
		if (state != null) ((Anim.Tagged) state).mcoptExtras$setGroup(Anim.classify(((SpriteContents) (Object) this).name()));
	}
}
