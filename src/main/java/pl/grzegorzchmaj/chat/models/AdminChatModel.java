package pl.grzegorzchmaj.chat.models;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.socket.WebSocketSession;
import pl.grzegorzchmaj.chat.services.UserListService;

import java.io.IOException;
import java.time.LocalTime;


public class AdminChatModel extends UserChatModel {

    @Autowired
    UserListService userListService;

    private String password;

    public AdminChatModel(WebSocketSession session, String nickname, String password) {
        super(session, nickname);
        this.password = password;
    }


    @Override
    public void sendMessage(String message) throws IOException {
        super.sendMessage(message);
    }

    public AdminChatModel() {
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void kick(String nick, int howLong){
        userListService.findUserByNickname(nick).get().setKickedTime(LocalTime.now().plusMinutes(howLong));
    }
    public void ban(String nick){
        userListService.findUserByNickname(nick).get().setBanned(true);
    }

}
