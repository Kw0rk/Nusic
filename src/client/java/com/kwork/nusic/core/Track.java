package com.kwork.nusic.core;

import java.io.File;

public class Track {

    private final String name;

    private final String path;

    private long duration;

    private String artist;

    private String album;

    private String coverPath;

    private long addedTime;

    public Track(
            String name,
            String path
    ){

        this.name = name;

        this.path = path;

        this.duration = 0;

        this.artist = "Unknown";

        this.album = "Unknown";

        this.coverPath = null;

        this.addedTime =
                System.currentTimeMillis();

    }

    public String getName(){

        return name;

    }

    public String getPath(){

        return path;

    }

    public File getFile(){

        return new File(
                path
        );

    }

    public boolean exists(){

        return getFile()
                .exists();

    }

    public long getDuration(){

        return duration;

    }

    public void setDuration(
            long duration
    ){

        this.duration =
                duration;

    }

    public String getArtist(){

        return artist;

    }

    public void setArtist(
            String artist
    ){

        this.artist =
                artist;

    }

    public String getAlbum(){

        return album;

    }

    public void setAlbum(
            String album
    ){

        this.album =
                album;

    }

    public String getCoverPath(){

        return coverPath;

    }

    public void setCoverPath(
            String coverPath
    ){

        this.coverPath =
                coverPath;

    }

    public long getAddedTime(){

        return addedTime;

    }

    public String getExtension(){

        int dot =
                name.lastIndexOf('.');

        if(dot == -1){

            return "";

        }

        return name.substring(
                dot + 1
        )
                .toLowerCase();

    }

    public String getDisplayName(){

        int dot =
                name.lastIndexOf('.');

        if(dot <= 0){

            return name;

        }

        return name.substring(
                0,
                dot
        );

    }

    @Override
    public String toString(){

        return getDisplayName();

    }

}