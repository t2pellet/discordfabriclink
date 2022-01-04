package nl.dyonb.discordfabriclink.mixin;

import discord4j.common.util.Snowflake;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.message.DiscordEmbed;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;
import nl.dyonb.discordfabriclink.message.DiscordMessage;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(at = @At("TAIL"), method = "sendSystemMessage")
    public void sendSystemMessage(Text message, UUID senderUuid, CallbackInfo ci) {
        if (message instanceof TranslatableText) {
            TranslatableText translatableText = (TranslatableText) message;
            String key = translatableText.getKey();

            if (DiscordFabricLinkConfig.CONFIG.shouldLogKeys) {
                DiscordFabricLink.LOGGER.log(Level.INFO, key);
            }

            // Send to chat
            if (!DiscordFabricLinkConfig.CONFIG.chatChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.chatKeys.contains(key) && !key.startsWith("chat.type.text")) {
                String discordMessage = message.getString();
                MinecraftServer server = (MinecraftServer) ((Object) this);
                String name = discordMessage.substring(0, discordMessage.indexOf(' '));
                PlayerEntity player = server.getPlayerManager().getPlayer(name);
                if (player != null) {
                    DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), discordMessage, player.getUuid()));
                }
            }
            // Send to log
            if (!DiscordFabricLinkConfig.CONFIG.logChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.logKeys.contains(key) && !key.startsWith("chat.type.text")) {
                String discordMessage = message.getString();
                MinecraftServer server = (MinecraftServer) ((Object) this);
                String name = discordMessage.substring(0, discordMessage.indexOf(' '));
                PlayerEntity player = server.getPlayerManager().getPlayer(name);
                if (player != null) {
                    DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(DiscordFabricLinkConfig.CONFIG.logChannelId), discordMessage, player.getUuid()));
                }  else {
                    DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordMessage(Snowflake.of(DiscordFabricLinkConfig.CONFIG.logChannelId), discordMessage));
                }
            }
        }
    }

}
