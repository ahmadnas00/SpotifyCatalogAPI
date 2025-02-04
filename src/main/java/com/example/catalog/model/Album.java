package com.example.catalog.model;

import java.util.Collection;
import java.util.List;

public class Album {

    private String id;
    private String name;
    private String uri;
    private String release_date;
    private int total_tracks;
    private List<Track> tracks;
    private List<Image> images;


    public String getId() {
        return id;
    }

    public String getUri() {
        return uri;
    }

    public String getName() {
        return name;
    }

    public String getRelease_date() {
        return release_date;
    }

    public List<Track> getTracks() {
        return tracks;
    }

    public int getTotal_tracks() {
        return total_tracks;
    }

    public List<Image> getImages() {
        return images;
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

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks;
    }

    public void setTotal_tracks(int total_tracks) {
        this.total_tracks = total_tracks;
    }

    public void setImages(List<Image> images) {
        this.images = images;
    }

}



