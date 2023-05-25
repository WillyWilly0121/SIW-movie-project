package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import it.uniroma3.siw.repository.UserRepository;
import jakarta.transaction.Transactional;

@Service
public class ReviewService {
	
	@Autowired
	protected ReviewRepository reviewRepository;
	
	@Autowired
	private MovieRepository movieRepository;
	
	@Autowired
	private UserRepository userRepository;
	
	@Transactional
	public void deleteReview(Review review) {
		this.reviewRepository.delete(review);
	}
	
	@Transactional
	public void saveReview(Review review, Movie movie, User user) {
		this.reviewRepository.save(review);
		movie.getReviews().add(review);
		user.getReviews().add(review);
		this.movieRepository.save(movie);
		this.userRepository.save(user);
	}
	
	public Review getReview(Long id) {
		return this.reviewRepository.findById(id).orElse(null);
	}
}
