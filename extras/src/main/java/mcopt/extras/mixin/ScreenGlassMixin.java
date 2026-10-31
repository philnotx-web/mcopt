package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import mcopt.extras.gui.GlassUi;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Marks the frames of the glass-styled screens and gives them a blurred, tinted backdrop instead of the dirt texture. */
@Mixin(Screen.class)
abstract class ScreenGlassMixin {
	@Shadow @Final protected Minecraft minecraft;
	@Shadow public int width;
	@Shadow public int height;

	@Shadow protected abstract void extractPanorama(GuiGraphicsExtractor graphics, float a);

	@Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At("HEAD"))
	private void mcoptExtras$begin(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		GlassUi.begin((Screen) (Object) this);
	}

	@Inject(method = "extractRenderStateWithTooltipAndSubtitles", at = @At("RETURN"))
	private void mcoptExtras$end(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		GlassUi.end();
	}

	@Inject(method = "extractBackground", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$backdrop(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float a, CallbackInfo ci) {
		if (!GlassUi.active()) return;
		if (this.minecraft.level == null) this.extractPanorama(graphics, a);
		if (ExtrasConfig.glassBlur && this.minecraft.options.getMenuBackgroundBlurriness() >= 1.0F) graphics.blurBeforeThisStratum();
		GlassUi.backdrop(graphics, this.width, this.height);
		this.minecraft.gui.hud.extractDeferredSubtitles();
		ci.cancel();
	}
}
