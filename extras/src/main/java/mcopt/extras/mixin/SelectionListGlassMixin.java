package mcopt.extras.mixin;

import mcopt.extras.gui.GlassUi;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.AbstractSelectionList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** The options list sits on one glass panel; vanilla's list texture and separator lines are left out. */
@Mixin(AbstractSelectionList.class)
abstract class SelectionListGlassMixin {
	@Inject(method = "extractListBackground", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$panel(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (!GlassUi.active()) return;
		AbstractSelectionList<?> self = (AbstractSelectionList<?>) (Object) this;
		int pad = 8;
		int x = self.getRowLeft() - pad, w = self.getRowWidth() + 2 * pad;
		GlassUi.listPanel(graphics, x, self.getY() + 2, w, self.getHeight() - 4);
		ci.cancel();
	}

	@Inject(method = "extractListSeparators", at = @At("HEAD"), cancellable = true)
	private void mcoptExtras$noSeparators(GuiGraphicsExtractor graphics, CallbackInfo ci) {
		if (GlassUi.active()) ci.cancel();
	}
}
