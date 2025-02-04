package com.example.catalog.services;

import com.example.catalog.model.Album;
import com.example.catalog.model.Artist;
import com.example.catalog.model.Song;
import com.example.catalog.model.Track;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class JSONDataSourceService implements DataSourceService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${jsonDataSourceService.data}")
    private String dataDir;

    // ------------------------ Albums ------------------------

    @Override
    public ResponseEntity<List<Album>> getAllAlbums() throws IOException {
        List<Album> albums = new ArrayList<>(loadAlbumMap().values());
        if (albums.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(albums);
    }

    @Override
    public ResponseEntity<Album> getAlbumById(String albumId) throws IOException {
        Album album = loadAlbumMap().get(albumId);
        if (album == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(album);
    }

    @Override
    public ResponseEntity<Void> updateAlbum(String albumId, Album updatedAlbum) throws IOException {
        ResponseEntity<Void> deleteResponse = deleteAlbumById(albumId);

        if (deleteResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        addAlbum(updatedAlbum);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> addAlbum(Album newAlbum) throws IOException {
        JsonNode root = loadJsonData(dataDir + "/albums.json");
        ((ObjectNode) root).set(newAlbum.getId(), objectMapper.valueToTree(newAlbum));
        ClassPathResource path = new ClassPathResource(dataDir + "/albums.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.getFile(), root);
        return new ResponseEntity<>(HttpStatus.OK); // ✅ Return 200 OK
    }


    @Override
    public ResponseEntity<Void> deleteAlbumById(String albumId) throws IOException {
        JsonNode rootNode = loadJsonData(dataDir + "/albums.json");
        ((ObjectNode) rootNode).remove(albumId);
        ClassPathResource path = new ClassPathResource(dataDir + "/albums.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.getFile(), rootNode);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    // ------------------------ Tracks ------------------------

    @Override
    public ResponseEntity<List<Track>> getAlbumTracks(String albumId) throws IOException {
        ResponseEntity<Album> response = getAlbumById(albumId);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Track> tracks = response.getBody().getTracks();
        if (tracks.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(tracks);
    }

    @Override
    public ResponseEntity<Void> addTrackToAlbum(Track newTrack, String albumId) throws IOException {
        Album album = getAlbumById(albumId).getBody();
        if (album == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        album.getTracks().add(newTrack);
        updateAlbum(albumId, album);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateTrack(String albumId, String trackId, Track updatedTrack) throws IOException {
        Album album = getAlbumById(albumId).getBody();
        if (album == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Track> tracks = album.getTracks();
        for (int i = 0; i < tracks.size(); i++) {
            if (tracks.get(i).getId().equals(trackId)) {
                tracks.set(i, updatedTrack);
                updateAlbum(albumId, album);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> deleteTrackFromAlbum(String albumId, String trackId) throws IOException {
        Album album = getAlbumById(albumId).getBody();
        if (album == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        List<Track> tracks = album.getTracks();
        boolean removed = tracks.removeIf(track -> track.getId().equals(trackId));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        updateAlbum(albumId, album);
        return ResponseEntity.noContent().build();
    }

    // ------------------------ Artists ------------------------

    @Override
    public ResponseEntity<Artist> getArtistById(String id) throws IOException {
        JsonNode artists = loadJsonData(dataDir+"/popular_artists.json");
        JsonNode artistNode = artists.get(id);
        if (artistNode == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        Artist artist = objectMapper.treeToValue(artistNode, Artist.class);
        return new ResponseEntity<>(artist,HttpStatus.OK);
    }

    @Override
    public ResponseEntity<List<Artist>> getAllArtists() throws IOException {
        List<Artist> artists = loadArtistList();
        if (artists.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(artists);
    }

    @Override
    public ResponseEntity<Void> addArtist(Artist artist) throws IOException {
        JsonNode root =loadJsonData(dataDir+"/popular_artists.json");
        ((ObjectNode) root).set(artist.getId(), objectMapper.valueToTree(artist));
        ClassPathResource path= new ClassPathResource(dataDir+"/popular_artists.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.getFile(), root);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Override
    public ResponseEntity<Void> deleteArtistById(String artistId) throws IOException {
        JsonNode rootNode = loadJsonData(dataDir+"/popular_artists.json");
        ((ObjectNode) rootNode).remove(artistId);
        ClassPathResource path= new ClassPathResource(dataDir+"/popular_artists.json");
        objectMapper.writerWithDefaultPrettyPrinter().writeValue(path.getFile(), rootNode);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Override
    public ResponseEntity<Void> updateArtist(String artistId, Artist updatedArtist) throws IOException {
        ResponseEntity<Void> deleteResponse = deleteArtistById(artistId);
        if (deleteResponse.getStatusCode() == HttpStatus.NOT_FOUND) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        addArtist(updatedArtist);
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @Override
    public ResponseEntity<List<Album>> getArtistAlbums(String artistId) throws IOException {
        List<Song> allSongs = loadSongList();
        List<Album> allAlbums = loadAlbumList();
        Set<String> trackNames = allSongs.stream()
                .filter(song -> song.getArtists() != null && song.getArtists().stream()
                        .anyMatch(artist -> artist.getId().equals(artistId)))
                .map(Song::getName)
                .collect(Collectors.toSet());
        if (trackNames.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        List<Album> artistAlbums = allAlbums.stream()
                .filter(album -> album.getTracks() != null && album.getTracks().stream()
                        .anyMatch(track -> trackNames.contains(track.getName())))
                .collect(Collectors.toList());
        if (artistAlbums.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(artistAlbums);
    }






    @Override
    public ResponseEntity<List<Song>> getArtistSongs(String artistId) throws IOException {
        List<Song> allSongs = loadSongList();
        List<Song> artistSongs = allSongs.stream()
                .filter(song -> song.getArtists().stream()
                        .anyMatch(artist -> artist.getId().equals(artistId)))
                .collect(Collectors.toList());
        if (artistSongs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(artistSongs);
    }


    // ------------------------ Songs ------------------------

    @Override
    public ResponseEntity<List<Song>> getAllSongs() throws IOException {
        List<Song> songs = loadSongList();
        if (songs.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(songs);
    }





    @Override
    public ResponseEntity<Song> getSongById(String songId) throws IOException {
        Song song = loadSongList()
                .stream()
                .filter(s -> s.getId().equals(songId))
                .findFirst()
                .orElse(null);
        if (song == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok(song);
    }

    @Override
    public ResponseEntity<Void> addSong(Song newSong) throws IOException {
        List<Song> songs = loadSongList();
        songs.add(newSong);
        saveJsonData(dataDir+"/popular_songs.json", songs);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<Void> updateSong(String songId, Song updatedSong) throws IOException {
        List<Song> songs = loadSongList();
        for (int i = 0; i < songs.size(); i++) {
            if (songs.get(i).getId().equals(songId)) {
                songs.set(i, updatedSong);
                saveJsonData(dataDir+"/popular_songs.json", songs);
                return ResponseEntity.noContent().build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @Override
    public ResponseEntity<Void> deleteSongById(String songId) throws IOException {
        List<Song> songs = loadSongList();
        boolean removed = songs.removeIf(song -> song.getId().equals(songId));
        if (!removed) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        saveJsonData(dataDir+"/popular_songs.json", songs);
        return ResponseEntity.noContent().build();
    }

    // ------------------------ Helper Methods ------------------------

    private JsonNode loadJsonData(String path) throws IOException {
        ClassPathResource resource = new ClassPathResource(path);
        return objectMapper.readTree(resource.getFile());
    }

    private void saveJsonData(String path, Object data) throws IOException {
        File file = new ClassPathResource(path).getFile();
        objectMapper.writeValue(file, data);
    }

    private Map<String, Album> loadAlbumMap() throws IOException {
        JsonNode albumsNode = loadJsonData(dataDir + "/albums.json");
        return objectMapper.convertValue(albumsNode, new TypeReference<Map<String, Album>>() {});
    }

    private List<Album> loadAlbumList() throws IOException {
        JsonNode albumsNode = loadJsonData(dataDir + "/albums.json");
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Album.class);
        return objectMapper.convertValue(albumsNode, listType);
    }


    private List<Artist> loadArtistList() throws IOException {
        JsonNode artistsNode = loadJsonData(dataDir+"/popular_artists.json");
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Artist.class);
        return objectMapper.convertValue(artistsNode.elements(), listType);
    }

    public Map<String, Artist> loadArtistMap() throws IOException {
        JsonNode artistsNode = loadJsonData(dataDir + "/popular_artists.json");

        if (artistsNode.isArray()) {
            List<Artist> artistList = objectMapper.convertValue(artistsNode, new TypeReference<List<Artist>>() {});
            return artistList.stream().collect(Collectors.toMap(Artist::getId, artist -> artist));
        } else {
            return objectMapper.convertValue(artistsNode, new TypeReference<Map<String, Artist>>() {});
        }
    }


    private List<Song> loadSongList() throws IOException {
        JsonNode songsNode = loadJsonData(dataDir + "/popular_songs.json");
        CollectionType listType = objectMapper.getTypeFactory().constructCollectionType(List.class, Song.class);
        return objectMapper.convertValue(songsNode, listType);
    }




//    private List<Song> loadSongList() throws IOException {
//        JsonNode songsNode = loadJsonData(dataDir+"/popular_songs.json");
//        return objectMapper.convertValue(songsNode, new TypeReference<List<Song>>() {});
//    }
}
