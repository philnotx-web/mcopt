package mcopt.extras.mixin;

import mcopt.extras.ExtrasConfig;
import mcopt.extras.gui.ExtrasOptions;
import net.minecraft.client.Options;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.options.OptionsSubScreen;
import net.minecraft.client.gui.screens.options.VideoSettingsScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/** Vanilla's Video Settings keeps its layout and sections; mcopt's sections follow below its Preferences. */
@Mixin(VideoSettingsScreen.class)
abstract class VideoSettingsScreenMixin extends OptionsSubScreen {
	VideoSettingsScreenMixin(Screen lastScreen, Options options, Component title) {
		super(lastScreen, options, title);
	}

	@Inject(method = "addOptions", at = @At("TAIL"))
	private void mcoptExtras$addSections(CallbackInfo ci) {
		if (this.list != null) ExtrasOptions.addSections(this.list, this, this.options);
	}

	@Inject(method = "removed", at = @At("HEAD"))
	private void mcoptExtras$save(CallbackInfo ci) {
		ExtrasConfig.save();
	}
}
