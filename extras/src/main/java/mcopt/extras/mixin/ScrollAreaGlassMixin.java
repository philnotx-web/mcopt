package mcopt.extras.mixin;

import mcopt.extras.gui.GlassUi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractScrollArea;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractScrollArea.class)
abstract class ScrollAreaGlassMixin {
	@Shadow protected abstract boolean scrollable();
	@Shadow protected abstract int scrollerHeight();
	@Shadow protected abstract int scrollBarX();
	@Shadow public abstract int scrollBarY();
	@Shadow public abstract int scrollbarWidth();

	@Inject(method = "extractScrollbar", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$scrollbar(GuiGraphicsExtractor graphics, int mouseX, int mouseY, CallbackInfo ci) {
		if (!GlassUi.active()) return;
		AbstractScrollArea self = (AbstractScrollArea) (Object) this;
		if (this.scrollable()) {
			GlassUi.scrollbar(graphics, this.scrollBarX(), self.getY(), this.scrollbarWidth(), self.getHeight(), this.scrollBarY(), this.scrollerHeight());
		}
		ci.cancel();
	}
}
