package pl.grzegorzchmaj.chat.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Data
@NoArgsConstructor
public class UserChatModel {

    private WebSocketSession session;
    private String nickname;
    private int counter;
    private long time;

    public UserChatModel(WebSocketSession session) {
        this.session = session;
        this.counter = 0;
    }

    public UserChatModel(String nickname) {
        this.nickname = nickname;
    }

    public void sendMessage(String message) throws IOException {
        session.sendMessage(new TextMessage(message));
    }
}
