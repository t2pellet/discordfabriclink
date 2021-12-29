package nl.dyonb.discordfabriclink.mixin;

import com.mojang.authlib.GameProfileRepository;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.datafixers.DataFixer;
import discord4j.common.util.Snowflake;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerTask;
import net.minecraft.server.WorldGenerationProgressListenerFactory;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.UserCache;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.world.SaveProperties;
import net.minecraft.world.level.storage.LevelStorage;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.util.DiscordEmbed;
import nl.dyonb.discordfabriclink.util.DiscordFabricLinkConfig;
import nl.dyonb.discordfabriclink.util.DiscordMessage;
import nl.dyonb.discordfabriclink.util.DiscordWebhook;
import org.apache.logging.log4j.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.net.Proxy;
import java.util.UUID;

@Environment(EnvType.SERVER)
@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin {

    @Inject(at = @At("TAIL"), method = "sendSystemMessage")
    public void sendSystemMessage(Text message, UUID senderUuid, CallbackInfo ci) {
        if (message instanceof TranslatableText) {
            TranslatableText translatableText = (TranslatableText) message;
            String key = translatableText.getKey();

            if (DiscordFabricLinkConfig.CONFIG.logKeys) {
                DiscordFabricLink.LOGGER.log(Level.INFO, key);
            }

            // Check if the key is in publicKeys
            for (String publicKey : DiscordFabricLinkConfig.CONFIG.publicKeys) {
                if (key.startsWith(publicKey)) {
                    String discordMessage = message.getString();
                    if (!key.startsWith("chat.type.text")) {
                        MinecraftServer server = (MinecraftServer) ((Object) this);
                        String name = discordMessage.substring(0, discordMessage.indexOf(' '));
                        PlayerEntity player = server.getPlayerManager().getPlayer(name);
                        if (player != null) {
                            DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordEmbed(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), discordMessage, player.getUuid()));
                        }
                    }
                }
            }

        }
    }

}
