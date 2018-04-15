package pl.grzegorzchmaj.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.grzegorzchmaj.chat.models.AdminChatModel;
import pl.grzegorzchmaj.chat.models.UserChatModel;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalTime;

@Service
public class UserInfoService {

    @Autowired
    UserListService userListService;

    public boolean isBannedOrKicked(UserChatModel userChatModel) throws IOException {

        if(userChatModel.isBanned()){
            userChatModel.sendMessage("Jesteś zbanowany");
            return true;
        }
        if(userChatModel.getKickedTime().isAfter(LocalTime.now())){
            userChatModel.sendMessage("Jesteś wykopany, \njeszcze: " + Duration.between(LocalTime.now(), userChatModel.getKickedTime()).getSeconds() + " sekund");
            return true;
        }
        return false;
    }

    public void isAdmin(UserChatModel userChatModel) {
        if (userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel).findAny().isPresent()) {
            if (userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel).findAny().get().getSession() == userChatModel.getSession())
                userChatModel = userListService.getUserList().stream().filter(s -> s instanceof AdminChatModel).findAny().get();
        }
    }

    public boolean isRaisedMaxMessages(UserChatModel userChatModel){
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
                return true;
            }
        }
        else{
            userChatModel.setCounter(0);
            userChatModel.setTime(System.currentTimeMillis());
        }
        return false;
    }




}
