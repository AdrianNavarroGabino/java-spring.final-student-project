package com.adriannavarrogabino.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adriannavarrogabino.models.entity.Autor;
import com.adriannavarrogabino.models.entity.Libro;
import com.adriannavarrogabino.models.services.IAutorService;
import com.adriannavarrogabino.models.services.ILibroService;

@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class AutorRestController {

	@Autowired
	private IAutorService autorService;
	
	@Autowired
	private ILibroService libroService;
	
	@GetMapping("/autores/page/{page}")
	public Page<Autor> showAutores(@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 10);
		return autorService.findAll(pageable);
	}
	
	@GetMapping("/autores/{id}/page/{page}")
	public Page<Libro> librosPorAutor(
			@PathVariable Long id, @PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 10);
		return libroService.findLibrosPorAutor(id, pageable);
	}
	
	@GetMapping("/autores/{nombre}")
	public Autor getAutorPorNombre(@PathVariable String nombre) {
		return autorService.buscarAutorPorNombre(nombre.replace("_", " ")
				.toUpperCase());
	}
	
	@PostMapping("/autores")
	public Autor addAutor(@RequestBody Autor autor) {
		return autorService.save(autor);
	}
}
