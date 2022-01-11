package nl.dyonb.discordfabriclink.command;

import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.spec.EmbedCreateFields;
import discord4j.core.spec.EmbedCreateSpec;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.network.MessageType;
import net.minecraft.text.LiteralText;
import nl.dyonb.discordfabriclink.DiscordFabricLink;
import nl.dyonb.discordfabriclink.config.DiscordFabricLinkConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandRegistry {

    private static CommandRegistry instance;

    public static CommandRegistry getInstance() {
        if (instance == null) {
            instance = new CommandRegistry();
        }
        return instance;
    }

    private List<BaseCommand> commands;

    private CommandRegistry() {
        commands = new ArrayList<>();
    }

    // Adds commands to be hooked up
    public void register(BaseCommand... commands) {
        for (var command : commands) {
            register(command);
        }
    }

    // Registers command
    public void register(BaseCommand command) {
        commands.add(command);
        DiscordFabricLink.client.getEventDispatcher().on(MessageCreateEvent.class)
                .map(MessageCreateEvent::getMessage)
                .filter(message -> {
                    return message.getAuthor().isPresent()
                            && !message.getAuthor().get().isBot()
                            && message.getChannelId().equals(Snowflake.of(command.channelId()))
                            && message.getContent().toLowerCase().equals(command.commandString());
                })
                .subscribe(msg -> command.action(msg.getChannel().block()));
    }

    // Checks if the given msg is a prefix for any command
    public boolean isPrefix(String msg) {
        return commands.stream().anyMatch(cmd -> cmd.commandString().equals(msg));
    }

}
