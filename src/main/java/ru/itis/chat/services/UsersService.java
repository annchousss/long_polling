package ru.itis.chat.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.models.Role;
import ru.itis.chat.models.User;
import ru.itis.chat.repositories.UsersRepository;

import java.util.Optional;
import java.util.UUID;

@Service
public class UsersService {
    @Autowired
    private UsersRepository usersRepository;

    public UserDto registerUserByToken(String name) {
        // чтобы кинуть токен обратно юзеру
        UserDto userDto = UserDto.builder()
                .username(name)
                .token(UUID.randomUUID().toString())
                .role(Role.USER)
                .build();
        // для бд
        User user = User.builder()
                .username(name)
                .token(userDto.getToken())
                .role(userDto.getRole())
                .build();
        usersRepository.saveUser(user);
        return userDto;
    }

    public UserDto getUserByToken(String token) {
        User user = usersRepository.readUserByToken(token).orElseThrow(IllegalArgumentException::new);

        return UserDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .token(user.getToken())
                .role(user.getRole())
                .build();
    }


}
