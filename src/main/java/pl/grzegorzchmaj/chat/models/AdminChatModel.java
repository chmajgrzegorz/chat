package pl.grzegorzchmaj.chat.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

//@Data
//@NoArgsConstructor
public class AdminChatModel extends UserChatModel {


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

}
