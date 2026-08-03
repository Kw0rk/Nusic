package com.kwork.nusic.data;

import com.kwork.nusic.core.Track;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private final List<Track> tracks =
            new ArrayList<>();

    public void add(Track track){

        if(track != null){

            tracks.add(track);

        }

    }

    public void clear(){

        tracks.clear();

    }

    public List<Track> getTracks(){

        return tracks;

    }

    public int size(){

        return tracks.size();

    }

    public boolean isEmpty(){

        return tracks.isEmpty();

    }

}