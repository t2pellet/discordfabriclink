package nl.dyonb.discordfabriclink.mixin;

import discord4j.common.util.Snowflake;
import net.minecraft.network.packet.c2s.play.ChatMessageC2SPacket;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.util.DiscordFabricLinkConfig;
import nl.dyonb.discordfabriclink.util.DiscordWebhook;
import org.apache.commons.lang3.StringUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(ServerPlayNetworkHandler.class)
public class ServerPlayNetworkHandlerMixin {

    @Shadow public ServerPlayerEntity player;

    @Inject(at = @At(value = "INVOKE", target = "Lnet/minecraft/server/PlayerManager;broadcastChatMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/MessageType;Ljava/util/UUID;)V"), method = "onGameMessage")
    public void onGameMessage(ChatMessageC2SPacket packet, CallbackInfo ci) {
        if (Arrays.stream(DiscordFabricLinkConfig.CONFIG.publicKeys).anyMatch("chat.type.text"::equals)) {
            String chatMessage = StringUtils.normalizeSpace(packet.getChatMessage());
            String playerName = this.player.getName().asString();

            DiscordWebhook discordWebhook = new DiscordWebhook(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), chatMessage, playerName, this.player.getUuid());
            DiscordFabricLink.chatToDiscordThread.addMessage(discordWebhook);
        }
    }

}
