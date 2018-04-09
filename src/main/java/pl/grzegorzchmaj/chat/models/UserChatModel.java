package pl.grzegorzchmaj.chat.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

//@Data
//@NoArgsConstructor
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

    public UserChatModel(WebSocketSession session, String nickname) {
        this.session = session;
        this.nickname = nickname;
    }

    public UserChatModel() {
    }

    public WebSocketSession getSession() {
        return session;
    }

    public void setSession(WebSocketSession session) {
        this.session = session;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "UserChatModel{" +
                "session=" + session +
                ", nickname='" + nickname + '\'' +
                ", counter=" + counter +
                ", time=" + time +
                '}';
    }
}
