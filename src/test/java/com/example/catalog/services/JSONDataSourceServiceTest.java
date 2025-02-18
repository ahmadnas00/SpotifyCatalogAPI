package com.example.catalog.services;

import com.beust.jcommander.*;  // If using JCommander
import org.testng.*;  // If using TestNG

import com.example.catalog.model.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@SpringBootTest(classes = JSONDataSourceService.class)

public class JSONDataSourceServiceTest {

    @Autowired
    private JSONDataSourceService jsonDataSourceService;
    private String ArtistID = "1Xyo4u8uXC1ZmMpatF05PJ";
    private String SongID = "0VjIjW4GlUZAMYd2vXMi3b";
    private String AlbumID = "4yP0hdKOZPNshxUOjY0cZj";

    ///  Artists

   @Test
    public void TestGetArtistById() throws IOException {
       Artist artist = jsonDataSourceService.getArtistById(ArtistID).getBody();
       assertNotNull(artist);
       assertEquals("The Weeknd", artist.getName());

       ResponseEntity<Artist> response = jsonDataSourceService.getArtistById("123");
       Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
       Assertions.assertNull(response.getBody());
   }

    @Test
    public void TestGetAllArtists() throws IOException {
        List<Artist> Artists = jsonDataSourceService.getAllArtists().getBody();
        assertNotNull(Artists);
        assertEquals("The Weeknd",Artists.get(0).getName());
    }

    @Test
    public void Test_Add_Then_Delete_Artist() throws IOException {
        Artist newArtist = new Artist();
        newArtist.setId("123456789abcdefghijklm");
        newArtist.setName("Ahmad Nassar");
        newArtist.setFollowers(9999);
        newArtist.setGenres(List.of("canadian contemporary r&b", "canadian pop", "pop"));
        newArtist.setPopularity(99);
        newArtist.setUri("spotify:artist:1Xyo4u8uXC1ZmMpatF05PJ");
        List<Image> images = null;
        newArtist.setImages(images);

        ResponseEntity<Void> addResponse = jsonDataSourceService.addArtist(newArtist);
        assertEquals(HttpStatus.OK, addResponse.getStatusCode());

        ResponseEntity<Artist> getResponse = jsonDataSourceService.getArtistById("123456789abcdefghijklm");
        assertEquals("Ahmad Nassar", getResponse.getBody().getName());

        ResponseEntity<Void> deleteResponse = jsonDataSourceService.deleteArtistById("123456789abcdefghijklm");
        assertEquals(HttpStatus.OK, deleteResponse.getStatusCode());
        ResponseEntity<Artist> getDeletedResponse = jsonDataSourceService.getArtistById("123456789abcdefghijklm");
        assertEquals(HttpStatus.NOT_FOUND, getDeletedResponse.getStatusCode());
        assertNull(getDeletedResponse.getBody());
    }

    ///  Songs

    @Test
    public void TestGetSongById() throws IOException {
       Song song = jsonDataSourceService.getSongById(SongID).getBody();
       assertNotNull(song);
       assertEquals("Blinding Lights",song.getName());
       ResponseEntity<Song> response = jsonDataSourceService.getSongById("123");
       Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
       Assertions.assertNull(response.getBody());
   }

    @Test
    public void TestGetAllSongs() throws IOException {
        ResponseEntity<List<Song>> Songresponse = jsonDataSourceService.getAllSongs();
        assertEquals(HttpStatus.OK, Songresponse.getStatusCode());
        assertEquals(100,Songresponse.getBody().size());
        boolean found = Songresponse.getBody().stream().anyMatch(song -> "STAY (with Justin Bieber)".equals(song.getName()));
        assertTrue(found);
    }

    @Test
    public void TestGetArtistSongs() throws IOException {
        ResponseEntity<List<Song>> ArtistSongsresponse = jsonDataSourceService.getArtistSongs(ArtistID);
        assertNotNull(ArtistSongsresponse);
        assertEquals(HttpStatus.OK, ArtistSongsresponse.getStatusCode());
        List<Song> songs = ArtistSongsresponse.getBody();
        boolean found = songs.stream().anyMatch(song -> "Blinding Lights".equals(song.getName()));
        assertTrue(found);
    }

    ///  Albums

    @Test
    public void TestGetAlbumById() throws IOException {
       Album album = jsonDataSourceService.getAlbumById(AlbumID).getBody();
       assertNotNull(album);
       assertEquals("After Hours",album.getName());
       ResponseEntity<Album> response = jsonDataSourceService.getAlbumById("123");
       Assertions.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
       Assertions.assertNull(response.getBody());
   }

    @Test
    public void TestGetAllAlbums() throws IOException {
        ResponseEntity<List<Album>> Albumresponse = jsonDataSourceService.getAllAlbums();
        assertNotNull(Albumresponse);
        boolean found = Albumresponse.getBody().stream().anyMatch(song -> "After Hours".equals(song.getName()));
        assertTrue(found);
    }

    @Test
    public void TestUpdateAlbum() throws IOException {
        Album updatedAlbum = new Album();
        updatedAlbum.setId("5658aM19fA3JVwTK6eQX70");
        updatedAlbum.setName("MyNewAlbum");
        updatedAlbum.setUri("spotify:album:5658aM19fA3JVwTK6eQX70");
        updatedAlbum.setRelease_date("2019-05-17");
        updatedAlbum.setTotal_tracks(0);
        updatedAlbum.setImages(null);
        updatedAlbum.setTracks(null);

        jsonDataSourceService.updateAlbum("5658aM19fA3JVwTK6eQX70",updatedAlbum);
        Album album = jsonDataSourceService.getAlbumById("5658aM19fA3JVwTK6eQX70").getBody();
        assertNotNull(album);
        assertEquals("MyNewAlbum",album.getName());
    }

    /// Tracks

    @Test
    public void TestgetAlbumTracks() throws IOException {
        ResponseEntity<List<Track>> Trackrespone = jsonDataSourceService.getAlbumTracks(AlbumID);
        assertNotNull(Trackrespone);
        boolean found = Trackrespone.getBody().stream().anyMatch(song -> "Alone Again".equals(song.getName()));
        assertTrue(found);
    }

    @Test
    public void TestAddTrackToAlbum() throws IOException{
        Track newTrack = new Track();
        newTrack.setId("7b5P51m8xx2XA6U7sdNZ5E");
        newTrack.setDuration_ms(250053);
        newTrack.setName("Alone Again2");
        newTrack.setExplicit(true);
        newTrack.setUri("spotify:track:7b5P51m8xx2XA6U7sdNZ5E");

        ResponseEntity<Void> TrackResponse = jsonDataSourceService.addTrackToAlbum(newTrack,"4yP0hdKOZPNshxUOjY0cZj");
        ResponseEntity<List<Track>> Trackrespone = jsonDataSourceService.getAlbumTracks("4yP0hdKOZPNshxUOjY0cZj");
        boolean found = Trackrespone.getBody().stream().anyMatch(song -> "Alone Again2".equals(song.getName()));
        assertTrue(found);
    }

}
