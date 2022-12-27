package nl.dyonb.discordfabriclink.mixin;

import discord4j.common.util.Snowflake;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.message.MessageSourceProfile;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableTextContent;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.message.DiscordEmbed;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;
import nl.dyonb.discordfabriclink.message.DiscordMessage;
import org.apache.logging.log4j.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Predicate;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // Handle joining separately from other system mesages in MinecraftServerMixin, because we can't get the UUID for a joining player from MinecraftServer::addSystemMessage
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        if (!DiscordFabricLinkConfig.CONFIG.chatChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.chatKeys.contains("multiplayer")) {
            sendJoinMsg(player, DiscordFabricLinkConfig.CONFIG.chatChannelId);
        }
        if (!DiscordFabricLinkConfig.CONFIG.logChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.logKeys.contains("multiplayer")) {
            sendJoinMsg(player, DiscordFabricLinkConfig.CONFIG.logChannelId);
        }
    }

    @Inject(at = @At("RETURN"), method = "remove")
    private void remove(ServerPlayerEntity player, CallbackInfo info) {
        if (DiscordFabricLinkConfig.CONFIG.chatKeys.contains("multiplayer")) {
            sendLeaveMsg(player, DiscordFabricLinkConfig.CONFIG.chatChannelId);
        }
        if (!DiscordFabricLinkConfig.CONFIG.logChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.logKeys.contains("multiplayer")) {
            sendLeaveMsg(player, DiscordFabricLinkConfig.CONFIG.logChannelId);
        }
    }

    @Inject(at = @At("RETURN"), method="broadcast(Lnet/minecraft/network/message/SignedMessage;Ljava/util/function/Predicate;Lnet/minecraft/server/network/ServerPlayerEntity;Lnet/minecraft/network/message/MessageSourceProfile;Lnet/minecraft/network/message/MessageType$Parameters;)V")
    private void onMessage(SignedMessage message, Predicate<ServerPlayerEntity> predicate, ServerPlayerEntity player, MessageSourceProfile profile, MessageType.Parameters parameters, CallbackInfo ci) {
        if (message.getContent().getContent() instanceof TranslatableTextContent translatableText) {
            String key = translatableText.getKey();

            if (DiscordFabricLinkConfig.CONFIG.shouldLogKeys) {
                DiscordFabricLink.LOGGER.log(Level.INFO, key);
            }
            // Send to chat
            if (!DiscordFabricLinkConfig.CONFIG.chatChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.chatKeys.contains(key) && !key.startsWith("chat.type.text")) {
                String discordMessage = message.getContent().getString();
                if (player != null) {
                    DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), discordMessage, player.getUuid()));
                }
            }
            // Send to log
            if (!DiscordFabricLinkConfig.CONFIG.logChannelId.isEmpty() && DiscordFabricLinkConfig.CONFIG.logKeys.contains(key) && !key.startsWith("chat.type.text")) {
                String discordMessage = message.getContent().getString();
                if (player != null) {
                    DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(DiscordFabricLinkConfig.CONFIG.logChannelId), discordMessage, player.getUuid()));
                }  else {
                    DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordMessage(Snowflake.of(DiscordFabricLinkConfig.CONFIG.logChannelId), discordMessage));
                }
            }
        }
    }

    private void sendJoinMsg(ServerPlayerEntity player, String channelId) {
        var name = player.getName().getString();
        var id = player.getUuid();
        DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(channelId), name + " joined the game", id));
    }

    private void sendLeaveMsg(ServerPlayerEntity player, String channelId) {
        var name = player.getName().getString();
        var id = player.getUuid();
        DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(channelId), name + " left the game", id));
    }

}
