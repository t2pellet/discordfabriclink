package nl.dyonb.discordfabriclink.command;

import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;

public class StatusCommand implements BaseCommand {

    @Override
    public String commandString() {
        return "!status";
    }

    @Override
    public void action(MessageChannel channel) {
        channel.createMessage("Players online: " + DiscordFabricLink.minecraftServer.getCurrentPlayerCount()).block();
        var players = DiscordFabricLink.minecraftServer.getPlayerManager().getPlayerList();
        for (var player : players) {
            var author = EmbedCreateFields.Author.of(player.getName().getString(), null, DiscordFabricLinkConfig.CONFIG.getUuidUrl(player.getUuid()));
            var embed = EmbedCreateSpec.builder().author(author).build();
            channel.createMessage(embed).block();
        }
    }
}
