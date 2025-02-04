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
    void testGetAllAlbums() throws IOException {
        String url = "https://api.spotify.com/v1/albums/" ;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<Album> albums = new ArrayList<>();
        albums.add(newAlbum);
        Mockito.when(restTemplate.exchange(url,HttpMethod.GET,entity,new ParameterizedTypeReference<List<Album>>() {})).thenReturn(new ResponseEntity<>(albums,HttpStatus.OK));
        List<Album> res = spotifyAPIDataSources.getAllAlbums().getBody();
        assertNotNull(res);
        assertEquals(1,res.size());
        assertEquals("New Test Album",res.get(0).getName());
        assertEquals("new_test_album",res.get(0).getId());
    }

    @Test
    void testGetAlbumById() throws Exception {
        String albumId = "Ahmad_new_test_album";
        String url = "https://api.spotify.com/v1/albums/" + albumId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Mockito.when(restTemplate.exchange(url,HttpMethod.GET,  entity,Album.class)).thenReturn(new ResponseEntity(newAlbum,HttpStatus.OK));
        Album album = spotifyAPIDataSources.getAlbumById(albumId).getBody();
        assertNotNull(album);
        assertEquals(albumId, album.getId());
        assertEquals("New Test Album", album.getName());
    }

    @Test
    public void testGetAlbumTracks() throws IOException {
        String albumId = "Ahmad_new_test_album";
        String url = "https://api.spotify.com/v1/"+"albums/" + albumId + "/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer "+accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        List<Track> tracks = new ArrayList<>();
        tracks.add(track1);
        tracks.add(track2);
        Mockito.when(restTemplate.exchange(url,HttpMethod.GET,  entity,new ParameterizedTypeReference<List<Track>>() {})).thenReturn(new ResponseEntity(tracks,HttpStatus.OK));
        List<Track> res = spotifyAPIDataSources.getAlbumTracks(albumId).getBody();
        assertNotNull(res);
        assertEquals(2,res.size());
        assertEquals("track1",res.get(0).getId());
        assertEquals("track2",res.get(1).getId());
    }

    @Test
    void testGetArtists() throws IOException {
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
        Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Artist>>() {}))
                .thenReturn(new ResponseEntity<>(artists, HttpStatus.OK));
        List<Artist> res = spotifyAPIDataSources.getAllArtists().getBody();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("Artist One", res.get(0).getName());
        assertEquals("artist1", res.get(0).getId());
        assertEquals("Artist Two", res.get(1).getName());
        assertEquals("artist2", res.get(1).getId());
    }

    @Test
    void testGetArtistById() throws IOException {
        String artistId = "artist1";
        String url = "https://api.spotify.com/v1/artists/" + artistId;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Artist artist = new Artist();
        artist.setId("artist1");
        artist.setName("Artist One");
        Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, Artist.class))
                .thenReturn(new ResponseEntity<>(artist, HttpStatus.OK));
        Artist res = spotifyAPIDataSources.getArtistById(artistId).getBody();
        assertNotNull(res);
        assertEquals(artistId, res.getId());
        assertEquals("Artist One", res.getName());
    }

    @Test
    void testGetArtistAlbums() throws IOException {
        String artistId = "artist1";
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/albums";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Album album1 = new Album();
        album1.setId("album1");
        album1.setName("Artist One Album");
        Album album2 = new Album();
        album2.setId("album2");
        album2.setName("Artist One Album 2");
        List<Album> albums = Arrays.asList(album1, album2);
        Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Album>>() {}))
                .thenReturn(new ResponseEntity<>(albums, HttpStatus.OK));
        List<Album> res = spotifyAPIDataSources.getArtistAlbums(artistId).getBody();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("Artist One Album", res.get(0).getName());
        assertEquals("album1", res.get(0).getId());
        assertEquals("Artist One Album 2", res.get(1).getName());
        assertEquals("album2", res.get(1).getId());
    }

    @Test
    void testGetArtistSongs() throws IOException {
        String artistId = "artist1";
        String url = "https://api.spotify.com/v1/artists/" + artistId + "/tracks";  // Assuming endpoint structure

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Song song1 = new Song();
        song1.setId("song1");
        song1.setName("First Song");
        Song song2 = new Song();
        song2.setId("song2");
        song2.setName("Second Song");
        List<Song> songs = Arrays.asList(song1, song2);
        Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Song>>() {}))
                .thenReturn(new ResponseEntity<>(songs, HttpStatus.OK));
        List<Song> res = spotifyAPIDataSources.getArtistSongs(artistId).getBody();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("First Song", res.get(0).getName());
        assertEquals("song1", res.get(0).getId());
        assertEquals("Second Song", res.get(1).getName());
        assertEquals("song2", res.get(1).getId());
    }

    @Test
    void testGetAllSongs() throws IOException {
        String url = "https://api.spotify.com/v1/tracks";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Song song1 = new Song();
        song1.setId("song1");
        song1.setName("First Song");
        Song song2 = new Song();
        song2.setId("song2");
        song2.setName("Second Song");
        List<Song> songs = Arrays.asList(song1, song2);
        Mockito.when(restTemplate.exchange(url, HttpMethod.GET, entity, new ParameterizedTypeReference<List<Song>>() {}))
                .thenReturn(new ResponseEntity<>(songs, HttpStatus.OK));
        List<Song> res = spotifyAPIDataSources.getAllSongs().getBody();
        assertNotNull(res);
        assertEquals(2, res.size());
        assertEquals("First Song", res.get(0).getName());
        assertEquals("song1", res.get(0).getId());
        assertEquals("Second Song", res.get(1).getName());
        assertEquals("song2", res.get(1).getId());
    }

}
