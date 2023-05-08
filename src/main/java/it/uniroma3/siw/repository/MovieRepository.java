package it.uniroma3.siw.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Movie;

public interface MovieRepository extends CrudRepository<Movie, Long> {

	public List<Movie> findByReleaseDate(Date year);

	public boolean existsByTitleAndReleaseDate(String title, Date year);	
}
