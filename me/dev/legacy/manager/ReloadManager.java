package me.dev.legacy.manager;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.dev.legacy.Client;
import me.dev.legacy.event.events.PacketEvent;
import me.dev.legacy.features.Feature;
import me.dev.legacy.features.command.Command;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReloadManager extends Feature {
   public String prefix;

   public void init(String prefix) {
      this.prefix = prefix;
      MinecraftForge.EVENT_BUS.register(this);
      if (!fullNullCheck()) {
         Command.sendMessage(ChatFormatting.RED + "OyVey has been unloaded. Type " + prefix + "reload to reload.");
      }

   }

   public void unload() {
      MinecraftForge.EVENT_BUS.unregister(this);
   }

   @SubscribeEvent
   public void onPacketSend(PacketEvent.Send event) {
      CPacketChatMessage packet;
      if (event.getPacket() instanceof CPacketChatMessage && (packet = (CPacketChatMessage)event.getPacket()).func_149439_c().startsWith(this.prefix) && packet.func_149439_c().contains("reload")) {
         Client.load();
         event.setCanceled(true);
      }

   }
}
