package ru.itis.chat.services;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import ru.itis.chat.controllers.Statics;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.models.Role;
import ru.itis.chat.models.User;
import ru.itis.chat.repositories.UsersRepository;

import java.util.*;

@Service
public class UsersService {

    @Autowired
    private UsersRepository usersRepository;

    @Value("${jwt.enabled}") // from app.props
    private Boolean jwtEnabled;

    public UserDto registerUserByToken(String name) {

        UserDto userDto;

        if(jwtEnabled){
            // для бд
            User user = User.builder()
                    .username(name)
                  //  .token(userDto.getToken())
                    .role(Role.USER)
                    .build();
            usersRepository.saveUser(user);

            Map<String, Object> claims = new HashMap<>();

            claims.put("userId", user.getId());
            claims.put("username", user.getUsername());
            claims.put("role", user.getRole().toString());

            String token = Jwts.builder().setClaims(claims) // эта херня кодирует jwt
                    .setSubject(user.getId().toString()) // like ID jwt
                   // .setIssuedAt(createdDate)
                    .setId(claims.get("userId").toString())
                    //.setExpiration(expirationDate)
                    .signWith(SignatureAlgorithm.HS512, Statics.jwtSecret).compact();

            // чтобы кинуть токен обратно юзеру
            userDto = UserDto.builder()
                    .username(user.getUsername())
                    .token(token)
                    .role(user.getRole())
                    .build();

        } else {
            String token = UUID.randomUUID().toString();
            // чтобы кинуть токен обратно юзеру
            userDto = UserDto.builder()
                    .username(name)
                    .token(token)
                    .role(Role.USER)
                    .build();
            // для бд
            User user = User.builder()
                    .username(name)
                    .token(userDto.getToken())
                    .role(userDto.getRole())
                    .build();
            usersRepository.saveUser(user);
        }

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
