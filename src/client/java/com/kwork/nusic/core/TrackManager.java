package com.kwork.nusic.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackManager {

    private static final List<Track> tracks =
            new ArrayList<>();

    private TrackManager(){

    }

    public static synchronized List<Track> getTracks(){

        return Collections.unmodifiableList(
                new ArrayList<>(
                        tracks
                )
        );

    }

    public static synchronized void setTracks(
            List<Track> list
    ){

        tracks.clear();

        if(list != null){

            tracks.addAll(
                    list
            );

        }

    }

    public static synchronized void add(
            Track track
    ){

        if(track == null){

            return;

        }

        tracks.add(
                track
        );

    }

    public static synchronized void clear(){

        tracks.clear();

    }

    public static synchronized int size(){

        return tracks.size();

    }

}