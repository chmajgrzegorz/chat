package pl.grzegorzchmaj.chat.models;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSocket
@Component
public class ChatSocket extends TextWebSocketHandler implements WebSocketConfigurer {


    List<UserChatModel> userList = new ArrayList<>();
    List<String> messageList = new ArrayList<>();



    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(this, "/chat")
                .setAllowedOrigins("*");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        userList.add(new UserChatModel(session));

        UserChatModel userChatModel = findUserBySessionId(session);

        userChatModel.sendMessage("Witaj na naszym chacie");
        userChatModel.sendMessage("Wpisz swój nick");

        for (String s : messageList) {
            userChatModel.sendMessage(s);
        }


    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        UserChatModel userChatModel = findUserBySessionId(session);
        if(message.getPayloadLength() == 0){
            userChatModel.sendMessage("Wpisz cokolwiek!");
            return;
        }

        if(userChatModel.getNickname() == null) {
            if(getNicknames().contains(message.getPayload())){
                userChatModel.sendMessage("Nick jest zajęty");
                userChatModel.sendMessage("Wpisz swój nick");
            }
            else{
                userChatModel.setNickname(message.getPayload());
                userChatModel.sendMessage("Ustawiono Twój nick!");
            }

            return;
        }



        if(userChatModel.getCounter() == 0) {
            userChatModel.setTime(System.currentTimeMillis());
            userChatModel.setCounter(userChatModel.getCounter() + 1);
        }
        else if(System.currentTimeMillis() - userChatModel.getTime() < 60000 ){
            if(userChatModel.getCounter() < 29)
                userChatModel.setCounter(userChatModel.getCounter() + 1);
            else {
                userChatModel.sendMessage("Nie mozesz wysylac wiadomosci");
                return;
            }
        }
        else{
            userChatModel.setCounter(0);
            userChatModel.setTime(System.currentTimeMillis());
        }


        if(messageList.size() == 30){
            messageList.remove(0);
            messageList.add(userChatModel.getNickname() + ": " + message.getPayload());
            sendMessageToAll(userChatModel.getNickname() + ": " + message.getPayload());
            return;
        }
        messageList.add(userChatModel.getNickname() + ": " + message.getPayload());
        sendMessageToAll(userChatModel.getNickname() + ": " + message.getPayload());
    }



    private void sendMessageToAll(String message) throws IOException {
        for (UserChatModel userChatModel : userList) {
            userChatModel.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userList.remove(findUserBySessionId(session));
    }

    private UserChatModel findUserBySessionId(WebSocketSession session){
        return userList.stream().filter(s -> s.getSession().equals(session)).findAny().get();
    }

    private List<String> getNicknames(){
        return userList.stream().map(s -> s.getNickname()).collect(Collectors.toList());
    }

    

}
