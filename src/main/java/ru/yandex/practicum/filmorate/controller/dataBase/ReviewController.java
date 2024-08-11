package ru.yandex.practicum.filmorate.controller.dataBase;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @PostMapping
    public Review postReview(@RequestBody Review review) {
        return reviewService.postReview(review);
    }

    @PutMapping
    public Review putReviews(@RequestBody Review review) {
        return reviewService.putReview(review);
    }

    @GetMapping("/{id}")
    public Review getReview(@PathVariable long id) {
        return reviewService.getReview(id);
    }

    @GetMapping
    public List<Review> getReviews(@RequestParam long filmId) {
        return reviewService.getReviews(filmId);
    }

    @DeleteMapping("/{id}")
    public List<Review> delReview(@PathVariable long id) {
        return reviewService.delReview(id);
    }

    @PutMapping("/{reviewId}/like/{userId}")
    public Review addReviewLike(@PathVariable long reviewId, @PathVariable long userId) {
        return reviewService.addReviewLike(reviewId, userId);
    }

    @DeleteMapping("/{reviewId}/like/{userId}")
    public Review delReviewLike(@PathVariable long reviewId, @PathVariable long userId) {
        return reviewService.delReviewLike(reviewId, userId);
    }

    @PutMapping("/{reviewId}/dislike/{userId}")
    public Review addReviewDislike(@PathVariable long reviewId, @PathVariable long userId) {
        return reviewService.addReviewDislike(reviewId, userId);
    }
}
