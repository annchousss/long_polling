package ru.itis.chat.controllers;

import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import ru.itis.chat.dto.MessageDto;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.security.basicTokenSecurity.details.UserDetailsImpl;
import ru.itis.chat.services.MessagesService;

import javax.xml.ws.Response;
import java.nio.file.attribute.UserPrincipal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class MessagesController {

    @Autowired
    private MessagesService messagesService;

    //private final Map<String, List<MessageDto>> messages = new HashMap<>();

//    @PostMapping("/messages")
//    public ResponseEntity<Object> receiveMessage(@RequestBody MessageDto message) {
//        if (!messages.containsKey(message.getPageId())) {
//            messages.put(message.getPageId(), new ArrayList<>());
//        }
//        for (List<MessageDto> pageMessages : messages.values()) {
//            synchronized (pageMessages) {
//                pageMessages.add(message);
//                pageMessages.notifyAll();
//            }
//        }
//        return ResponseEntity.ok().build();
//    }


    private final Map<String, List<MessageDto>> messages = new HashMap<>();

    @PostMapping("/messages")
    public ResponseEntity<Object> receiveMessage(@RequestParam("message") String message, Authentication authentication) {
        UserDto userDto = ((UserDetailsImpl) authentication.getDetails()).getUser();
        if (!messages.containsKey(userDto.getToken())) {
            messages.put(userDto.getToken(), new ArrayList<>());
        }
        for (List<MessageDto> pageMessages : messages.values()) {
            synchronized (pageMessages) {
                pageMessages.add(messagesService.sendMessage(userDto, message));
                pageMessages.notifyAll();
            }
        }
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @GetMapping("/messages")
    public ResponseEntity<List<MessageDto>> getMessagesForPage(Authentication authentication) throws InterruptedException {
        UserDto userDto = ((UserDetailsImpl) authentication.getDetails()).getUser();
        synchronized (messages.get(userDto.getToken())) {
            if (messages.get(userDto.getToken()).isEmpty()) {
                messages.get(userDto.getToken()).wait();
            }
            List<MessageDto> response = new ArrayList<>(messages.get(userDto.getToken()));
            messages.get(userDto.getToken()).clear();
            return ResponseEntity.ok(response);
        }
    }




}
