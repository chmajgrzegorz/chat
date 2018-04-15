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
import pl.grzegorzchmaj.chat.services.*;

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
    @Autowired
    UserInfoService userInfoService;
    @Autowired
    MessageInfoService messageInfoService;



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

        if(userInfoService.isBannedOrKicked(userChatModel))
            return;

        userInfoService.isAdmin(userChatModel);

        if(messageInfoService.checkMessage(userChatModel, message, session))
            return;

        if(adminComandsService.adminCommand(userChatModel, message))
            return;

        if(userInfoService.isRaisedMaxMessages(userChatModel))
            return;

        messageListService.showRecentMessages(userChatModel,message);

        if(userChatModel.getNickname().isEmpty())
            return;

        messageListService.addMessage(userChatModel.getNickname() + ": " + message.getPayload());
        messageInfoService.sendMessageToAll(userChatModel.getNickname() + ": " + message.getPayload());
    }



    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        userListService.removeUser(userListService.findUserBySessionId(session).get());
    }


}
