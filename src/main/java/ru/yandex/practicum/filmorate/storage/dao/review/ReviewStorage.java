package ru.yandex.practicum.filmorate.storage.dao.review;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewStorage {

    Review create(Review review);

    Review update(Review review);

    void delete(Long reviewId);

    List<Review> getAllReviewsByCount(Integer count);

    Review getReviewById(Long reviewId);

    List<Review> getReviewsByFilmId(Long filmId, Integer count);

    void addReaction(Long reviewId, Long userId, Boolean isUseful);

    //void addDislike(Long reviewId, Long userId, Boolean isUseful);

    void deleteReaction(Long reviewId, Long userId);

    //void deleteDislike(Long reviewId, Long userId);

}
