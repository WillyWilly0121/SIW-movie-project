package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import it.uniroma3.siw.repository.ArtistRepository;

@Controller
public class ArtistController {
	
	@Autowired 
	private ArtistRepository artistRepository;

}
