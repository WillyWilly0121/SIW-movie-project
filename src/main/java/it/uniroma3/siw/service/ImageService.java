package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.repository.ImageRepository;
import jakarta.transaction.Transactional;

@Service
public class ImageService {
	
	@Autowired
	protected ImageRepository imageRepository;
	
	public Image getImage(Long id) {
		return this.imageRepository.findById(id).orElse(null);
	}
	
	@Transactional
	public void deleteImage(Image image) {
		this.imageRepository.delete(image);
	}
	
	@Transactional
	public Image saveImage(Image image) {
		return this.imageRepository.save(image);
	}
}
