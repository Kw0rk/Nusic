package com.kwork.nusic.data;

import java.util.ArrayList;
import java.util.List;

public class Playlist {

    private String name;

    private final List<String> trackPaths = new ArrayList<>();

    private long createdTime = System.currentTimeMillis();

    public Playlist() {
    }

    public Playlist(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTrackPaths() {
        return trackPaths;
    }

    public long getCreatedTime() {
        return createdTime;
    }

    public boolean contains(String path) {
        return trackPaths.contains(path);
    }

    public void add(String path) {
        if (path != null && !trackPaths.contains(path)) {
            trackPaths.add(path);
        }
    }

    public void remove(String path) {
        trackPaths.remove(path);
    }

    public int size() {
        return trackPaths.size();
    }

}
