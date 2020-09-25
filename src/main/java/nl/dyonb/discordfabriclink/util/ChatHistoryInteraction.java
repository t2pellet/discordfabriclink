package nl.dyonb.discordfabriclink.util;

import net.minecraft.text.Text;
import nl.dyonb.chathistory.ChatHistory;
import nl.dyonb.chathistory.util.ChatMessage;

import java.util.UUID;

public class ChatHistoryInteraction {

    public static void addMessage(Text text, UUID uuid) {
        ChatHistory.CHAT_HISTORY.add(new ChatMessage(text, uuid));
    }

}
