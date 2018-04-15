package pl.grzegorzchmaj.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import pl.grzegorzchmaj.chat.models.AdminChatModel;
import pl.grzegorzchmaj.chat.models.UserChatModel;

import java.io.IOException;

@Service
public class MessageInfoService {


    @Autowired
    UserListService userListService;

    public boolean checkMessage(UserChatModel userChatModel, TextMessage message,WebSocketSession session) throws IOException {

        if(message.getPayloadLength() == 0){
            userChatModel.sendMessage("Wpisz cokolwiek!");
            return true;
        }

        if(userChatModel.getNickname() == null) {
            if(userListService.getNicknames().contains(message.getPayload()) || message.getPayload().equals("ADMIN")){
                userChatModel.sendMessage("Nick jest zajęty");
                userChatModel.sendMessage("Wpisz swój nick");
            }
            else if(message.getPayload().equals("admin")){
                userChatModel.setNickname(message.getPayload());
                userChatModel.sendMessage("Wpisz haslo");
                return true;
            }
            else{
                userChatModel.setNickname(message.getPayload());
                userChatModel.sendMessage("Ustawiono Twój nick!");
            }
            return true;
        }
        else if(userChatModel.getNickname().equals("admin")){
            if(message.getPayload().equals("password")) {
                AdminChatModel adminChatModel = new AdminChatModel(userChatModel.getSession(), "ADMIN", message.getPayload());
                userListService.getUserList().remove(userListService.findUserBySessionId(session).get());
                userListService.getUserList().add(adminChatModel);
                adminChatModel.sendMessage("Zalogowano");
                return true;
            }
            else {
                userChatModel.sendMessage("Podałeś złe hasło");
                userChatModel.sendMessage("Wpisz swój nick");
                userListService.removeUser(userListService.findUserBySessionId(session).get());
                userListService.addUserToList(new UserChatModel(session));
                return true;
            }
        }
        return false;
    }

    public void sendMessageToAll(String message) throws IOException {
        for (UserChatModel userChatModel : userListService.getUserList()) {
            userChatModel.sendMessage(message);
        }
    }
}
