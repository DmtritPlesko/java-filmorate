package ru.yandex.practicum.filmorate.storage.dao.reviewDb;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.ReviewRowMapper;
import ru.yandex.practicum.filmorate.model.Review;

import java.time.LocalDateTime;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewDbStorage implements ReviewDb {
    private int review_id = 1;
    private final JdbcTemplate jdbcTemplate;
    private final String save = "INSERT INTO feeds (user_id, entity_id, event_type, operation, time_stamp) " +
            "values (?, ?, ?, ?, ?)";
    public Review postReview(Review review) {
        review.setReviewId(review_id);
        review_id++;
        String request = "INSERT INTO reviews(content, positive, user_id, film_id, useful) " +
                "values(?, ?, ?, ?, ?)";
        jdbcTemplate.update(request, review.getContent(), review.getIsPositive(),
                review.getUserId(), review.getFilmId(), review.getUseful());
        jdbcTemplate.update(save, review.getUserId(), review.getReviewId(), "REVIEW", "ADD", LocalDateTime.now());
        return review;
    }

    public Review putReview(Review review) {
        String request = "UPDATE reviews SET " + "content = ?, positive = ?, user_id = ?, film_id = ?, useful = ? " +
                "WHERE id = ?";
        jdbcTemplate.update(request, review.getContent(), review.getIsPositive(), review.getUserId(),
                review.getFilmId(), review.getUseful(), review.getReviewId());
        jdbcTemplate.update(save, review.getUserId(), review.getReviewId(), "REVIEW", "UPDATE",
                LocalDateTime.now());
        return review;
    }

    public Review getReview(long id) {
        try {
            String request = "SELECT * FROM reviews WHERE id = ?";
            return jdbcTemplate.queryForObject(request, new ReviewRowMapper(), id);
        } catch (EmptyResultDataAccessException e) {
            throw new NotFoundException("Комментарий не найден");
        }
    }

    public List<Review> getReviews(long filmId) {
        String request = "SELECT * FROM reviews WHERE film_id = ?";
        return jdbcTemplate.query(request, new ReviewRowMapper(), filmId);
    }

    public List<Review> delReview(long id) {
        Review review = getReview(id);
        String request = "DELETE FROM reviews WHERE id = ?";
        String request1 = "SELECT * FROM reviews";
        jdbcTemplate.update(request, id);
        jdbcTemplate.update(save, review.getUserId(), review.getReviewId(), "REVIEW", "REMOVE",
                LocalDateTime.now());
        return jdbcTemplate.query(request1, new ReviewRowMapper());
    }

    public Review addReviewLike(long id, long userId) {
        Review review = getReview(id);
        String check = "DELETE FROM dislike_reviews WHERE user_id = ? AND review_id = ?";
        int result = jdbcTemplate.update(check, id, userId);
        if (result == 0) {
            review.setUseful(review.getUseful() + 1);
        } else {
            review.setUseful(review.getUseful() + 2);
        }
        putReview(review);
        String request = "INSERT INTO like_reviews(user_id, review_id) " + "values(?, ?)";
        jdbcTemplate.update(request, id, userId);
        return review;
    }

    public Review delReviewLike(long id, long userId) {
        Review review = getReview(id);
        String check = "DELETE FROM like_reviews WHERE user_id = ? AND review_id = ?";
        int result = jdbcTemplate.update(check, id, userId);
        if (result == 1) {
            review.setUseful(review.getUseful() - 1);
        }
        putReview(review);
        String request = "DELETE FROM like_reviews WHERE user_id = ? AND review_id = ?";
        jdbcTemplate.update(request, id, userId);
        return review;
    }

    public Review addReviewDislike(long id, long userId) {
        Review review = getReview(id);
        String check = "DELETE FROM like_reviews WHERE user_id = ? AND review_id = ?";
        int result = jdbcTemplate.update(check, id, userId);
        if (result == 1) {
            review.setUseful(review.getUseful() - 2);
        } else {
            review.setUseful(review.getUseful() - 1);
        }
        putReview(review);
        String request = "INSERT INTO dislike_reviews(user_id, review_id) " + "values(?, ?)";
        jdbcTemplate.update(request, id, userId);
        return review;
    }
}
