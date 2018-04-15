package pl.grzegorzchmaj.chat.models;

import org.apache.tomcat.jni.Local;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import pl.grzegorzchmaj.chat.services.AdminComandsService;
import pl.grzegorzchmaj.chat.services.AdminListService;
import pl.grzegorzchmaj.chat.services.MessageListService;
import pl.grzegorzchmaj.chat.services.UserListService;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Optional;
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

    @Autowired
    AdminComandsService adminComandsService;



    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry webSocketHandlerRegistry) {
        webSocketHandlerRegistry
                .addHandler(this, "/chat")
                .setAllowedOrigins("*");
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        userListService.addUserToList(new UserChatModel(session));

        UserChatModel userChatModel = userListService.findUserBySessionId(session).get();

        for (String s : messageListService.getMessageList()) {
            userChatModel.sendMessage(s);
        }

        userChatModel.sendMessage("Witaj na naszym chacie");
        userChatModel.sendMessage("Wpisz swój nick");


    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        UserChatModel userChatModel = userListService.findUserBySessionId(session).get();

        if(userChatModel.isBanned()){
            userChatModel.sendMessage("Jesteś zbanowany");
            return;
        }
        if(userChatModel.getKickedTime().isAfter(LocalTime.now())){
            userChatModel.sendMessage("Jesteś wykopany, \njeszcze: " + Duration.between(LocalTime.now(), userChatModel.getKickedTime()).getSeconds() + " sekund");
            return;
        }

        if(userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel ).findAny().isPresent()){
            if(userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel ).findAny().get().getSession() == userChatModel.getSession())
            userChatModel = userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel).findAny().get();
        }


        if(message.getPayloadLength() == 0){
            userChatModel.sendMessage("Wpisz cokolwiek!");
            return;
        }

        if(userChatModel.getNickname() == null) {
            if(userListService.getNicknames().contains(message.getPayload()) || message.getPayload().equals("ADMIN")){
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
        else if(message.getPayload().startsWith("/")) {
            if (userChatModel.getNickname().equals("ADMIN")) {
                adminComandsService.adminCommand(userChatModel, message);
                return;
            }
            else{
                    userChatModel.sendMessage("Nie masz dostępu do komend admina");
                    return;
                }
        }
        else if(userChatModel.getNickname().equals("admin")){
            if(message.getPayload().equals("password")) {
                AdminChatModel adminChatModel = new AdminChatModel(userChatModel.getSession(), "ADMIN", message.getPayload());
                userListService.getUserList().remove(userListService.findUserBySessionId(session).get());
                userListService.getUserList().add(adminChatModel);
                adminChatModel.sendMessage("Zalogowano admina");
                return;
            }
            else {
                userChatModel.sendMessage("Podałeś złe hasło");
                userChatModel.sendMessage("Wpisz swój nick");
                userListService.removeUser(userListService.findUserBySessionId(session).get());
                userListService.addUserToList(new UserChatModel(session));
                return;
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
                userChatModel.setKickedTime(LocalTime.now().plusMinutes(1));
                userChatModel.setCounter(0);
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
        if(userChatModel.getNickname().isEmpty())
            return;
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
        userListService.removeUser(userListService.findUserBySessionId(session).get());
    }


}
