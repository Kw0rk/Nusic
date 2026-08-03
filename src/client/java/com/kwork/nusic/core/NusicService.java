package com.kwork.nusic.core;

import java.io.File;
import java.util.List;

public class NusicService {

    private static NusicService instance;

    private final NusicManager manager;

    private NusicService(){

        manager =
                NusicManager.getInstance();

    }

    public static synchronized NusicService getInstance(){

        if(instance == null){

            instance =
                    new NusicService();

        }

        return instance;

    }

    public NusicManager getManager(){

        return manager;

    }

    public MusicPlayer getPlayer(){

        return manager.getPlayer();

    }

    public List<Track> getTracks(){

        return manager.getTracks();

    }

    public File getMusicFolder(){

        return manager.getMusicFolder();

    }

    public void reloadLibrary(){

        manager.reload();

    }

    public void play(
            Track track
    ){

        if(track == null){

            return;

        }

        manager.getPlayer()
                .play(
                        track
                );

    }

    public void playIndex(
            int index
    ){

        PlaybackManager
                .playIndex(
                        index
                );

    }

    public void pause(){

        manager.getPlayer()
                .pause();

    }

    public void resume(){

        manager.getPlayer()
                .resume();

    }

    public void togglePause(){

        manager.getPlayer()
                .togglePause();

    }

    public void stop(){

        manager.getPlayer()
                .stop();

    }

    public void next(){

        PlaybackManager
                .next();

    }

    public void previous(){

        PlaybackManager
                .previous();

    }

    public boolean isPlaying(){

        return manager.getPlayer()
                .isPlaying();

    }

    public boolean isPaused(){

        return manager.getPlayer()
                .isPaused();

    }

    public Track getCurrentTrack(){

        return manager.getPlayer()
                .getCurrentTrack();

    }

    public long getPosition(){

        return manager.getPlayer()
                .getPosition();

    }

    public long getDuration(){

        return manager.getPlayer()
                .getDuration();

    }

}