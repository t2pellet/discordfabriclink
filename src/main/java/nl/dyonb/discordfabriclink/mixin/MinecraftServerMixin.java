package nl.dyonb.discordfabriclink.mixin;

import discord4j.common.util.Snowflake;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.util.DiscordFabricLinkConfig;
import nl.dyonb.discordfabriclink.util.DiscordMessage;
import nl.dyonb.discordfabriclink.util.DiscordWebhook;
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

            if (DiscordFabricLinkConfig.CONFIG.logKeys) {
                DiscordFabricLink.LOGGER.log(Level.INFO, key);
            }

            // Check if the key is in publicKeys
            for (String publicKey : DiscordFabricLinkConfig.CONFIG.publicKeys) {
                if (key.startsWith(publicKey)) {
                    String discordMessage = message.getString();

                    if (key.startsWith("chat.type.text")) {
                        // The most horrible hacky stuff you have ever seen
                        String name = ((LiteralText) ((TranslatableText) message).getArgs()[0]).asString();
                        String actualMessage = ((String) ((TranslatableText) message).getArgs()[1]);

                        DiscordWebhook discordWebhook = new DiscordWebhook(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), actualMessage, name, senderUuid);
                        DiscordFabricLink.chatToDiscordThread.addMessage(discordWebhook);
                    } else {
                        DiscordFabricLink.chatToDiscordThread.addMessage(new DiscordMessage(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId), discordMessage));
                    }
                }
            }

        }
    }

}
