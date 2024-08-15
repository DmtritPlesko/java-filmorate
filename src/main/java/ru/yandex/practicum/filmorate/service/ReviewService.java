package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.dao.filmDb.FilmDbStorage;
import ru.yandex.practicum.filmorate.storage.dao.review.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.dao.userDb.UserDbStorage;

import java.util.List;

@RequiredArgsConstructor
@Service
public class ReviewService {
    private final UserDbService userService;
    private final FilmDbService filmDbService;
    private final ReviewStorage reviewStorage;
    private final FeedService feedService;


    public Review create(Review review) {
        reviewValidation(review);
        Review reviewCreated = reviewStorage.create(review);
        feedService.create(reviewCreated.getUserId(), reviewCreated.getReviewId(), "REVIEW", "ADD");
        return reviewCreated;
    }

    public Review update(Review review) {
        reviewValidation(review);
        Review reviewUpdated = reviewStorage.update(review);
        feedService.create(reviewUpdated.getUserId(), reviewUpdated.getReviewId(), "REVIEW", "UPDATE");
        return reviewUpdated;
    }

    public void delete(Long reviewId) {
        idValidation(reviewId);
        Review review = getReviewById(reviewId);
        reviewStorage.delete(reviewId);
        feedService.create(review.getUserId(), reviewId, "REVIEW", "REMOVE");
    }

    public List<Review> getAllReviewsByCount(Integer count) {
        return reviewStorage.getAllReviewsByCount(count);
    }

    public Review getReviewById(Long reviewId) {
        idValidation(reviewId);
        return reviewStorage.getReviewById(reviewId);
    }

    public List<Review> getReviewsByFilmId(Long filmId, Integer count) {
        if (filmId == null) {
            return reviewStorage.getAllReviewsByCount(count);
        } else {
            filmDbService.getFilmById(filmId);
            return reviewStorage.getReviewsByFilmId(filmId, count);
        }
    }

    public void addLike(Long reviewId, Long userId) {
        idValidation(reviewId);
        idValidation(userId);
        userService.getUserById(userId);
        reviewStorage.addReaction(reviewId, userId, Boolean.TRUE);
    }

    public void addDislike(Long reviewId, Long userId) {
        idValidation(reviewId);
        idValidation(userId);
        userService.getUserById(userId);
        reviewStorage.addReaction(reviewId, userId, Boolean.FALSE);
    }

    public void deleteLike(Long reviewId, Long userId) {
        idValidation(reviewId);
        idValidation(userId);
        userService.getUserById(userId);
        reviewStorage.deleteReaction(reviewId, userId);
    }

    public void deleteDislike(Long reviewId, Long userId) {
        idValidation(reviewId);
        idValidation(userId);
        userService.getUserById(userId);
        reviewStorage.deleteReaction(reviewId, userId);
    }

    private void reviewValidation(Review review) {
        if (review.getContent() == null || review.getContent().trim().isEmpty()) {
            throw new IllegalArgumentException("Содержание отзыва не может быть пустым");
        }
        if (review.getIsPositive() == null) {
            throw new IllegalArgumentException("Необходимо оценить характер отзыва");
        }
        if (review.getUserId() == null || userService.getUserById(review.getUserId()) == null) {
            throw new IllegalArgumentException("id юзера не может быть null");
        }
        if (review.getFilmId() == null || filmDbService.getFilmById(review.getFilmId()) == null) {
            throw new IllegalArgumentException("id фильма не может быть null");
        }
    }

    private void idValidation(Long someId) {
        if (someId == null) {
            throw new IllegalArgumentException("id не может быть null");
        }
    }

}
