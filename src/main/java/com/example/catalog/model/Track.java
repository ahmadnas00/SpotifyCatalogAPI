package com.example.catalog.model;

public class Track {

    private String id;
    private String name;
    private String uri;
    private int duration_ms;
    private boolean explicit;

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUri() {
        return uri;
    }

    public int getDuration_ms() {
        return duration_ms;
    }

    public boolean isExplicit() {
        return explicit;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public void setDuration_ms(int duration_ms) {
        this.duration_ms = duration_ms;
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
    }
}
