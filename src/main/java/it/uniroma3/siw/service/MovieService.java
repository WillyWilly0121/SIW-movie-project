package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Review;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.repository.ReviewRepository;
import jakarta.transaction.Transactional;

@Service
public class MovieService {
	
	@Autowired
	protected MovieRepository movieRepository;
	
	@Autowired
	private ArtistRepository artistRepository;
	
	@Autowired
	protected ReviewRepository reviewRepository;
	
	public Movie getMovie(Long id) {
		return this.movieRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public Movie saveMovie(Movie movie) {
		return this.movieRepository.save(movie);
	}
	
	public Iterable<Movie> findAllMovies(){
		return this.movieRepository.findAll();
	}
	
	@Transactional
	public void deleteMovie(Movie movie) {
		if(movie.getDirector()!=null) {
			Artist director = movie.getDirector();
			director.getDirected().remove(movie);
			this.artistRepository.save(director);
		}
		for(Artist artist : movie.getActors()) {
			artist.getActed().remove(movie);
			this.artistRepository.save(artist);
		}
		for(Review review : movie.getReviews()) {
			this.reviewRepository.delete(review);
		}
		this.movieRepository.delete(movie);
	}
	

}
