package ru.itis.chat.services;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.itis.chat.dto.MessageDto;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.models.Message;
import ru.itis.chat.repositories.MessagesRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MessagesService {
    @Autowired
    private MessagesRepository messagesRepository;

    public MessageDto sendMessage(UserDto userDto, String messageSrting){
        Message message = Message.builder()
                .message(messageSrting)
                .userId(userDto.getId())
                .build();
        messagesRepository.saveMessage(message);

        MessageDto messageDto = MessageDto.builder()
                .message(messageSrting)
                .username(userDto.getUsername())
                .id(message.getId())
                .build();
        return messageDto;

    }

}
