package com.example.catalog.services;// src/main/java/com/example/catalog/services/DataSourceService.java

import com.example.catalog.model.*;
import org.springframework.http.ResponseEntity;

import java.io.IOException;
import java.util.List;

public interface DataSourceService {


    ResponseEntity<List<Album>> getAllAlbums() throws IOException;

    ResponseEntity<Album> getAlbumById(String albumId) throws IOException;

    ResponseEntity<Void> addAlbum(Album album) throws IOException;

    ResponseEntity<Void> updateAlbum(String albumId, Album updatedAlbum) throws IOException;

    ResponseEntity<Void> deleteAlbumById(String albumId) throws IOException;

    ///  Track

    ResponseEntity<List<Track>> getAlbumTracks(String albumId) throws IOException;

    ResponseEntity<Void> addTrackToAlbum(Track track, String albumId) throws IOException;

    ResponseEntity<Void> updateTrack(String albumId, String trackId, Track updatedTrack) throws IOException;

    ResponseEntity<Void> deleteTrackFromAlbum(String albumId, String trackId) throws IOException;

    ///  Artist

    ResponseEntity<Artist> getArtistById(String artistId) throws IOException;

    ResponseEntity<List<Artist>> getAllArtists() throws IOException;

    ResponseEntity<Void> addArtist(Artist artist) throws IOException;

    ResponseEntity<Void> updateArtist(String artistId, Artist updatedArtist) throws IOException;

    ResponseEntity<Void> deleteArtistById(String artistId) throws IOException;

    ResponseEntity<List<Album>> getArtistAlbums(String artistId) throws IOException;

    /// Songs

    ResponseEntity<List<Song>> getArtistSongs(String artistId) throws IOException;

    ResponseEntity<List<Song>> getAllSongs() throws IOException;

    ResponseEntity<Song> getSongById(String songId) throws IOException;

    ResponseEntity<Void> addSong(Song song) throws IOException;

    ResponseEntity<Void> updateSong(String songId, Song updatedSong) throws IOException;

    ResponseEntity<Void> deleteSongById(String songId) throws IOException;
}