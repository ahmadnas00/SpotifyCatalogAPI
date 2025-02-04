package com.example.catalog.services;

import com.example.catalog.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ActiveProfiles("test") // Ensures we're using the test profile
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class SpotifyAPIDataSourcesTest {

    @InjectMocks
    @Spy
    private SpotifyAPIDataSources spotifyAPIDataSources;

    @Mock
    private RestTemplate restTemplate;

    @Value("${SpotifyAPIDataSources.token}")
    private String accessToken;

    private MockMvc mockMvc;
    private Album newAlbum;
    private Track track1;
    private Track track2;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        newAlbum = new Album();
        newAlbum.setId("new_test_album");
        newAlbum.setName("New Test Album");
        newAlbum.setTotal_tracks(2);
        newAlbum.setRelease_date("2025-02-01");
        Image image = new Image();
        image.setUrl("http://example.com/image.jpg");
        image.setHeight(300);
        image.setWidth(300);
        newAlbum.setImages(Arrays.asList(image));
        track1 = new Track();
        track1.setId("track1");
        track1.setName("Track One");
        track1.setUri("http://example.com/track1");
        track1.setDuration_ms(210000); // 3:30 minutes
        track1.setExplicit(false);
        track2 = new Track();
        track2.setId("track2");
        track2.setName("Track Two");
        track2.setUri("http://example.com/track2");
        track2.setDuration_ms(240000); // 4 minutes
        track2.setExplicit(true);
        newAlbum.setTracks(Arrays.asList(track1, track2));
    }

    @Test
    void testGetAlbumById() {
        String albumId = "new_test_album";
        String url = "https://api.spotify.com/v1/albums/" + albumId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "mockAccessToken");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Album newAlbum = new Album();
        newAlbum.setId(albumId);
        newAlbum.setName("New Test Album");
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Album.class)
        )).thenReturn(new ResponseEntity<>(newAlbum, HttpStatus.OK));
        ResponseEntity<Album> responseEntity = spotifyAPIDataSources.getAlbumById(albumId);
        Album album = responseEntity.getBody();
        assertNotNull(album, "Album should not be null");
        assertEquals(albumId, album.getId(), "Album ID should match");
        assertEquals("New Test Album", album.getName(), "Album name should match");
    }

    @Test
    public void testGetAlbumTracks() throws IOException {
        String albumId = "new_test_album";
        String url = "https://api.spotify.com/v1/albums/" + albumId + "/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Track track1 = new Track();
        track1.setId("track1");
        track1.setName("Track One");
        Track track2 = new Track();
        track2.setId("track2");
        track2.setName("Track Two");
        List<Track> tracks = Arrays.asList(track1, track2);
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Track[].class)
        )).thenReturn(new ResponseEntity<>(tracks.toArray(new Track[0]), HttpStatus.OK));
        ResponseEntity<List<Track>> responseEntity = spotifyAPIDataSources.getAlbumTracks(albumId);
        List<Track> res = responseEntity.getBody();  // Extract the list from ResponseEntity
        assertNotNull(res, "Response body should not be null");
        assertEquals(2, res.size(), "The number of tracks should match");
        assertEquals("track1", res.get(0).getId(), "Track ID should match");
        assertEquals("track2", res.get(1).getId(), "Track ID should match");
    }

    @Test
    void testGetAllArtists() throws IOException {
        String url = "https://api.spotify.com/v1/artists";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Artist artist1 = new Artist();
        artist1.setId("artist1");
        artist1.setName("Artist One");
        Artist artist2 = new Artist();
        artist2.setId("artist2");
        artist2.setName("Artist Two");
        List<Artist> artists = Arrays.asList(artist1, artist2);
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Artist[].class)
        )).thenReturn(new ResponseEntity<>(artists.toArray(new Artist[0]), HttpStatus.OK));
        ResponseEntity<List<Artist>> responseEntity = spotifyAPIDataSources.getAllArtists();
        List<Artist> res = responseEntity.getBody();  // Extract the list from ResponseEntity
        assertNotNull(res, "Response body should not be null");
        assertEquals(2, res.size(), "The number of artists should match");
        assertEquals("Artist One", res.get(0).getName(), "Artist name should match");
        assertEquals("artist1", res.get(0).getId(), "Artist ID should match");
        assertEquals("Artist Two", res.get(1).getName(), "Artist name should match");
        assertEquals("artist2", res.get(1).getId(), "Artist ID should match");
    }

    @Test
    void testGetArtistById() {
        String artistId = "artist1";
        String url = "https://api.spotify.com/v1/artists/" + artistId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + "mockAccessToken");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Artist artist = new Artist();
        artist.setId(artistId);
        artist.setName("Artist One");
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Artist.class)
        )).thenReturn(new ResponseEntity<>(artist, HttpStatus.OK));
        ResponseEntity<Artist> responseEntity = spotifyAPIDataSources.getArtistById(artistId);
        Artist res = responseEntity.getBody();
        assertNotNull(res, "Artist should not be null");
        assertEquals(artistId, res.getId(), "Artist ID should match");
        assertEquals("Artist One", res.getName(), "Artist name should match");
    }

    @Test
    void testGetArtistAlbums() {
        String artistId = "artist1";
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/albums";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mockAccessToken");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Album album1 = new Album();
        album1.setId("album1");
        album1.setName("Artist One Album");
        Album album2 = new Album();
        album2.setId("album2");
        album2.setName("Artist One Album 2");
        Album[] albumsArray = new Album[]{album1, album2};
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Album[].class)
        )).thenReturn(new ResponseEntity<>(albumsArray, HttpStatus.OK));
        ResponseEntity<List<Album>> responseEntity = spotifyAPIDataSources.getArtistAlbums(artistId);
        List<Album> res = responseEntity.getBody();  // Extract the List<Album>
        assertNotNull(res, "Response body should not be null");
        assertEquals(2, res.size(), "There should be exactly 2 albums");
        assertEquals("Artist One Album", res.get(0).getName(), "First album name should match");
        assertEquals("album1", res.get(0).getId(), "First album ID should match");
        assertEquals("Artist One Album 2", res.get(1).getName(), "Second album name should match");
        assertEquals("album2", res.get(1).getId(), "Second album ID should match");
    }

    @Test
    void testGetArtistSongs() {
        String artistId = "artist1";
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/top-tracks?market=US";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mockAccessToken");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Song song1 = new Song();
        song1.setId("song1");
        song1.setName("Song One");
        Song song2 = new Song();
        song2.setId("song2");
        song2.setName("Song Two");
        Song[] songsArray = new Song[]{song1, song2};
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.eq(Song[].class)
        )).thenReturn(new ResponseEntity<>(songsArray, HttpStatus.OK));
        ResponseEntity<List<Song>> responseEntity = spotifyAPIDataSources.getArtistSongs(artistId);
        List<Song> res = responseEntity.getBody();  // Extract the List<Song>
        assertNotNull(res, "Response body should not be null");
        assertEquals(2, res.size(), "There should be exactly 2 songs");
        assertEquals("Song One", res.get(0).getName(), "First song name should match");
        assertEquals("song1", res.get(0).getId(), "First song ID should match");
        assertEquals("Song Two", res.get(1).getName(), "Second song name should match");
        assertEquals("song2", res.get(1).getId(), "Second song ID should match");
    }

    @Test
    void testGetAllSongs() {
        String url = "https://api.spotify.com/v1/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer mockAccessToken");
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Song song1 = new Song();
        song1.setId("song1");
        song1.setName("Song One");
        Song song2 = new Song();
        song2.setId("song2");
        song2.setName("Song Two");
        List<Song> songs = Arrays.asList(song1, song2);
        Mockito.when(restTemplate.exchange(
                ArgumentMatchers.eq(url),
                ArgumentMatchers.eq(HttpMethod.GET),
                ArgumentMatchers.any(HttpEntity.class),
                ArgumentMatchers.<ParameterizedTypeReference<List<Song>>>any()
        )).thenReturn(new ResponseEntity<>(songs, HttpStatus.OK));
        ResponseEntity<List<Song>> responseEntity = spotifyAPIDataSources.getAllSongs();
        List<Song> res = responseEntity.getBody();  // Extract the List<Song>
        assertNotNull(res, "Response body should not be null");
        assertEquals(2, res.size(), "There should be exactly 2 songs");
        assertEquals("Song One", res.get(0).getName(), "First song name should match");
        assertEquals("song1", res.get(0).getId(), "First song ID should match");
        assertEquals("Song Two", res.get(1).getName(), "Second song name should match");
        assertEquals("song2", res.get(1).getId(), "Second song ID should match");
    }
}
