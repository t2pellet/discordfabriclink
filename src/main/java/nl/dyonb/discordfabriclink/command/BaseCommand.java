package nl.dyonb.discordfabriclink.command;

import discord4j.core.object.entity.channel.MessageChannel;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;

public interface BaseCommand {

    // String that activates it
    String commandString();

    // Channel Id
    default String channelId() {
        return DiscordFabricLinkConfig.CONFIG.chatChannelId;
    }

    // Action on activation
    void action(MessageChannel channel);
}
