package com.kwork.nusic.core;

public class AudioManager {

    private AudioManager(){

    }

    private static NusicService service(){

        return NusicService
                .getInstance();

    }

    public static void play(
            Track track
    ){

        service()
                .play(
                        track
                );

    }

    public static void pause(){

        service()
                .pause();

    }

    public static void resume(){

        service()
                .resume();

    }

    public static void togglePause(){

        service()
                .togglePause();

    }

    public static void stop(){

        service()
                .stop();

    }

    public static void next(){

        service()
                .next();

    }

    public static void previous(){

        service()
                .previous();

    }

    public static boolean isPlaying(){

        return service()
                .isPlaying();

    }

    public static boolean isPaused(){

        return service()
                .isPaused();

    }

    public static Track getCurrentTrack(){

        return service()
                .getCurrentTrack();

    }

    public static long getPosition(){

        return service()
                .getPosition();

    }

    public static long getDuration(){

        return service()
                .getDuration();

    }

}