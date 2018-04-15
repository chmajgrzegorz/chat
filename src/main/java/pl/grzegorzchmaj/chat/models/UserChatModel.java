package pl.grzegorzchmaj.chat.models;


import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.time.LocalTime;

public class UserChatModel {

    private WebSocketSession session;
    private String nickname;
    private int counter;
    private long time;
    private boolean isBanned = false;
    private LocalTime kickedTime;

    public UserChatModel(WebSocketSession session) {
        this.session = session;
        this.counter = 0;
        this.kickedTime = LocalTime.now().minusMinutes(1);
    }

    public LocalTime getKickedTime() {
        return kickedTime;
    }

    public void setKickedTime(LocalTime kickedTime) {
        this.kickedTime = kickedTime;
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
        this.counter = 0;
        this.kickedTime = LocalTime.now().minusMinutes(1);
    }

    public UserChatModel() {
    }

    public boolean isBanned() {
        return isBanned;
    }

    public void setBanned(boolean banned) {
        isBanned = banned;
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
