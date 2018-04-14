package pl.grzegorzchmaj.chat.services;

import org.springframework.stereotype.Service;
import pl.grzegorzchmaj.chat.models.UserChatModel;

import java.util.ArrayList;
import java.util.List;

@Service
public class MessageListService {

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
}
