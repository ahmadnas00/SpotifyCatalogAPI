package com.example.catalog.services;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class SpotifyAPIDataSources implements DataSourceService {


    private static final String BASE_URL = "https://api.spotify.com/v1/";
    private RestTemplate restTemplate = new RestTemplate();

    @Value("${SpotifyAPIDataSources.token}")
    private String accessToken = "BQAgB9iUzuPZ1lgvD94jXaMfBNWrz_2HKv_X_UgpCv7FM1D_szJBIt7OhX4ZzJd4eIopAInIm_X5reI8KdVSAKZOeEmQIvxe2XSAQsWCjkq2HfOCztgLr3gG7YlYFkty8L35jFjFQ6o";

    private HttpHeaders getAuthHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        return headers;
    }


    // ------------------------ GET Requests ------------------------

    @Override
    public ResponseEntity<Artist> getArtistById(String artistId) {
        String url = BASE_URL + "artists/" + artistId;
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());

        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, Artist.class);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<Album>> getAllAlbums() {
        String url = BASE_URL + "browse/new-releases";
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());
        try {
            ResponseEntity<Album[]> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    Album[].class
            );
            return ResponseEntity.ok(Arrays.asList(response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<Album> getAlbumById(String albumId) {
        String url = BASE_URL + "albums/" + albumId;
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());

        try {
            ResponseEntity<Album> response = restTemplate.exchange(url, HttpMethod.GET, entity, Album.class);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<Track>> getAlbumTracks(String albumId) {
        String url = BASE_URL + "albums/" + albumId + "/tracks";
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());

        try {
            ResponseEntity<Track[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Track[].class);
            return ResponseEntity.ok(Arrays.asList(response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<Artist>> getAllArtists() {
        // Spotify API does NOT provide an endpoint for all artists, so returning 501 Not Implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<List<Album>> getArtistAlbums(String artistId) {
        String url = BASE_URL + "artists/" + artistId + "/albums";
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());

        try {
            ResponseEntity<Album[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Album[].class);
            return ResponseEntity.ok(Arrays.asList(response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<Song>> getArtistSongs(String artistId) {
        String url = BASE_URL + "artists/" + artistId + "/top-tracks?market=US";
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());

        try {
            ResponseEntity<Song[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, Song[].class);
            return ResponseEntity.ok(Arrays.asList(response.getBody()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @Override
    public ResponseEntity<List<Song>> getAllSongs() {
        // Spotify API does NOT provide an endpoint for all songs, so returning 501 Not Implemented
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build();
    }

    @Override
    public ResponseEntity<Song> getSongById(String songId) {
        String url = BASE_URL + "tracks/" + songId;
        HttpEntity<String> entity = new HttpEntity<>(getAuthHeaders());

        try {
            ResponseEntity<Song> response = restTemplate.exchange(url, HttpMethod.GET, entity, Song.class);
            return response;
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    // ------------------------ Unsupported Operations ------------------------

    @Override
    public ResponseEntity<Void> addAlbum(Album album) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> updateAlbum(String albumId, Album updatedAlbum) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> deleteAlbumById(String albumId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> addTrackToAlbum(Track track, String albumId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> updateTrack(String albumId, String trackId, Track updatedTrack) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> deleteTrackFromAlbum(String albumId, String trackId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> addArtist(Artist artist) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> updateArtist(String artistId, Artist updatedArtist) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> deleteArtistById(String artistId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> addSong(Song song) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> updateSong(String songId, Song updatedSong) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> deleteSongById(String songId) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

}
