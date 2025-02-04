package com.example.catalog.controller;

import com.example.catalog.model.Album;
import com.example.catalog.model.Track;
import com.example.catalog.services.DataSourceService;
import com.example.catalog.utils.SpotifyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final DataSourceService dataSourceService;

    @Autowired
    public AlbumController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    // ------------------------ GET Requests ------------------------

    @GetMapping
    public ResponseEntity<List<Album>> getAllAlbums() throws IOException {
        List<Album> albums = dataSourceService.getAllAlbums().getBody();
        if (albums.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbumById(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        Album album = dataSourceService.getAlbumById(id).getBody();
        if (album == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(album);
    }

    @GetMapping("/{id}/tracks")
    public ResponseEntity<List<Track>> getAlbumTracks(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        List<Track> tracks = dataSourceService.getAlbumTracks(id).getBody();
        if (tracks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(tracks);
    }

    // ------------------------ POST Requests ------------------------

    @PostMapping
    public ResponseEntity<Void> addAlbum(@RequestBody Album album) throws IOException {
        if (album.getId() == null || album.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.addAlbum(album);
    }

    @PostMapping("/{id}/tracks")
    public ResponseEntity<Void> addTrackToAlbum(@PathVariable String id, @RequestBody Track track) throws IOException {
        if (!SpotifyUtils.isValidId(id) || track.getId() == null || track.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.addTrackToAlbum(track, id);
    }

    // ------------------------ PUT Requests ------------------------

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateAlbum(@PathVariable String id, @RequestBody Album updatedAlbum) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.updateAlbum(id, updatedAlbum);
    }

    @PutMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> updateTrack(@PathVariable String id, @PathVariable String trackId, @RequestBody Track updatedTrack) throws IOException {
        if (!SpotifyUtils.isValidId(id) || !SpotifyUtils.isValidId(trackId)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.updateTrack(id, trackId, updatedTrack);
    }

    // ------------------------ DELETE Requests ------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlbum(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.deleteAlbumById(id);
    }

    @DeleteMapping("/{id}/tracks/{trackId}")
    public ResponseEntity<Void> deleteTrackFromAlbum(@PathVariable String id, @PathVariable String trackId) throws IOException {
        if (!SpotifyUtils.isValidId(id) || !SpotifyUtils.isValidId(trackId)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.deleteTrackFromAlbum(id, trackId);
    }
}
