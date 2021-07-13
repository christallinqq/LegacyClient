package me.dev.legacy.mixin.mixins;

import me.dev.legacy.Client;
import me.dev.legacy.event.events.KeyEvent;
import me.dev.legacy.features.modules.player.MultiTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.crash.CrashReport;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({Minecraft.class})
public abstract class MixinMinecraft {
   @Inject(
      method = {"shutdownMinecraftApplet"},
      at = {@At("HEAD")}
   )
   private void stopClient(CallbackInfo callbackInfo) {
      this.unload();
   }

   @Redirect(
      method = {"run"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/Minecraft;displayCrashReport(Lnet/minecraft/crash/CrashReport;)V"
)
   )
   public void displayCrashReport(Minecraft minecraft, CrashReport crashReport) {
      this.unload();
   }

   @Inject(
      method = {"runTickKeyboard"},
      at = {@At(
   value = "INVOKE",
   remap = false,
   target = "Lorg/lwjgl/input/Keyboard;getEventKey()I",
   ordinal = 0,
   shift = At.Shift.BEFORE
)}
   )
   private void onKeyboard(CallbackInfo callbackInfo) {
      int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
      if (Keyboard.getEventKeyState()) {
         KeyEvent event = new KeyEvent(i);
         MinecraftForge.EVENT_BUS.post(event);
      }

   }

   private void unload() {
      Client.LOGGER.info("Initiated client shutdown.");
      Client.onUnload();
      Client.LOGGER.info("Finished client shutdown.");
   }

   @Redirect(
      method = {"sendClickBlockToController"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"
)
   )
   private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
      return !MultiTask.getInstance().isOn() && playerSP.func_184587_cr();
   }

   @Redirect(
      method = {"rightClickMouse"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z",
   ordinal = 0
)
   )
   private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
      return !MultiTask.getInstance().isOn() && playerControllerMP.func_181040_m();
   }
}
