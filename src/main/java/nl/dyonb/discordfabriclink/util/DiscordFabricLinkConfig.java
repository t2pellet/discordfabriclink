package nl.dyonb.discordfabriclink.util;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.JanksonConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;
import nl.dyonb.discordfabriclink.DiscordFabricLink;

import java.util.List;

@Config(name = DiscordFabricLink.MOD_ID)
public class DiscordFabricLinkConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static DiscordFabricLinkConfig CONFIG = new DiscordFabricLinkConfig();

    @Comment("Bot token")
    public String discordBotToken = "";

    @Comment("Chatting channel ID")
    public String chatChannelId = "";

    @Comment("Log channel ID (optional)")
    public String logChannelId = "";

    @Comment("The message keys that will be sent to the chat channel")
    public List<String> chatKeys = List.of("death", "multiplayer", "chat.type.text", "chat.type.advancement", "chat.type.announcement");

    @Comment("The message keys that will be sent to the log channel. Ignores chat.type.text")
    public List<String> logKeys = List.of("death", "multiplayer", "chat.type.advancement", "chat.type.announcement", "chat.type.admin");

    @Comment("Log the keys")
    public boolean shouldLogKeys = false;

    @Comment("Webhook name")
    public String webhookName = "Discord Fabric Link";

    @Comment("The format to use in the Minecraft chat")
    public String minecraftChatFormat = "[%s] %s";

    @Comment("UUID face API")
    public String uuidFaceApi = "https://crafatar.com/avatars/%s?overlay&size=128";

    public static void initialize() {
        AutoConfig.register(DiscordFabricLinkConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DiscordFabricLinkConfig.class).getConfig();
    }

}
