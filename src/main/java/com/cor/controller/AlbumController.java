package com.cor.controller;

import static org.springframework.hateoas.mvc.ControllerLinkBuilder.linkTo;
import static org.springframework.hateoas.mvc.ControllerLinkBuilder.methodOn;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cor.domain.Album;
import com.cor.service.MusicService;

@Controller
public class AlbumController {
	@Autowired
	private MusicService musicService;

	@RequestMapping(value = "/albums", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Collection<Resource<Album>> getAllAlbums() {
		Collection<Album> albums = musicService.getAllAlbums();
		List<Resource<Album>> resources = new ArrayList<Resource<Album>>();
		for (Album a : albums) {
			Resource<Album> resource = new Resource<Album>(a);
			// Link to Album
			resource.add(linkTo(methodOn(AlbumController.class).getAlbum(a.getId())).withSelfRel());
			// Link to Artist
			resource.add(linkTo(methodOn(ArtistController.class).getArtist(a.getArtist().getId())).withRel("artist"));
			// Option to purchase Album
			if (a.getStockLevel() > 0) {
				resource.add(linkTo(methodOn(AlbumController.class).purchaseAlbum(a.getId())).withRel("album.purchase"));
			}
			resources.add(resource);
		}
		return resources;
	}

	@RequestMapping(value = "/album/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Resource<Album> getAlbum(@PathVariable(value = "id") String id) {
		Album a = musicService.getAlbum(id);
		Resource<Album> resource = new Resource<Album>(a);
		resource.add(linkTo(methodOn(AlbumController.class).getAlbum(id)).withSelfRel());
		return resource;
	}

	@RequestMapping(value = "/album/purchase/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public Resource<Album> purchaseAlbum(@PathVariable(value = "id") String id) {
		Album a = musicService.getAlbum(id);
		a.setStockLevel(a.getStockLevel() - 1);
		Resource<Album> resource = new Resource<Album>(a);
		resource.add(linkTo(methodOn(AlbumController.class).getAlbum(id)).withSelfRel());
		return resource;
	}
}
