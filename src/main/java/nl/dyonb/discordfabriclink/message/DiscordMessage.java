package nl.dyonb.discordfabriclink.message;

import discord4j.common.util.Snowflake;
import nl.dyonb.discordfabriclink.DiscordFabricLink;

public class DiscordMessage implements BaseMessage {

    public Snowflake channelId;
    public String message;

    public DiscordMessage(Snowflake channelId, String message) {
        this.channelId = channelId;
        this.message = message;
    }

    @Override
    public void send() {
        DiscordFabricLink.client.getChannelById(this.channelId).block()
                .getRestChannel().createMessage(this.message).block();
    }

}
