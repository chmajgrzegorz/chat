package pl.grzegorzchmaj.chat.models;

import org.apache.tomcat.jni.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.grzegorzchmaj.chat.services.AdminListService;
import pl.grzegorzchmaj.chat.services.MessageListService;
import pl.grzegorzchmaj.chat.services.UserListService;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@EnableWebSocket
@Component
public class ChatSocket extends TextWebSocketHandler implements WebSocketConfigurer {



    @Autowired
    UserListService userListService;

    @Autowired
    AdminListService adminListService;

    @Autowired
    MessageListService messageListService;



    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(this, "/chat")
                .setAllowedOrigins("*");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        userListService.addUserToList(new UserChatModel(session));

        UserChatModel userChatModel = userListService.findUserBySessionId(session);

        for (String s : messageListService.getMessageList()) {
            userChatModel.sendMessage(s);
        }

        userChatModel.sendMessage("Witaj na naszym chacie");
        userChatModel.sendMessage("Wpisz swój nick");




    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        UserChatModel userChatModel = userListService.findUserBySessionId(session);
        System.out.println(userChatModel);

        if(userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel ).findAny().isPresent()){
            userChatModel = userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel).findAny().get();
            System.out.println(userChatModel);
        }


        if(message.getPayloadLength() == 0){
            userChatModel.sendMessage("Wpisz cokolwiek!");
            return;
        }

        if(userChatModel.getNickname() == null) {
            if(userListService.getNicknames().contains(message.getPayload())){
                userChatModel.sendMessage("Nick jest zajęty");
                userChatModel.sendMessage("Wpisz swój nick");
            }
            else if(message.getPayload().equals("admin")){
                   userChatModel.setNickname(message.getPayload());
                userChatModel.sendMessage("Wpisz haslo");
                return;
            }
            else{
                userChatModel.setNickname(message.getPayload());
                userChatModel.sendMessage("Ustawiono Twój nick!");
            }
            return;
        }

        else if(userChatModel.getNickname().equals("admin")){
            if(message.getPayload().equals("password")) {
                AdminChatModel adminChatModel = new AdminChatModel(userChatModel.getSession(), "ADMIN", message.getPayload());
                System.out.println(adminChatModel + "Sorawdzenie");
                userListService.getUserList().remove(userListService.findUserBySessionId(session));
                userListService.getUserList().add(adminChatModel);
                adminChatModel.sendMessage("Zalogowano admina");
                return;
            }
            else {
                userChatModel.sendMessage("Podałeś złe hasło");
                userChatModel.sendMessage("Wpisz swój nick");
                userListService.removeUser(userListService.findUserBySessionId(session));
                userListService.addUserToList(new UserChatModel(session));
            }
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


        if(messageListService.getMessageList().size() == 30){
            messageListService.removeMessage(0);
            messageListService.addMessage(userChatModel.getNickname() + ": " + message.getPayload());
            sendMessageToAll(userChatModel.getNickname() + ": " + message.getPayload());
            return;
        }
        messageListService.addMessage(userChatModel.getNickname() + ": " + message.getPayload());
        sendMessageToAll(userChatModel.getNickname() + ": " + message.getPayload());
    }



    private void sendMessageToAll(String message) throws IOException {
        for (UserChatModel userChatModel : userListService.getUserList()) {
            userChatModel.sendMessage(message);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userListService.removeUser(userListService.findUserBySessionId(session));
    }


}
