package it.uniroma3.siw.controller;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.controller.validator.ReviewValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class ReviewController {
	
	@Autowired 
	private CredentialsService credentialsService;
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	MovieRepository movieRepository;
	
	@Autowired
	ReviewRepository reviewRepository;
	
	@Autowired
	ReviewValidator reviewValidator;
	
	@GetMapping("/review/{id}")
	public String formNewReview(@PathVariable("id") Long id, Model model) {
		model.addAttribute("review", new Review());
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return "formNewReview.html";
	}
	
	@PostMapping("/addReview/{movieId}")
	public String newReview(@Valid @ModelAttribute("review") Review review, BindingResult bindingResult, @PathVariable("movieId") Long movieId, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		Movie movie = this.movieRepository.findById(movieId).get();
		User user = credentials.getUser();
		review.setMovie(movie);
		review.setUser(user);
		this.reviewValidator.validate(review, bindingResult);
		if (!bindingResult.hasErrors()) {
			this.reviewRepository.save(review);
			movie.getReviews().add(review);
			user.getReviews().add(review);
			this.movieRepository.save(movie);
			this.userRepository.save(user);
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			model.addAttribute("review", new Review());
			model.addAttribute("movie", movie);
			return "formNewReview.html";
		}
	}
	
	@GetMapping("/deleteReviewFromMoviePage/{reviewId}/{movieId}")
	public String deleteReviewFromMoviePage(@PathVariable("reviewId") Long reviewId, @PathVariable("movieId") Long movieId, Model model) {
		this.reviewRepository.delete(this.reviewRepository.findById(reviewId).get());
		model.addAttribute("movie", this.movieRepository.findById(movieId).get());
		return "movie.html";
	}
	
	@GetMapping("/deleteReviewFromUserPage/{reviewId}/{userId}")
	public String deleteReviewFromUserPage(@PathVariable("reviewId") Long reviewId, @PathVariable("userId") Long userId, Model model) {
		this.reviewRepository.delete(this.reviewRepository.findById(reviewId).get());
		model.addAttribute("user", this.userRepository.findById(userId).get());
		return "userDetails.html";
	}
}
