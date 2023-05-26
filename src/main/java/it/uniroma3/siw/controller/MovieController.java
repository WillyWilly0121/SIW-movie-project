package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.MovieValidator;
import it.uniroma3.siw.model.Movie;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.MovieService;

@Controller
public class MovieController {
	
	@Autowired 
	private MovieService movieService;
	
	@Autowired 
	private ArtistService artistService;

	@Autowired 
	private MovieValidator movieValidator;
	
	@Autowired
	private ImageService imageService;
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
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "resourceNotFound.html";
		}
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
			this.movieService.saveMovie(movie); 
			model.addAttribute("movie", movie);
			return "movie.html";
		} else {
			return "admin/formNewMovie.html"; 
		}
	}
	
	@GetMapping(value="/admin/manageMovies")
	public String manageMovies(Model model) {
		model.addAttribute("movies", this.movieService.findAllMovies());
		return "admin/manageMovies.html";
	}
	
	@GetMapping(value="/admin/formUpdateMovie/{id}")
	public String formUpdateMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = movieService.getMovie(id);
		if(movie!=null) {
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping("/admin/updateActors/{id}")
	public String updateActors(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			Set<Artist> actorsToAdd = this.artistService.getArtistNotContaingMovie(movie);
			model.addAttribute("notActors", actorsToAdd);
			model.addAttribute("actors", movie.getActors());
			model.addAttribute("movie", movie);
			return "admin/actorsToAdd.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/removeMovieImage/{movieId}/{imageId}")
	public String removeImage(@PathVariable("movieId") Long movieId, @PathVariable("imageId") Long imageId, Model model) {
		Movie movie = this.movieService.getMovie(movieId);
		Image image = this.imageService.getImage(imageId);
		if(movie!=null && image!=null) {
			if(movie.getImages().size()==1) {
				return "admin/formUpdateMovie.html";
			} else {
				movie.getImages().remove(image);
				this.movieService.saveMovie(movie);
				this.imageService.deleteImage(image);
				model.addAttribute("movie", movie);
				return "admin/formUpdateMovie.html";
			}
		} else {
			return "resourceNotFound.html";
		}
	}
	
	
	@GetMapping(value="/admin/addActorToMovie/{actorId}/{movieId}")
	public String addActorToMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.getMovie(movieId);
		Artist actor = this.artistService.getArtist(actorId);
		if(movie!=null && actor!=null) {
			List<Artist> actors = movie.getActors();
			actors.add(actor);
			actor.getActed().add(movie);
			this.artistService.saveArtist(actor);
			this.movieService.saveMovie(movie);
			Set<Artist> actorsToAdd = this.artistService.getArtistNotContaingMovie(movie);
			model.addAttribute("notActors", actorsToAdd);
			model.addAttribute("actors", movie.getActors());
			model.addAttribute("movie", movie);
			return "admin/actorsToAdd.html";
		} else {
			return "resourceNotFound.html";
		}
		
	}
	
	@GetMapping(value="/admin/removeActorFromMovie/{actorId}/{movieId}")
	public String removeActorFromMovie(@PathVariable("actorId") Long actorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.getMovie(movieId);
		Artist actor = this.artistService.getArtist(actorId);
		if(movie!=null && actor!=null) {
			List<Artist> actors = movie.getActors();
			actors.remove(actor);
			actor.getActed().remove(movie);
			this.artistService.saveArtist(actor);
			this.movieService.saveMovie(movie);
			Set<Artist> actorsToAdd = this.artistService.getArtistNotContaingMovie(movie);
			model.addAttribute("notActors", actorsToAdd);
			model.addAttribute("actors", movie.getActors());
			model.addAttribute("movie", movie);
			return "admin/actorsToAdd.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/setDirectorToMovie/{directorId}/{movieId}")
	public String setDirectorToMovie(@PathVariable("directorId") Long directorId, @PathVariable("movieId") Long movieId, Model model) {
		Movie movie = this.movieService.getMovie(movieId);
		Artist director = this.artistService.getArtist(directorId);
		if(movie!=null && director!=null) {
			movie.setDirector(director);
			this.movieService.saveMovie(movie);
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/removeDirector/{id}")
	public String removeDirector(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			movie.setDirector(null);
			this.movieService.saveMovie(movie);
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/addDirector/{id}")
	public String addDirector(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			model.addAttribute("artists", artistService.findAllArtists());
			model.addAttribute("movie", movie);
			return "admin/directorsToAdd.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/addMovieImages/{id}")
	public String getAddArtistImage(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			model.addAttribute("movie", movie);
			return "admin/addMovieImages.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@PostMapping("/admin/updateMovieImages/{id}")
	public String updateArtistImage(@RequestParam("files") MultipartFile[] files, @PathVariable("id") Long id, Model model, @ModelAttribute("image") Image image, Errors errors) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			if(files==null) {
				errors.reject("image.null");
			}
	        for (MultipartFile file : files) {
	            try {
	                Image immagine = new Image();
	                immagine.setFilename(file.getOriginalFilename());
	                immagine.setImageData(file.getBytes());
	                String format = immagine.getFormat();
	                if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
	                	errors.reject("image.formatNotSupported");
	                	continue;
	                }
	                movie.getImages().add(immagine);
	            } catch (IOException ex) {
	            	errors.reject("image.readError");
	            }
	        }
			this.movieService.saveMovie(movie); 
			model.addAttribute("movie", movie);
			return "admin/formUpdateMovie.html";
		} else {
			return "resourceNotFound.html";
		}
		
	}
	
	@GetMapping("/admin/removeMovie/{id}")
	public String removeMovie(@PathVariable("id") Long id, Model model) {
		Movie movie = this.movieService.getMovie(id);
		if(movie!=null) {
			this.movieService.deleteMovie(movie);
			return "admin/indexMovie.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	
	@GetMapping("/movie")
	public String getMovies(Model model) {
		model.addAttribute("movies", this.movieService.findAllMovies());
		return "movies.html";
	}
}
