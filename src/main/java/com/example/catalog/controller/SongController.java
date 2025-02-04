package com.example.catalog.controller;

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
@RequestMapping("/songs")
public class SongController {

    private final DataSourceService dataSourceService;

    @Autowired
    public SongController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    // ------------------------ GET Requests ------------------------

    @GetMapping
    public ResponseEntity<List<Song>> getAllSongs() throws IOException {
        List<Song> songs = dataSourceService.getAllSongs().getBody();
        if (songs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(songs);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Song> getSongById(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        Song song = dataSourceService.getSongById(id).getBody();
        if (song == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(song);
    }

    // ------------------------ POST Request ------------------------

    @PostMapping
    public ResponseEntity<Void> addSong(@RequestBody Song song) throws IOException {
        if (song.getId() == null || song.getName() == null) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.addSong(song);
    }

    // ------------------------ PUT Request ------------------------

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateSong(@PathVariable String id, @RequestBody Song updatedSong) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.updateSong(id, updatedSong);
    }

    // ------------------------ DELETE Request ------------------------

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSong(@PathVariable String id) throws IOException {
        if (!SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }
        return dataSourceService.deleteSongById(id);
    }
}
