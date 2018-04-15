package pl.grzegorzchmaj.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.TextMessage;
import pl.grzegorzchmaj.chat.models.UserChatModel;

import java.io.IOException;
import java.time.LocalTime;
import java.util.stream.Collectors;

@Component
public class AdminComandsService {


    @Autowired
    UserListService userListService;

    public void adminCommand(UserChatModel userChatModel, TextMessage message) throws IOException {
        if (message.getPayload().matches("\\/kick \\w* [1-9]\\d?")) {
            String data[] = message.getPayload().split(" ");
            if (userListService.findUserByNickname(data[1]).isPresent()) {
                userListService.findUserByNickname(data[1]).get().setKickedTime(LocalTime.now().plusMinutes(Long.parseLong(data[2])));
                userListService.findUserByNickname(data[1]).get().sendMessage("Zostałeś wykopany na: " + data[2] + " minut");
                userChatModel.sendMessage("Wykopano użytkownika");
            } else
                userChatModel.sendMessage("Podaj poprawną nazwe użytownika");
        } else if (message.getPayload().matches("\\/ban \\w*")) {
            String data[] = message.getPayload().split(" ");
            if (userListService.findUserByNickname(data[1]).isPresent()) {
                userListService.findUserByNickname(data[1]).get().setBanned(true);
                userListService.findUserByNickname(data[1]).get().sendMessage("Zostałeś zbanowany");
                userChatModel.sendMessage("Zbanowano użytkownika");
            } else
                userChatModel.sendMessage("Podaj poprawną nazwe użytownika");
        } else if (message.getPayload().matches("\\/unban \\w*")) {
            String data[] = message.getPayload().split(" ");
            if (userListService.findUserByNickname(data[1]).isPresent()) {
                userListService.findUserByNickname(data[1]).get().setBanned(false);
                userListService.findUserByNickname(data[1]).get().sendMessage("Zostałeś odbanowany");
                userChatModel.sendMessage("Odbanowano użytkownika");
            } else
                userChatModel.sendMessage("Podaj poprawną nazwe użytownika");
        } else if (message.getPayload().equals("/showusers")) {
            userChatModel.sendMessage(userListService.getUserList()
                    .stream()
                    .map(s -> s.getNickname())
                    .collect(Collectors.joining(", \n", "Użytkownicy: ", "")));
        } else if (message.getPayload().matches("\\/changenick \\w* \\w*")) {
            String data[] = message.getPayload().split(" ");
            if (userListService.findUserByNickname(data[1]).isPresent()) {
                userListService.findUserByNickname(data[1]).get().setNickname(data[2]);
                userListService.findUserByNickname(data[2]).get().sendMessage("Twój nick został zmieniony na " + data[2]);
                userChatModel.sendMessage("Zmieniłeś nick użytkownika");
            } else
                userChatModel.sendMessage("Podaj poprawną nazwe użytownika");
        } else {
            userChatModel.sendMessage("Podaj poprawną komende. Wszystkie komendy:");
            userChatModel.sendMessage("/kick nick jak_długo_[minuty]");
            userChatModel.sendMessage("/ban nick");
            userChatModel.sendMessage("/unban nick");
            userChatModel.sendMessage("/changenick nick nowy_nick");
            userChatModel.sendMessage("/showusers");
        }

    }
}
