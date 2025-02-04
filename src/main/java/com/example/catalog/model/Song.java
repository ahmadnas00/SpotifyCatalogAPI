package com.example.catalog.model;

import java.util.List;

public class Song {

    private String id;
    private String name;
    private String uri;
    private int duration_ms;
    private int popularity;
    private Album album;
    private List<Artist> artists;



    public int getDuration_ms() {
        return duration_ms;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public int getPopularity() {
        return popularity;
    }

    public Album getAlbum() {
        return album;
    }

    public List<Artist> getArtists() {
        return artists;
    }


    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setPopularity(int popularity) {
        this.popularity = popularity;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public void setArtists(List<Artist> artists) {
        this.artists = artists;
    }


    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }
}
