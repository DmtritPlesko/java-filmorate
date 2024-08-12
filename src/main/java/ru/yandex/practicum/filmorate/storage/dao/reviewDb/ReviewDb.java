package ru.yandex.practicum.filmorate.storage.dao.reviewDb;

import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

public interface ReviewDb {
    Review postReview(Review review);

    Review putReview(Review review);

    Review getReview(long id);

    List<Review> getReviews(long filmId);

    List<Review> delReview(long id);

    Review addReviewLike(long id, long userId);

    Review delReviewLike(long id, long userId);

    Review addReviewDislike(long id, long userId);
}
