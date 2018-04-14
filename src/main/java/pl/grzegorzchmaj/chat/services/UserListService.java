package pl.grzegorzchmaj.chat.services;

import org.apache.tomcat.jni.User;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.grzegorzchmaj.chat.models.UserChatModel;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserListService {

    List<UserChatModel> userList = new ArrayList<>();

    public List<UserChatModel> getUserList() {
        return userList;
    }

    public void addUserToList(UserChatModel userChatModel){
        userList.add(userChatModel);
    }

    public UserChatModel findUserBySessionId(WebSocketSession session){
        return userList.stream().filter(s -> s.getSession().equals(session)).findAny().get();
    }

    public UserChatModel findUserByNickname(String nickname){
        return userList.stream().filter(s -> s.getNickname().equals(nickname)).findAny().get();
    }

    public void removeUser(UserChatModel userChatModel){
        userList.remove(userChatModel);
    }

    public List<String> getNicknames(){
        return userList.stream().map(s -> s.getNickname()).collect(Collectors.toList());
    }
}
