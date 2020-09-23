package nl.dyonb.discordfabriclink.util;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.WebhookData;
import discord4j.rest.util.AllowedMentions;
import nl.dyonb.discordfabriclink.DiscordFabricLink;

import java.util.UUID;

public class DiscordWebhook implements BaseMessage {

    public Snowflake channelId;
    public String message;
    public String username;
    public UUID uuid;

    public DiscordWebhook(Snowflake channelId, String message, String username, UUID uuid) {
        this.channelId = channelId;
        this.message = message;
        this.username = username;
        this.uuid = uuid;
    }

    @Override
    public void send() {
        WebhookData webhook = DiscordFabricLink.client.getChannelById(this.channelId).block()
                .getRestChannel().getWebhooks()
                .filter(webhookData -> webhookData.name().get().toLowerCase().equals(DiscordFabricLinkConfig.CONFIG.webhookName.toLowerCase()))
                .blockFirst();

        DiscordFabricLink.client.getWebhookByIdWithToken(Snowflake.of(webhook.id()), webhook.token().get()).block()
                .execute(webhookExecuteSpec -> {
                    webhookExecuteSpec.setUsername(this.username).setContent(this.message)
                            .setAvatarUrl(String.format(DiscordFabricLinkConfig.CONFIG.uuidFaceApi, this.uuid.toString()))
                            .setAllowedMentions(AllowedMentions.builder().parseType(AllowedMentions.Type.USER).build());
                }).block();
    }

}
