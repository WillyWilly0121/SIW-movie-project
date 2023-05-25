package it.uniroma3.siw.service;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import jakarta.transaction.Transactional;

@Service
public class ArtistService {
	
	@Autowired 
	protected ArtistRepository artistRepository;
	
	@Autowired
	private MovieRepository movieRepository;
	
	public Set<Artist> getArtistNotContaingMovie(Movie movie){
		return this.artistRepository.findDistinctByActedNotContaining(movie);
	}
	
	public Artist getArtist(Long id) {
		return this.artistRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public Artist saveArtist(Artist artist) {
		return this.artistRepository.save(artist);
	}
	
	public Iterable<Artist> findAllArtists(){
		return this.artistRepository.findAll();
	}
	
	@Transactional
	public void deleteArtist(Artist artist) {
		for(Movie movie : artist.getDirected()) {
			movie.setDirector(null);
			this.movieRepository.save(movie);
		}
		for(Movie movie : artist.getActed()) {
			movie.getActors().remove(artist);
			this.movieRepository.save(movie);
		}
		this.artistRepository.delete(artist);
	}
}
