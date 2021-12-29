package nl.dyonb.discordfabriclink.mixin;

import discord4j.common.util.Snowflake;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.util.DiscordEmbed;
import nl.dyonb.discordfabriclink.util.DiscordFabricLinkConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // Handle joining separately from other system mesages in MinecraftServerMixin, because we can't get the UUID for a joining player from MinecraftServer::addSystemMessage
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo info) {
        if (Arrays.asList(DiscordFabricLinkConfig.CONFIG.publicKeys).contains("multiplayer")) {
            var name = player.getName();
            var id = player.getUuid();
            DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), name + " joined the game", id));
        }
    }

}
