package nl.dyonb.discordfabriclink.util;

import me.sargunvohra.mcmods.autoconfig1u.AutoConfig;
import me.sargunvohra.mcmods.autoconfig1u.ConfigData;
import me.sargunvohra.mcmods.autoconfig1u.annotation.Config;
import me.sargunvohra.mcmods.autoconfig1u.annotation.ConfigEntry;
import me.sargunvohra.mcmods.autoconfig1u.serializer.JanksonConfigSerializer;
import me.sargunvohra.mcmods.autoconfig1u.shadowed.blue.endless.jankson.Comment;
import nl.dyonb.discordfabriclink.DiscordFabricLink;

@Config(name = DiscordFabricLink.MOD_ID)
public class DiscordFabricLinkConfig implements ConfigData {

    @ConfigEntry.Gui.Excluded
    public static DiscordFabricLinkConfig CONFIG = new DiscordFabricLinkConfig();

    @Comment("Bot token")
    public String discordBotToken = "";

    @Comment("Chatting channel ID")
    public String chatChannelId = "";

    @Comment("The message keys that will be sent to the public chat")
    public String[] publicKeys = {"death", "multiplayer", "chat.type.text", "chat.type.advancement", "chat.type.announcement"};

    @Comment("Send admin commands")
    public boolean sendAdminCommands = false;

    @Comment("Log the keys")
    public boolean logKeys = false;

    @Comment("Webhook name")
    public String webhookName = "Discord Fabric Link";

    @Comment("The format to use in the Minecraft chat")
    public String minecraftChatFormat = "<%s> %s";

    @Comment("UUID face API")
    public String uuidFaceApi = "https://crafatar.com/avatars/%s?overlay&size=128";

    public static void initialize() {
        AutoConfig.register(DiscordFabricLinkConfig.class, JanksonConfigSerializer::new);
        CONFIG = AutoConfig.getConfigHolder(DiscordFabricLinkConfig.class).getConfig();
    }

}
