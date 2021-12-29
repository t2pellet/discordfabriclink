package nl.dyonb.discordfabriclink.util;

import discord4j.common.util.Snowflake;
import discord4j.discordjson.json.EmbedAuthorData;
import discord4j.discordjson.json.EmbedData;
import nl.dyonb.discordfabriclink.DiscordFabricLink;

import java.util.UUID;

public class DiscordEmbed implements BaseMessage {

    public Snowflake channelId;
    public String message;
    public UUID uuid;

    public DiscordEmbed(Snowflake channelId, String message, UUID uuid) {
        this.channelId = channelId;
        this.message = message;
        this.uuid = uuid;
    }

    @Override
    public void send() {
        var author = EmbedAuthorData.builder()
                .name(message)
                .iconUrl(DiscordFabricLinkConfig.CONFIG.getUuidUrl(this.uuid))
                .build();
        var data = EmbedData.builder()
                .author(author)
                .build();
        DiscordFabricLink.client.getChannelById(this.channelId).block()
                .getRestChannel().createMessage(data).block();
    }
}
