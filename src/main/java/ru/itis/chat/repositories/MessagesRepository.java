package ru.itis.chat.repositories;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.itis.chat.dto.MessageDto;
import ru.itis.chat.models.Message;
import ru.itis.chat.models.MessageWithUser;
import ru.itis.chat.models.Role;
import ru.itis.chat.models.User;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class MessagesRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private static final String GET_MESSAGE_BY_ID = "SELECT * from messages where id = ?";
    private static final String INSERT_MESSAGE = "INSERT INTO messages (user_id, message) VALUES (?, ?)";

    public Optional<Message> readMessage(Long id) {
        return Optional.of(jdbcTemplate.queryForObject(GET_MESSAGE_BY_ID, rowMapper));
    }

    public void saveMessage(Message message) {
        jdbcTemplate.update(INSERT_MESSAGE, message.getUserId(), message.getMessage());
    }


    private RowMapper<Message> rowMapper = new RowMapper<Message>() {
        @Override
        public Message mapRow(ResultSet resultSet, int i) throws SQLException {
            return Message.builder()
                    .id(resultSet.getLong("id"))
                    .userId(resultSet.getLong("user_id"))
                    .message(resultSet.getString("message"))
                    .build();
        }
    };


}
