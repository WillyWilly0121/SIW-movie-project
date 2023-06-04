package it.uniroma3.siw.controller;

import java.io.IOException;
import java.util.Date;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
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

import it.uniroma3.siw.controller.validator.ArtistValidator;
import it.uniroma3.siw.model.Artist;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.service.ArtistService;
import it.uniroma3.siw.service.ImageService;

@Controller
public class ArtistController {
	
	@Autowired 
	private ImageService imageService;
	@Autowired 
	private ArtistService artistService;
	@Autowired
	private ArtistValidator artistValidator;
	
	//ADMIN
	
	@GetMapping(value="/admin/formNewArtist")
	public String formNewArtist(Model model) {
		model.addAttribute("artist", new Artist());
		return "admin/formNewArtist.html";
	}
	
	@GetMapping(value="/admin/indexArtist")
	public String indexArtist() {
		return "admin/indexArtist.html";
	}
	
	@PostMapping("/admin/artist")
	public String newArtist(@RequestParam("file") MultipartFile file, @Valid @ModelAttribute("artist") Artist artist, BindingResult bindingResult, Model model) {
		this.artistValidator.validate(artist, bindingResult);
		if (!bindingResult.hasErrors()) {
			if(file==null) {
				bindingResult.reject("image.null");
			}
			try {
                Image immagine = new Image();
                immagine.setFilename(file.getOriginalFilename());
                immagine.setImageData(file.getBytes());
                String format = immagine.getFormat();
                if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
                	bindingResult.reject("image.formatNotSupported");
                }
                imageService.saveImage(immagine);
                artist.setImage(immagine);
            } catch (IOException ex) {
            	bindingResult.reject("image.readError");
            }
			this.artistService.saveArtist(artist); 
			return "redirect:/artist/" + artist.getId();
		} else {
			return "admin/formNewArtist.html"; 
		}
	}
	
	@GetMapping(value="/admin/manageArtists")
	public String manageArtists( Model model) {
		model.addAttribute("artists", this.artistService.findAllArtists());
		return "admin/manageArtists.html";
	}
	
	@GetMapping(value="/admin/formUpdateArtist/{id}")
	public String formUpdateArtist(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			if(artist.getImage()==null) {
				return "redirect:/admin/addArtistImage/" + artist.getId();
			} else {
				model.addAttribute("artist", artist);
				return "admin/formUpdateArtist.html";
			}
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/removeArtistImage/{artistId}/{imageId}")
	public String removeArtistImage(@PathVariable("artistId") Long artistId, @PathVariable("imageId") Long imageId, Model model) {
		Artist artist = this.artistService.getArtist(artistId);
		Image image = this.imageService.getImage(imageId);
		if(artist!=null && image!=null) {
			artist.setImage(null);
			this.artistService.saveArtist(artist);
			this.imageService.deleteImage(image);
			return "redirect:/admin/addArtistImage/" + artistId;
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/admin/addArtistImage/{id}")
	public String getAddArtistImage(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			model.addAttribute("artist", artist);
			return "admin/addArtistImage.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@PostMapping("/admin/updateArtistImage/{id}")
	public String updateArtistImage(@RequestParam("file") MultipartFile file, @PathVariable("id") Long id, Model model,@ModelAttribute("image") Image immagine, Errors errors) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			if(file==null) {
				errors.reject("image.null");
			}
			try {
	            immagine.setFilename(file.getOriginalFilename());
	            immagine.setImageData(file.getBytes());
	            String format = immagine.getFormat();
	            if (!(format.equals("jpeg") || format.equals("png") || format.equals("jpg"))) {
	            	errors.reject("image.formatNotSupported");
	            }
	            immagine = imageService.saveImage(immagine);
	            artist.setImage(immagine);
	            this.artistService.saveArtist(artist); 
	        } catch (IOException ex) {
	        	errors.reject("image.readError");
	        }
			return "redirect:/admin/formUpdateArtist/" + artist.getId();
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping("/admin/removeArtist/{id}")
	public String removeMovie(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			this.artistService.deleteArtist(artist);
			return "redirect:/admin/indexArtist";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="/artist/{id}")
	public String getArtist(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			model.addAttribute("artist", artist);
			return "artist.html";
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@PostMapping(value="/admin/addDeathDate/{id}")
	public String addDeathDate(@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date date, @PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			Date dataCorrente = new Date();
			if(date.compareTo(dataCorrente)<=0) {
				artist.setDeathDate(date);
				this.artistService.saveArtist(artist);
			}
			return "redirect:/admin/formUpdateArtist/" + artist.getId();
		} else {
			return "resourceNotFound.html";
		}
	}
	
	@GetMapping(value="admin/removeDeathDate/{id}")
	public String deleteDeathDate(@PathVariable("id") Long id, Model model) {
		Artist artist = this.artistService.getArtist(id);
		if(artist!=null) {
			artist.setDeathDate(null);
			this.artistService.saveArtist(artist);
			return "redirect:/admin/formUpdateArtist/" + artist.getId();
		} else {
			return "resourceNotFound.html";
		}
	}
	
	
	@GetMapping(value="/artist")
	public String getArtists(Model model) {
		model.addAttribute("artists", this.artistService.findAllArtists());
		return "artists.html";
	}
}
