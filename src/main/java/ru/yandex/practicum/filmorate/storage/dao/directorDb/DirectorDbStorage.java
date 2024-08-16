package ru.yandex.practicum.filmorate.storage.dao.directorDb;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataRetrievalFailureException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.DirectorMapper;
import ru.yandex.practicum.filmorate.model.Director;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Slf4j
@Setter
@Component
@RequiredArgsConstructor
public class DirectorDbStorage implements DirectorDb {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Director> allDirectors() {
        return jdbcTemplate.query("SELECT * FROM directors", DirectorMapper::mapRow);
    }

    @Override
    public Director createNewDirector(Director director) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(con -> {
            PreparedStatement pr = con.prepareStatement("INSERT INTO directors (director_name) VALUES (?)",
                    Statement.RETURN_GENERATED_KEYS);
            pr.setString(1, director.getName());
            return pr;
        }, keyHolder);

        Number number = keyHolder.getKey();
        director.setId(number.longValue());

        return director;
    }

    @Override
    public Director getDirectorById(Long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT * " +
                            "FROM directors " +
                            "WHERE director_id = ?",
                    DirectorMapper::mapRow, id);
        } catch (DataRetrievalFailureException e) {
            log.error("Директор с id = {} не найден", id);
            throw new NotFoundException("Директор с id = " + id + " не найден");
        }
    }

    @Override
    public Director updateDirector(Director director) {
        jdbcTemplate.update("UPDATE directors " +
                        "SET director_name = ? " +
                        "where director_id = ?",
                director.getName(), director.getId());
        return director;
    }

    @Override
    public void deleteDirectorById(Long id) {
        jdbcTemplate.update("DELETE FROM directors where director_id = ?", id);
    }

}
