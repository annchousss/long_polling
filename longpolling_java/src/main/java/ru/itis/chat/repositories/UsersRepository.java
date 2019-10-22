package ru.itis.chat.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.itis.chat.models.Role;
import ru.itis.chat.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

@Component
public class UsersRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_USER_BY_ID = "SELECT * from users_table where id = ?";
    private static final String INSERT_USER = "INSERT INTO users_table (username, token, role) VALUES (?, ?, ?)";
    private static final String GET_USER_BY_TOKEN = "SELECT * from users_table where token = ?";


    public Optional<User> readUser(Long id) {
        return Optional.of(jdbcTemplate.queryForObject(GET_USER_BY_ID, rowMapper, id));
    }

    public void saveUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps =
                            connection.prepareStatement(INSERT_USER, new String[] {"id"});
                    ps.setString(1, user.getUsername());
                    ps.setString(2, user.getToken());
                    ps.setString(3, user.getRole().toString());

                    return ps;
                }, keyHolder);
        user.setId(keyHolder.getKey().longValue()); // какое id сгенерила бд для юзера
    }

    public Optional<User> readUserByToken(String token) {
        return Optional.of(jdbcTemplate.queryForObject(GET_USER_BY_TOKEN, rowMapper, token));
    }

    private RowMapper<User> rowMapper = new RowMapper<User>() {
        @Override
        public User mapRow(ResultSet resultSet, int i) throws SQLException {
            return User.builder()
                    .id(resultSet.getLong("id"))
                    .username(resultSet.getString("username"))
                    .token(resultSet.getString("token"))
                    .role(Role.roleFromString(resultSet.getString("role")))
                    .build();
        }
    };
}
