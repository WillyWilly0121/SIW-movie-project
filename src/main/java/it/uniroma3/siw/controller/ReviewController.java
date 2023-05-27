package it.uniroma3.siw.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.MovieService;
import it.uniroma3.siw.service.ReviewService;
import it.uniroma3.siw.service.UserService;

@Controller
public class ReviewController {
	
	@Autowired
	UserService userService;
	
	@Autowired
	MovieService movieService;
	
	@Autowired
	ReviewService reviewService;
	
	@Autowired
	ReviewValidator reviewValidator;
	
	@GetMapping("/review/{id}")
	public String formNewReview(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			model.addAttribute("review", new Review());
			model.addAttribute("movie", movie);
			return "formNewReview.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@PostMapping("/addReview/{movieId}/{userId}")
	public String newReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("movieId") Long movieId, @PathVariable("userId") Long userId, Model model) {
		Movie movie = this.movieService.getMovie(movieId);
		User user = this.userService.getUser(userId);
		if(movie!=null && user!=null) {
			review.setMovie(movie);
			review.setUser(user);
			this.reviewValidator.validate(review, bindingResult);
			if (!bindingResult.hasErrors()) {
				this.reviewService.saveReview(review, movie, user);
				return "redirect:/movie/" + movie.getId();
			} else {
				return "redirect:/review/" + movie.getId();
			}
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping("/deleteReviewFromMoviePage/{reviewId}/{movieId}")
	public String deleteReviewFromMoviePage(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId, Model model) {
		Review review = this.reviewService.getReview(reviewId);
		Movie movie = this.movieService.getMovie(movieId);
		if(movie!=null && review!=null) {
			this.reviewService.deleteReview(review);
			return "redirect:/movie/" + movie.getId();
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping("/deleteReviewFromUserPage/{reviewId}/{userId}")
	public String deleteReviewFromUserPage(@PathVariable("reviewId") Long reviewId, @PathVariable("userId") Long userId, Model model) {
		Review review = this.reviewService.getReview(reviewId);
		User user = this.userService.getUser(userId);
		if(review!=null && user!=null) {
			this.reviewService.deleteReview(review);
			return "redirect:/userDetails/" + user.getId();
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping("/admin/deleteReviewFromUpdateMoviePage/{reviewId}/{movieId}")
	public String deleteReviewFromUpdateMoviePage(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId, Model model) {
		Review review = this.reviewService.getReview(reviewId);
		Movie movie = this.movieService.getMovie(movieId);
		if(movie!=null && review!=null) {
			this.reviewService.deleteReview(review);
			return "redirect:/admin/formUpdateMovie/" + movie.getId();
		} else {
			return "resourceNotFound.html";
		}
	}
}
