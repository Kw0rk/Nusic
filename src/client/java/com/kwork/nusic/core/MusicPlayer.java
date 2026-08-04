package com.kwork.nusic.core;


import com.kwork.nusic.core.audio.FFmpegPlayer;
import com.kwork.nusic.data.SettingsData;

import java.io.File;
import java.util.List;



public class MusicPlayer {


    private final FFmpegPlayer engine =
            new FFmpegPlayer();



    private Track currentTrack;


    private List<Track> tracks;


    private Runnable onFinished;


    private SettingsData settings;


    private long pausedPosition = 0;







    public synchronized void play(
            Track track
    ){


        if(track == null)
            return;



        File file =
                track.getFile();



        if(file == null ||
                !file.exists()){


            System.out.println(
                    "[Nusic] Missing file: "
                    + track.getName()
            );


            return;


        }



        engine.stop();



        currentTrack =
                track;


        pausedPosition = 0;



        System.out.println(
                "[Nusic] Playing: "
                + track.getName()
        );



        engine.play(
                file,
                () -> {


                    if(onFinished != null)
                        onFinished.run();


                }
        );


    }







    public synchronized void stop(){


        engine.stop();

        pausedPosition = 0;


    }







    public synchronized void pause(){


        pausedPosition =
                engine.getPosition();



        engine.pause();



    }







    public synchronized void resume(){


        engine.seek(
                pausedPosition
        );



        engine.resume();



    }







    public synchronized void togglePause(){



        if(engine.isPaused()){


            resume();


        }
        else if(engine.isPlaying()){


            pause();


        }


    }







    public synchronized void seek(
            long millis
    ){


        pausedPosition =
                millis;


        engine.seek(
                millis
        );


    }







    public boolean isPlaying(){


        return engine.isPlaying();


    }







    public boolean isPaused(){


        return engine.isPaused();


    }







    public Track getCurrentTrack(){


        return currentTrack;


    }







    public long getPosition(){


        if(engine.isPaused()){


            return pausedPosition;


        }


        return engine.getPosition();


    }







    public long getDuration(){


        return engine.getDuration();


    }







    public void setTracks(
            List<Track> tracks
    ){


        this.tracks =
                tracks;


    }







    public List<Track> getTracks(){


        return tracks;


    }







    public void playTrack(
            int index
    ){


        if(tracks == null)
            return;



        if(index < 0 ||
                index >= tracks.size())
            return;



        play(
                tracks.get(index)
        );


    }







    public void next(){


        PlaybackManager.next();


    }







    public void previous(){


        PlaybackManager.previous();


    }







    public void setOnFinished(
            Runnable runnable
    ){


        onFinished =
                runnable;


    }







    public void setSettings(
            SettingsData settings
    ){


        this.settings =
                settings;


        if(settings != null){


            engine.setVolume(
                    settings.volume
            );


        }


    }







    public void setVolume(
            float volume
    ){


        engine.setVolume(
                volume
        );


        if(settings != null){


            settings.volume =
                    volume;


        }


    }







    public float getVolume(){


        return engine.getVolume();


    }


}