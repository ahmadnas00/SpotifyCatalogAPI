package com.example.catalog.controller;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.services.DataSourceService;
import com.example.catalog.utils.SpotifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/artists")
public class ArtistController {

    private final DataSourceService dataSourceService;

    @Autowired
    public ArtistController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    // ------------------------ GET Requests ------------------------

    @GetMapping("/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        Artist artist = dataSourceService.getArtistById(id).getBody();
        if (artist == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(artist);
    }

    @GetMapping
    public ResponseEntity<List<Artist>> getAllArtists() throws IOException {
        List<Artist> artists = dataSourceService.getAllArtists().getBody();
        if (artists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(artists);
    }

    @GetMapping("/{id}/albums")
    public ResponseEntity<List<Album>> getArtistAlbums(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        List<Album> albums = dataSourceService.getArtistAlbums(id).getBody();
        if (albums.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}/songs")
    public ResponseEntity<List<Song>> getArtistSongs(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        List<Song> songs = dataSourceService.getArtistSongs(id).getBody();
        if (songs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(songs);
    }

    // ------------------------ POST Request ------------------------

    @PostMapping
    public ResponseEntity<Void> addArtist(@RequestBody Artist artist) throws IOException {
        if (artist.getId() == null || artist.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.addArtist(artist);
    }

    // ------------------------ PUT Request ------------------------

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateArtist(@PathVariable String id, @RequestBody Artist updatedArtist) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.updateArtist(id, updatedArtist);
    }

    // ------------------------ DELETE Request ------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArtist(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.deleteArtistById(id);
    }
}
