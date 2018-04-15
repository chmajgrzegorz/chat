package pl.grzegorzchmaj.chat.services;

import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import pl.grzegorzchmaj.chat.models.UserChatModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class MessageListService {

    @Autowired
    MessageInfoService messageInfoService;

    List<String> messageList = new ArrayList<>();

    public List<String> getMessageList() {
        return messageList;
    }

    public void removeMessage(int index) {
        messageList.remove(index);
    }

    public void addMessage(String message){
        messageList.add(message);
    }

    public void showRecentMessages(UserChatModel userChatModel, TextMessage message) throws IOException {
        if(getMessageList().size() == 30){
            removeMessage(0);
            addMessage(userChatModel.getNickname() + ": " + message.getPayload());
            messageInfoService.sendMessageToAll(userChatModel.getNickname() + ": " + message.getPayload());
            return;
        }
    }
}
