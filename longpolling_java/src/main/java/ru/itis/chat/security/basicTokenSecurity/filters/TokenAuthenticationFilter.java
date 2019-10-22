package ru.itis.chat.security.basicTokenSecurity.filters;

import org.springframework.security.core.context.SecurityContextHolder;
import ru.itis.chat.dto.UserDto;
import ru.itis.chat.security.basicTokenSecurity.auth.TokenAuthentication;
import ru.itis.chat.security.basicTokenSecurity.details.UserDetailsImpl;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class TokenAuthenticationFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        // вытаскиваем запрос
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        // вытаскиваем заголовок с токеном
        String token = request.getHeader("Authorization"); // ACTUAL
        // если заголовок содержит что-либо
        if (token != null) {
            // создаем объект токен-аутентификации
            TokenAuthentication authentication = new TokenAuthentication();
            UserDto userDto = UserDto.builder().token(token).build(); // TOKEN ONLY
            UserDetailsImpl userDetails = new UserDetailsImpl(userDto);
            authentication.setUserDetails(userDetails);
            // в него кладем токен
            // отдаем контексту
            SecurityContextHolder.getContext().setAuthentication(authentication); // TokenAuthentication кастится в Authentication
        }
        // отдаем запрос дальше (его встретит либо другой фильтр, либо что-то еще)
        filterChain.doFilter(servletRequest, servletResponse);
    }
}
