package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.validation.Valid;

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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;

@Controller
public class MovieController {
	
	@Autowired 
	private MovieRepository movieRepository;
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired 
	private MovieValidator movieValidator;
	
	//ADMIN
	
	@GetMapping(value="/admin/indexMovie")
	public String indexMovie() {
		return "admin/indexMovie.html";
	}
	
	@GetMapping(value="/admin/formNewMovie")
	public String formNewMovie(Model model) {
		model.addAttribute("movie", new Movie());
		return "admin/formNewMovie.html";
	}
	
	@GetMapping("/movie/{id}")
	public String getMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", this.movieRepository.findById(id).get());
		return "movie.html";
	}
	
	@PostMapping("/admin/movie")
	public String newMovie(@RequestParam("files") MultipartFile[] files, @Valid @ModelAttribute("movie") Movie movie, BindingResult bindingResult, Model model) {
		this.movieValidator.validate(movie, bindingResult);
		if (!bindingResult.hasErrors()) {
			if(files==null) {
				bindingResult.reject("image.null");
			}
			movie.setImages(new ArrayList<>());
	        for (MultipartFile file : files) {
	            try {
	                Image immagine = new Image();
	                immagine.setFilename(file.getOriginalFilename());
	                immagine.setImageData(file.getBytes());
	                String format = immagine.getFormat();
	                if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
	                	bindingResult.reject("image.formatNotSupported");
	                	continue;
	                }
	                movie.getImages().add(immagine);
	            } catch (IOException ex) {
	            	bindingResult.reject("image.readError");
	            }
	        }
			this.movieRepository.save(movie); 
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "admin/formNewMovie.html"; 
		}
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "admin/formUpdateMovie.html";
	}
	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {
		
		Movie movie = this.movieRepository.findById(id).get();
		Set<Artist> actorsToAdd = this.artistRepository.findDistinctByActedNotContaining(movie);
		model.addAttribute("notActors", actorsToAdd);
		model.addAttribute("actors", movie.getActors());
		model.addAttribute("movie", movie);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		List<Artist> actors = movie.getActors();
		actors.add(actor);
		actor.getActed().add(movie);
		this.artistRepository.save(actor);
		this.movieRepository.save(movie);
		Set<Artist> actorsToAdd = this.artistRepository.findDistinctByActedNotContaining(movie);
		model.addAttribute("notActors", actorsToAdd);
		model.addAttribute("actors", movie.getActors());
		model.addAttribute("movie", movie);

		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieRepository.findById(movieId).get();
		Artist actor = this.artistRepository.findById(actorId).get();
		List<Artist> actors = movie.getActors();
		actors.remove(actor);
		actor.getActed().remove(movie);
		this.artistRepository.save(actor);
		this.movieRepository.save(movie);
		Set<Artist> actorsToAdd = this.artistRepository.findDistinctByActedNotContaining(movie);
		model.addAttribute("notActors", actorsToAdd);
		model.addAttribute("actors", movie.getActors());
		model.addAttribute("movie", movie);
		return "admin/actorsToAdd.html";
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		
		Artist director = this.artistRepository.findById(directorId).get();
		Movie movie = this.movieRepository.findById(movieId).get();
		movie.setDirector(director);
		this.movieRepository.save(movie);
		
		model.addAttribute("movie", movie);
		return "admin/formUpdateMovie.html";
	}
	
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		model.addAttribute("artists", artistRepository.findAll());
		model.addAttribute("movie", movieRepository.findById(id).get());
		return "admin/directorsToAdd.html";
	}
	
	@GetMapping("/movie")
	public String getMovies(Model model) {
		model.addAttribute("movies", this.movieRepository.findAll());
		return "movies.html";
	}
}
