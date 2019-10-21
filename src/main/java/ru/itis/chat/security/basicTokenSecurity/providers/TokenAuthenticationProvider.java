package ru.itis.chat.security.basicTokenSecurity.providers;

import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ru.itis.chat.controllers.Statics;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.models.Role;
import ru.itis.chat.security.basicTokenSecurity.auth.TokenAuthentication;
import ru.itis.chat.security.basicTokenSecurity.details.UserDetailsImpl;

import java.util.List;
import java.util.Optional;

@Component
public class TokenAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    @Qualifier("customUserDetailsService")
    private UserDetailsService service;

    @Value("${jwt.enabled}")
    private Boolean jwtEnabled;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // делаем явное преобразование для работы с TokenAuthentication
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;

        if (jwtEnabled) {

            String id = Jwts.parser().setSigningKey(Statics.jwtSecret)
                    .parseClaimsJws(tokenAuthentication.getName()).getBody().getSubject();

            if(id != null){
                String role = Jwts.parser()
                        .setSigningKey(Statics.jwtSecret)
                        .parseClaimsJws(tokenAuthentication.getName())
                        .getBody()
                        .get("role", String.class);
                String userName = Jwts.parser()
                        .setSigningKey(Statics.jwtSecret)
                        .parseClaimsJws(tokenAuthentication.getName())
                        .getBody()
                        .get("username", String.class);

                UserDto userDto = UserDto.builder()
                        .id(Long.valueOf(id))
                        .token(tokenAuthentication.getName())
                        .username(userName)
                        .role(Role.roleFromString(role))
                        .build();

                UserDetailsImpl userDetails = new UserDetailsImpl(userDto);
                tokenAuthentication.setUserDetails(userDetails);
                tokenAuthentication.setAuthenticated(true);
            }

        } else {


            UserDetailsImpl userDetails = (UserDetailsImpl) service.loadUserByUsername(tokenAuthentication.getName());


            tokenAuthentication.setUserDetails(userDetails);
            // говорим, что с ним все окей
            tokenAuthentication.setAuthenticated(true);
        }
//        // загружаем данные безопасности пользователя из UserDetailsService
//        // по токену достали пользователя из БД
//        UserDetailsImpl userDetails = (UserDetailsImpl) service.loadUserByUsername(tokenAuthentication.getName());
//        // если данные пришли
//        if (userDetails != null && userDetails.getCurrentToken().isNotExpired()) {
//            // в данный объект аутентификации кладем пользователя
//            tokenAuthentication.setUserDetails(userDetails);
//            // говорим, что с ним все окей
//            tokenAuthentication.setAuthenticated(true);
//        } else {
//            throw new BadCredentialsException("Incorrect Token");
//        }
        // возвращаем объект SecurityContext-у
        return tokenAuthentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return TokenAuthentication.class.equals(authentication);
    }
}