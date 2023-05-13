package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ReviewRepository;

@Component
public class ReviewValidator implements Validator{
	
	@Autowired
	private ReviewRepository reviewRepository;
	
	@Override
	public boolean supports(Class<?> clazz) {
		return Review.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Review review = (Review) target;
		if(this.reviewRepository.existsByUserAndMovie(review.getUser(), review.getMovie())) {
			errors.reject("review.duplicate");
		}
		
	}

}
