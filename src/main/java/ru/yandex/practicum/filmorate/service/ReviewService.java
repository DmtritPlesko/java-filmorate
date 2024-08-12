package ru.yandex.practicum.filmorate.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exeption.NotFoundException;
import ru.yandex.practicum.filmorate.exeption.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.reviewDb.ReviewDb;

import java.util.List;

@Slf4j
@Service
public class ReviewService {
    private ReviewDb reviewDb;

    @Autowired
    public ReviewService(ReviewDb reviewDb) {
        this.reviewDb = reviewDb;
    }

    public Review postReview(Review review) {
        if (review.getContent() == null) {
            throw new ValidationException("Содержание комментария не может быть пустым");
        }

        if (review.getUserId() < 0) {
            throw new NotFoundException("Не найден пользователь с указанным идентификатором");
        } else if (review.getUserId() == 0) {
            throw new ValidationException("Идентификатор пользователя не может быть пустым");
        }

        if (review.getFilmId() < 0) {
            throw new NotFoundException("Не найден фильм с указанным идентификатором");
        } else if (review.getFilmId() == 0) {
            throw new ValidationException("Идентификатор фильма не может быть пустым");
        }

        if (review.getIsPositive() == null) {
            throw new ValidationException("Категория не может быть пустой");
        }

        return reviewDb.postReview(review);
    }

    public Review putReview(Review review) {
        return reviewDb.putReview(review);
    }

    public Review getReview(long id) {
        return reviewDb.getReview(id);
    }

    public List<Review> getReviews(long filmId) {
        return reviewDb.getReviews(filmId);
    }

    public List<Review> delReview(long id) {
        return reviewDb.delReview(id);
    }

    public Review addReviewLike(long id, long userId) {
        return reviewDb.addReviewLike(id, userId);
    }

    public Review delReviewLike(long id, long userId) {
        return reviewDb.delReviewLike(id, userId);
    }

    public Review addReviewDislike(long id, long user_id) {
        return reviewDb.addReviewDislike(id, user_id);
    }
}
