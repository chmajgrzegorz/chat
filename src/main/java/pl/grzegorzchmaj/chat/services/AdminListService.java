package pl.grzegorzchmaj.chat.services;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import pl.grzegorzchmaj.chat.models.AdminChatModel;

import java.util.ArrayList;
import java.util.List;

@Service
public class AdminListService {

    List<AdminChatModel> adminList = new ArrayList<>();

    public List<AdminChatModel> getAdminList() {
        return adminList;
    }

    public AdminChatModel findAdminBySessionId(WebSocketSession session){
        return adminList.stream().filter(s -> s.getSession().equals(session)).findAny().get();
    }
}
