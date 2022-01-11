package nl.dyonb.discordfabriclink;

import nl.dyonb.discordfabriclink.message.BaseMessage;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ChatToDiscordThread extends Thread {

    private BlockingQueue<BaseMessage> blockingQueue = new LinkedBlockingQueue();

    @Override
    public void run() {
        try {
            while (true) {
                blockingQueue.take().send();
            }
        } catch (InterruptedException e) {
            DiscordFabricLink.LOGGER.error("Chat to Discord Thread Interrupted. Server stopping?");
        }
    }

    public void addMessage(BaseMessage baseMessage) {
        blockingQueue.add(baseMessage);
    }

}
