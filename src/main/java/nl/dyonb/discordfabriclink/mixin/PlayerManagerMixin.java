package nl.dyonb.discordfabriclink.mixin;

import discord4j.common.util.Snowflake;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.message.DiscordEmbed;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    private void sendJoinMsg(ServerPlayerEntity player, String channelId) {
        var name = player.getName().asString();
        var id = player.getUuid();
        DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(channelId), name + " joined the game", id));
    }

    private void sendLeaveMsg(ServerPlayerEntity player, String channelId) {
        var name = player.getName().asString();
        var id = player.getUuid();
        DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(channelId), name + " left the game", id));
    }

}
