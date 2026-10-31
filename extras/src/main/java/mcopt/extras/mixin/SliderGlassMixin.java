package mcopt.extras.mixin;

import com.mojang.renderpearl.api.pipeline.RenderPipeline;
import mcopt.extras.gui.GlassUi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.resources.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

/** Sliders: a glass track filled up to the value, and a glass handle. */
@Mixin(AbstractSliderButton.class)
abstract class SliderGlassMixin {
	@Shadow protected double value;

	@Redirect(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V", ordinal = 0))
	private void mcoptExtras$track(GuiGraphicsExtractor g, RenderPipeline pipeline, Identifier sprite, int x, int y, int w, int h, int color) {
		if (!GlassUi.active()) {
			g.blitSprite(pipeline, sprite, x, y, w, h, color);
			return;
		}
		AbstractSliderButton self = (AbstractSliderButton) (Object) this;
		GlassUi.sliderTrack(g, x, y, w, h, this.value, self.isActive());
	}

	@Redirect(method = "extractWidgetRenderState", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIIII)V", ordinal = 1))
	private void mcoptExtras$handle(GuiGraphicsExtractor g, RenderPipeline pipeline, Identifier sprite, int x, int y, int w, int h, int color) {
		if (!GlassUi.active()) {
			g.blitSprite(pipeline, sprite, x, y, w, h, color);
			return;
		}
		AbstractSliderButton self = (AbstractSliderButton) (Object) this;
		GlassUi.sliderHandle(g, x, y, w, h, self.isHoveredOrFocused(), self.isActive());
	}
}
