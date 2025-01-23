package com.example.catalog.controller;

import com.example.catalog.model.Artist;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.stream.StreamSupport;

import com.example.catalog.utils.SpotifyUtils;
import com.example.catalog.utils.CatalogUtils;
import com.fasterxml.jackson.databind.node.ArrayNode;
@RestController
public class CatalogController {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @GetMapping("/popularSongs")
    public ResponseEntity<JsonNode> getPopularSongs(
            @RequestParam(defaultValue = "0") int offset,
            @RequestParam(defaultValue = "-1") int limit) throws IOException {
        // Load songs from the JSON file
        ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
        ArrayNode allSongs = (ArrayNode) objectMapper.readTree(resource.getFile());

        // Validate offset and limit
        if (offset < 0 || limit < -1) {
            return new ResponseEntity<>(objectMapper.createObjectNode().put("error", "Invalid offset or limit"), HttpStatus.BAD_REQUEST);
        }

        // Apply offset and limit to paginate the results
        ArrayNode paginatedSongs = objectMapper.createArrayNode();
        int endIndex = (limit == -1) ? allSongs.size() : Math.min(offset + limit, allSongs.size());
        for (int i = offset; i < endIndex; i++) {
            paginatedSongs.add(allSongs.get(i));
        }

        return new ResponseEntity<>(paginatedSongs, HttpStatus.OK);
    }




    @GetMapping("/popularSongs/filter")
    public ResponseEntity<JsonNode> filterSongs(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPopularity", required = false) Integer minPopularity)
            throws IOException {
        ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
        ArrayNode allSongs = (ArrayNode) objectMapper.readTree(resource.getFile());
        ArrayNode filteredSongs = objectMapper.createArrayNode();
        for (JsonNode song : allSongs) {
            boolean matchesName = (name == null || song.get("name").asText().toLowerCase().contains(name.toLowerCase()));
            boolean matchesPopularity = (minPopularity == null || song.get("popularity").asInt() >= minPopularity);

            if (matchesName && matchesPopularity) {
                filteredSongs.add(song);
            }
        }
        return new ResponseEntity<>(filteredSongs, HttpStatus.OK);
    }


    @GetMapping("/popularArtists")
    public JsonNode getPopularArtists() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/popular_artists.json");
        return objectMapper.readTree(resource.getFile());
    }


    @GetMapping("/albums/{id}")
    public ResponseEntity<JsonNode> getAlbumById(@PathVariable String id) throws IOException {
        if (! SpotifyUtils.isValidId(id)) {
            return new ResponseEntity<>(objectMapper.createObjectNode().put("error","Invalid Id"), HttpStatus.BAD_REQUEST);
        }

        ClassPathResource resource = new ClassPathResource("data/albums.json");
        JsonNode albums = objectMapper.readTree(resource.getFile());
        JsonNode album = albums.get(id);
        if (album != null) {
            return new ResponseEntity<>(album, HttpStatus.OK);
        }

        else {
            ObjectNode errorResponse = objectMapper.createObjectNode().put("error","Album not found");
            return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("/artists")
    public ResponseEntity<String> addArtist(@RequestBody Artist artist) throws IOException {
        ClassPathResource resource = new ClassPathResource("data/popular_artists.json");
        ArrayNode allArtists = (ArrayNode) objectMapper.readTree(resource.getFile());
        JsonNode artistNode = objectMapper.valueToTree(artist);
        allArtists.add(artistNode);
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(resource.getFile(), allArtists);
        return new ResponseEntity<>("Artist added successfully.", HttpStatus.CREATED);
    }


    @GetMapping("/artists/{id}")
    public ResponseEntity<Artist> getArtistById(@PathVariable String id) throws IOException {
        if (! SpotifyUtils.isValidId(id)) {
            return ResponseEntity.badRequest().build();
        }

        ClassPathResource resource = new ClassPathResource("data/popular_artists.json");
        JsonNode artists = objectMapper.readTree(resource.getFile());

        JsonNode artistNode = artists.get(id);
        if (artistNode == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        return  ResponseEntity.ok(objectMapper.treeToValue(artistNode, Artist.class));
    }


    @GetMapping("/songs/mostRecent")
    public ResponseEntity<JsonNode> getMostRecentSong() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
        ArrayNode allSongsNode = (ArrayNode) objectMapper.readTree(resource.getFile());
        List<JsonNode> allSongs = StreamSupport.stream(allSongsNode.spliterator(), false).toList();
        JsonNode mostRecentSong = CatalogUtils.getMostRecentSong(allSongs);

        if (mostRecentSong != null) {
            return new ResponseEntity<>(mostRecentSong, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(objectMapper.createObjectNode().put("error", "No songs found"), HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/songs/longest")
    public ResponseEntity<JsonNode> getLongestSong() throws IOException {
        ClassPathResource resource = new ClassPathResource("data/popular_songs.json");
        ArrayNode allSongsNode = (ArrayNode) objectMapper.readTree(resource.getFile());
        List<JsonNode> allSongs = StreamSupport.stream(allSongsNode.spliterator(), false).toList();
        JsonNode longestSong = CatalogUtils.getLongestSong(allSongs);

        if (longestSong != null) {
            return new ResponseEntity<>(longestSong, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(objectMapper.createObjectNode().put("error", "No songs found"), HttpStatus.NOT_FOUND);
        }
    }










}