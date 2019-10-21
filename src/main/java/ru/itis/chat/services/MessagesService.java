package ru.itis.chat.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.itis.chat.dto.MessageDto;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.models.Message;
import ru.itis.chat.models.MessageWithUser;
import ru.itis.chat.repositories.MessagesRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessagesService {
    @Autowired
    private MessagesRepository messagesRepository;

    private final Map<String, List<MessageDto>> messages = new HashMap<>();

    public void sendMessage(UserDto userDto, String messageSrting){
        Message message = Message.builder()
                .message(messageSrting)
                .userId(userDto.getId())
                .build();

        MessageDto messageDto = MessageDto.builder()
                .message(messageSrting)
                .username(userDto.getUsername())
                .id(message.getId())
                .userToken(userDto.getToken())
                .build();

        if (!messages.containsKey(userDto.getToken())) {
            messages.put(userDto.getToken(), new ArrayList<>());
        }
        messagesRepository.saveMessage(message);

        for (List<MessageDto> pageMessages : messages.values()) {
            synchronized (pageMessages) {
                pageMessages.add(messageDto);
                pageMessages.notifyAll();
            }
        }
    }

    @SneakyThrows
    public List<MessageDto> getMessagesForPage(UserDto userDto){
        synchronized (messages.get(userDto.getToken())) {
            if (messages.get(userDto.getToken()).isEmpty()) {
                messages.get(userDto.getToken()).wait();
            }

            List<MessageDto> response = new ArrayList<>(messages.get(userDto.getToken()));

            messages.get(userDto.getToken()).clear();
            return response;
        }
    }

    public List<MessageDto> getAllMessages(){
        List<MessageDto> messageDtos = new ArrayList<>();

            for(MessageWithUser messageWithUser : messagesRepository.getMessages())
                messageDtos.add(MessageDto.builder()
                        .message(messageWithUser.getMessage())
                        .username(messageWithUser.getUser().getUsername())
                        .id(messageWithUser.getId())
                        .userToken(messageWithUser.getUser().getToken())
                        .build());
            return messageDtos;
    }

}
