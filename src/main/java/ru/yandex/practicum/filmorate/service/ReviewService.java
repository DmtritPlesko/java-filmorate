package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.review.ReviewStorage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final ReviewStorage reviewStorage;

    public Review create(Review review) {
        reviewValidation(review);
        return reviewStorage.create(review);
    }

    public Review update(Review review) {
        reviewValidation(review);
        return reviewStorage.update(review);
    }

    public void delete(Long reviewId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("id отзыва не может быть null");
        }
        reviewStorage.delete(reviewId);
    }

    public Set<Review> getAllReviewsByCount(Integer count) {
        return new HashSet<>(reviewStorage.getAllReviewsByCount(count));
    }

    public Review getReviewById(Long reviewId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("id отзыва не может быть null");
        }
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewStorage.getAllReviewsByCount(count);
        } else {
            return reviewStorage.getReviewsByFilmId(filmId, count);
        }
    }

    public void addLike(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("id отзыва не может быть null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("id юзера не может быть null");
        }
        deleteDislike(reviewId, userId);
        reviewStorage.addReaction(reviewId, userId, Boolean.TRUE);
    }

    public void addDislike(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("id отзыва не может быть null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("id юзера не может быть null");
        }
        deleteLike(reviewId, userId);
        reviewStorage.addReaction(reviewId, userId, Boolean.FALSE);
    }

    public void deleteLike(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("id отзыва не может быть null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("id юзера не может быть null");
        }
        reviewStorage.deleteReaction(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        if (reviewId == null) {
            throw new IllegalArgumentException("id отзыва не может быть null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("id юзера не может быть null");
        }
        reviewStorage.deleteReaction(reviewId, userId);
    }

    private void reviewValidation(Review review) {
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new IllegalArgumentException("Необходимо оценить характер отзыва");
        }
        if (review.getUserId() == null) {
            throw new IllegalArgumentException("id юзера не может быть null");
        }
        if (review.getFilmId() == null) {
            throw new IllegalArgumentException("id фильма не может быть null");
        }
    }

}
