package nl.dyonb.discordfabriclink;

import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.discordjson.json.WebhookData;
import discord4j.rest.util.Image;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.network.MessageType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.text.LiteralText;
import nl.dyonb.discordfabriclink.command.CommandRegistry;
import nl.dyonb.discordfabriclink.command.StatusCommand;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;
import nl.dyonb.discordfabriclink.message.DiscordMessage;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DiscordFabricLink implements DedicatedServerModInitializer {
    public static ChatToDiscordThread chatToDiscordThread = new ChatToDiscordThread();

    public static final String LOG_ID = "DiscordFabricLink";
    public static final String MOD_ID = "discordfabriclink";

    public static final Logger LOGGER = LogManager.getLogger(LOG_ID);

    public static MinecraftServer minecraftServer;

    public static GatewayDiscordClient client;

    @Override
    public void onInitializeServer() {
        DiscordFabricLinkConfig.initialize();
        LOGGER.log(Level.INFO, "Waiting for Discord4J to login");
        this.initializeD4J();
        chatToDiscordThread.start();
        LOGGER.log(Level.INFO, "Discord4J logged in!");

        if (!DiscordFabricLinkConfig.CONFIG.chatChannelId.isEmpty()) {
            try {
                Snowflake snowflake = Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId);
                ServerLifecycleEvents.SERVER_STARTING.register(minecraftServer -> {
                    DiscordFabricLink.minecraftServer = minecraftServer;
                });
                ServerLifecycleEvents.SERVER_STARTED.register(minecraftServer -> {
                    var msg = ":white_check_mark: **Server started**";
                    chatToDiscordThread.addMessage(new DiscordMessage(snowflake, msg));
                    if (!DiscordFabricLinkConfig.CONFIG.logChannelId.isEmpty()) {
                        chatToDiscordThread.addMessage(new DiscordMessage(Snowflake.of(DiscordFabricLinkConfig.CONFIG.logChannelId), msg));
                    }
                });
                ServerLifecycleEvents.SERVER_STOPPED.register(minecraftServer -> {
                    // Don't use the separate thread to send this message, Otherwise the message won't get sent.
                    var msg = ":octagonal_sign: **Server stopped**";
                    new DiscordMessage(snowflake, msg).send();
                    if (!DiscordFabricLinkConfig.CONFIG.logChannelId.isEmpty()) {
                        new DiscordMessage(Snowflake.of(DiscordFabricLinkConfig.CONFIG.logChannelId), msg).send();
                    }
                    chatToDiscordThread.interrupt();
                    client.logout().block();
                });
            } catch (Exception ex) {
                LOGGER.error("Failed to setup discord bot");
                ex.printStackTrace();
            }
        } else {
            LOGGER.error("Cannot attach discord bot, since the chat channel ID is empty");
        }
    }

    public void initializeD4JWebhooks() {
        // Make a list of webhooks
        String[] channelIds = {DiscordFabricLinkConfig.CONFIG.chatChannelId, DiscordFabricLinkConfig.CONFIG.logChannelId};
        for (var channelId : channelIds) {
            if (channelId.isEmpty()) continue;
            List<WebhookData> list = client.getChannelById(Snowflake.of(channelId)).block().getRestChannel().getWebhooks().collectList().block();
            List<String> webhookNameList = new ArrayList<>();
            list.forEach(webhookData -> {
                webhookNameList.add(webhookData.name().get().toLowerCase());
            });

            // Create the webhook if it doesn't exist
            if (!webhookNameList.contains(DiscordFabricLinkConfig.CONFIG.webhookName.toLowerCase())) {
                TextChannel textChannel = (TextChannel) client.getChannelById(Snowflake.of(channelId)).block();
                textChannel.createWebhook(webhookCreateSpec -> {
                    webhookCreateSpec.setName(DiscordFabricLinkConfig.CONFIG.webhookName)
                            .setAvatar(Image.ofUrl(String.format(DiscordFabricLinkConfig.CONFIG.uuidFaceApi, UUID.randomUUID())).block());
                }).block();
            }
        }
    }

    public void initializeD4J() {
        client = DiscordClientBuilder.create(DiscordFabricLinkConfig.CONFIG.discordBotToken)
                .build()
                .login()
                .block();

        this.initializeD4JWebhooks();

        // Log when ready
        client.getEventDispatcher().on(ReadyEvent.class)
                .subscribe(event -> {
                    User self = event.getSelf();
                    DiscordFabricLink.LOGGER.log(Level.INFO, String.format("Logged in as %s#%s", self.getUsername(), self.getDiscriminator()));
                });

        // Register commands
        CommandRegistry.getInstance().register(
                new StatusCommand()
        );

        // Setup messaging disc -> game
        client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> {
                    return message.getAuthor().isPresent()
                            && !message.getAuthor().get().isBot()
                            && message.getChannelId().equals(Snowflake.of(DiscordFabricLinkConfig.CONFIG.chatChannelId))
                            && !CommandRegistry.getInstance().isPrefix(message.getContent());
                })
                .subscribe(message -> {
                    String formattedString = String.format(DiscordFabricLinkConfig.CONFIG.minecraftChatFormat, message.getAuthor().get().getUsername(), message.getContent());
                    LiteralText literalText = new LiteralText(formattedString);
                    DiscordFabricLink.minecraftServer.getPlayerManager().broadcast(literalText, MessageType.CHAT, UUID.randomUUID());
                });

        client.onDisconnect();
    }
}
