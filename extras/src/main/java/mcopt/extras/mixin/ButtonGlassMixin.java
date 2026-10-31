package mcopt.extras.mixin;

import mcopt.extras.gui.GlassUi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractButton;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Buttons (and cycle buttons such as On/Off options) as glass capsules on the glass-styled screens. */
@Mixin(AbstractButton.class)
abstract class ButtonGlassMixin {
	@Inject(method = "extractDefaultSprite", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$glass(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (!GlassUi.active()) return;
		AbstractButton self = (AbstractButton) (Object) this;
		GlassUi.button(graphics, self.getX(), self.getY(), self.getWidth(), self.getHeight(), self.isActive(), self.isHoveredOrFocused());
		ci.cancel();
	}
}
