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

    @PostMapping("/messages") // for sending message to DB
    public ResponseEntity<List<MessageDto>> receiveMessage(@RequestParam("message") String message, Authentication authentication) {
        UserDto userDto = ((UserDetailsImpl) authentication.getDetails()).getUser();
        messagesService.sendMessage(userDto, message);
        return ResponseEntity.ok().build();
    }

    @SneakyThrows
    @GetMapping("/messages") // отрисовка в UI
    public ResponseEntity<List<MessageDto>> getMessagesForPage(Authentication authentication) throws InterruptedException {
        UserDto userDto = ((UserDetailsImpl) authentication.getDetails()).getUser();
        return ResponseEntity.ok(messagesService.getMessagesForPage(userDto));
    }

    @SneakyThrows
    @GetMapping("/messagesAll") // upload history
    public ResponseEntity<List<MessageDto>> getMessagesForPage() throws InterruptedException {
        return ResponseEntity.ok(messagesService.getAllMessages());
    }



}
