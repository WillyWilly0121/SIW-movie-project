package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.repository.ArtistRepository;
import it.uniroma3.siw.model.Artist;

@Component
public class ArtistValidator implements Validator{
	
	@Autowired 
	private ArtistRepository artistRepository;

	@Override
	public boolean supports(Class<?> clazz) {
		return Artist.class.equals(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		Artist artist = (Artist) target;
		if (artist.getNome()!=null && artist.getCognome()!=null && artist.getBornDate()!=null
				&& artistRepository.existsByNomeAndCognome(artist.getNome(), artist.getCognome())) {
			errors.reject("artist.duplicate");
		}
	}
}
