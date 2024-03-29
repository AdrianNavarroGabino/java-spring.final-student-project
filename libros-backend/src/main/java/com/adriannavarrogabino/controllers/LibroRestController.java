package com.adriannavarrogabino.controllers;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.adriannavarrogabino.models.entity.Genero;
import com.adriannavarrogabino.models.entity.Libro;
import com.adriannavarrogabino.models.services.ILibroService;

// @CrossOrigin(origins = {"http://localhost:4200"})
@CrossOrigin(origins = {"*"})
@RestController
@RequestMapping("/api")
public class LibroRestController {
	
	@Autowired
	ILibroService libroService;
	
	private final Logger log = LoggerFactory.getLogger(
			LibroRestController.class);
	
	@GetMapping("/libros")
	public Page<Libro> index()
	{
		Pageable pageable = PageRequest.of(0, 10);
		return libroService.findAll(pageable);
	}
	
	@GetMapping("/libros/page/{page}")
	public Page<Libro> index(@PathVariable Integer page)
	{
		Pageable pageable = PageRequest.of(page, 10);
		return libroService.findAll(pageable);
	}
	
	@GetMapping("/libros/rnd")
	public List<Libro> librosAleatorios() {
		return libroService.findRandomLibros();
	}
	
	@PostMapping("libros/buscar/page/{page}")
	public Page<Libro> buscarLibros(@RequestBody String buscar,
			@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 10);
		return libroService.buscarLibros(buscar, pageable);
	}
	
	@GetMapping("/libros/generos/{idGenero}/page/{page}")
	public Page<Libro> librosPorAutor(@PathVariable Long idGenero,
			@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 10);
		return libroService.findLibrosPorGenero(idGenero, pageable);
	}
	
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	@GetMapping("/libros/{id}")
	public ResponseEntity<?> show(@PathVariable Long id)
	{
		Libro libro = null;
		Map<String, Object> response = new HashMap<>();
		
		try
		{
			libro = libroService.findById(id);
		}
		catch(DataAccessException e)
		{
			response.put("mensaje",
					"Error al realizar la consulta en la base de datos");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(
					response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		if(libro == null)
		{
			response.put("mensaje",
					"El libro que buscas no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(
					response, HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Libro>(libro, HttpStatus.OK);
	}
	
	@GetMapping("/libros/estanterias/{idEstanteria}/page/{page}")
	public Page<Libro> getEstanteria(@PathVariable Long idEstanteria,
			@PathVariable Integer page) {
		Pageable pageable = PageRequest.of(page, 10);
		return libroService.findEstanteria(idEstanteria, pageable);
	}
	
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	@PostMapping("/libros")
	public ResponseEntity<?> create(@RequestBody Libro libro)
	{
		Libro libroNew = null;
		List<Genero> generos = new ArrayList<>();
		libro.getGeneros().stream().forEach(g -> generos.add(g));
		Map<String, Object> response = new HashMap<>();
		
		try
		{
			libroNew = libroService.save(libro);
			libroNew.setGeneros(generos);
			libroNew = libroService.save(libroNew);
		}
		catch(DataAccessException e)
		{
			response.put("mensaje",
					"Error al realizar el insert en la base de datos");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(
					response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El libro ha sido creado con éxito");
		response.put("libro", libroNew);
		
		return new ResponseEntity<Map<String, Object>>(
				response, HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_USER", "ROLE_ADMIN"})
	@PutMapping("/libros/{id}")
	public ResponseEntity<?> update(
			@RequestBody Libro libro, @PathVariable Long id)
	{
		Libro libroActual = libroService.findById(id);
		Libro libroUpdated = null;
		
		Map<String, Object> response = new HashMap<>();
		
		if(libroActual == null)
		{
			response.put("mensaje", "El libro no existe en la base de datos");
			return new ResponseEntity<Map<String, Object>>(
					response, HttpStatus.NOT_FOUND);
		}
		
		try
		{
			libroActual.setTitulo(libro.getTitulo());
			libroActual.setAutor(libro.getAutor());
			libroActual.setIdioma(libro.getIdioma());
			libroActual.setPaginas(libro.getPaginas());
			libroActual.setGeneros(libro.getGeneros());
			libroActual.setAnyoPublicacion(libro.getAnyoPublicacion());
			
			libroUpdated = libroService.save(libroActual);
		}
		catch(DataAccessException e)
		{
			response.put("mensaje",
					"Error al realizar el update en la base de datos");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "El libro ha sido actualizado con éxito");
		response.put("libro", libroUpdated);
		
		return new ResponseEntity<Map<String, Object>>(response,
				HttpStatus.CREATED);
	}
	
	@Secured({"ROLE_ADMIN"})
	@DeleteMapping("/libros/{id}")
	public ResponseEntity<?> delete(@PathVariable Long id)
	{
		Map<String, Object> response = new HashMap<>();
		try
		{
			libroService.delete(id);
		}
		catch(DataAccessException e)
		{
			response.put("mensaje",
					"Error al realizar el delete en la base de datos");
			response.put("error", e.getMessage().concat(": ")
					.concat(e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response,
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
		response.put("mensaje", "Libro eliminado con éxito");
		
		return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
	}
	
	@GetMapping("/uploads/img/{nombreFoto:.+}")
	public ResponseEntity<Resource> verFoto(@PathVariable String idFoto) {
		Path rutaArchivo = Paths.get("https://www.ebookelo.com/images/cover/" +
				idFoto + ".jpg");
		log.info(rutaArchivo.toString());
		Resource recurso = null;

		try {
			recurso = new UrlResource(rutaArchivo.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		if(!recurso.exists() || !recurso.isReadable())
		{
			rutaArchivo = Paths.get("src/main/resources/static/images")
					.resolve("no-portada.png").toAbsolutePath();
			
			try {
				recurso = new UrlResource(rutaArchivo.toUri());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			
			log.error("Error. No se pudo cargar la imagen");
		}
		
		HttpHeaders cabecera = new HttpHeaders();
		cabecera.add(HttpHeaders.CONTENT_DISPOSITION,
				"attachment; filename=\"" + recurso.getFilename() + "\"");

		return new ResponseEntity<Resource>(recurso, cabecera, HttpStatus.OK);
	}
}
