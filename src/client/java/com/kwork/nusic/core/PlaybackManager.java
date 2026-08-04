package com.kwork.nusic.core;

import com.kwork.nusic.data.SettingsData;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlaybackManager {

    private static final List<Track> queue =
            new ArrayList<>();

    private static final MusicPlayer player =
            new MusicPlayer();

    private static int index = -1;

    private static SettingsData settings;

    private static final Random random = new Random();

    static {

        player.setOnFinished(() -> {

            if(settings != null && settings.repeatTrack){

                play();

                return;

            }

            if(settings == null || settings.autoNext){

                advance(true);

            }

        });

    }

    public static MusicPlayer getPlayer(){

        return player;

    }

    public static synchronized void setSettings(
            SettingsData data
    ){

        settings = data;

        player.setSettings(data);

    }

    public static synchronized void setQueue(
            List<Track> tracks
    ){

        Track current =
                player.getCurrentTrack();

        String currentPath =
                current == null
                        ?
                        null
                        :
                        current.getPath();

        queue.clear();

        if(tracks != null){

            queue.addAll(
                    tracks
            );

        }

        player.setTracks(
                queue
        );

        if(queue.isEmpty()){

            index = -1;

            player.stop();

            return;

        }

        /*
         * пытаемся сохранить текущий трек
         */

        if(currentPath != null){

            for(int i = 0; i < queue.size(); i++){

                if(queue.get(i)
                        .getPath()
                        .equals(
                                currentPath
                        )){

                    index = i;

                    return;

                }

            }

        }

        if(index < 0 ||
                index >= queue.size()){

            index = 0;

        }

    }

    public static synchronized void play(){

        if(index < 0 ||
                index >= queue.size()){

            return;

        }

        player.play(
                queue.get(index)
        );

    }

    public static synchronized void playIndex(
            int newIndex
    ){

        if(newIndex < 0 ||
                newIndex >= queue.size()){

            return;

        }

        index = newIndex;

        player.play(
                queue.get(index)
        );

    }

    public static synchronized void next(){

        advance(false);

    }

    private static synchronized void advance(
            boolean auto
    ){

        if(queue.isEmpty()){

            return;

        }

        if(settings != null &&
                settings.shuffle &&
                queue.size() > 1){

            int newIndex = index;

            while(newIndex == index){

                newIndex =
                        random.nextInt(
                                queue.size()
                        );

            }

            index = newIndex;

            play();

            return;

        }

        index++;

        if(index >= queue.size()){

            index = 0;

            boolean repeat =
                    settings != null &&
                    settings.repeatPlaylist;

            if(auto && !repeat){

                player.stop();

                return;

            }

        }

        play();

    }

    public static synchronized void previous(){

        if(queue.isEmpty()){

            return;

        }

        index--;

        if(index < 0){

            index =
                    queue.size()-1;

        }

        play();

    }

    public static synchronized void stop(){

        player.stop();

    }

    public static synchronized Track getCurrentTrack(){

        if(index < 0 ||
                index >= queue.size()){

            return null;

        }

        return queue.get(index);

    }

    public static synchronized int getIndex(){

        return index;

    }

    public static synchronized List<Track> getQueue(){

        return new ArrayList<>(
                queue
        );

    }

    public static synchronized void removeTrack(
            Track track
    ){

        if(track == null)
            return;

        int removedIndex = -1;

        for(int i = 0; i < queue.size(); i++){

            if(queue.get(i)
                    .getPath()
                    .equals(
                            track.getPath()
                    )){

                removedIndex = i;

                break;

            }

        }

        if(removedIndex == -1)
            return;

        boolean wasCurrent =
                removedIndex == index;

        queue.remove(
                removedIndex
        );

        if(queue.isEmpty()){

            index = -1;

            player.stop();

            return;

        }

        if(removedIndex < index){

            index--;

        }

        if(index >= queue.size()){

            index = 0;

        }

        player.setTracks(
                queue
        );

        if(wasCurrent){

            play();

        }

    }

}