package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.ArrayList;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.repository.MovieRepository;
import it.uniroma3.siw.service.CredentialsService;

@Controller
public class MovieController {
	
	@Autowired 
	private MovieRepository movieRepository;
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Autowired 
	private MovieValidator movieValidator;
	
	@Autowired 
	private CredentialsService credentialsService;
	
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
	
}
