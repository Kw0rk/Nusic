package com.kwork.nusic.core;

import com.kwork.nusic.data.SettingsData;
import com.kwork.nusic.storage.SettingsStorage;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class NusicManager {

    private static NusicManager instance;

    private final SettingsData settings;

    private final SettingsStorage storage;

    private final MusicScanner scanner;

    private final File configFolder;

    private final File musicFolder;

    private final File cacheFolder;

    private final File ffmpegFolder;

    private List<Track> tracks =
            new ArrayList<>();

    private NusicManager(){

        File gameDir =
                MinecraftClient
                        .getInstance()
                        .runDirectory;

        configFolder =
                new File(
                        gameDir,
                        "config/nusic"
                );

        configFolder.mkdirs();

        musicFolder =
                new File(
                        configFolder,
                        "music"
                );

        musicFolder.mkdirs();

        cacheFolder =
                new File(
                        configFolder,
                        "cache"
                );

        cacheFolder.mkdirs();

        ffmpegFolder =
                new File(
                        configFolder,
                        "ffmpeg"
                );

        ffmpegFolder.mkdirs();

        extractFFmpeg();

        storage =
                new SettingsStorage(
                        configFolder
                );

        settings =
                storage.load();

        scanner =
                new MusicScanner(
                        musicFolder
                );

        reload();

        PlaybackManager
                .getPlayer()
                .setSettings(
                        settings
                );

        System.out.println(
                "[Nusic] Manager loaded"
        );

    }

    public static synchronized NusicManager getInstance(){

        if(instance == null){

            instance =
                    new NusicManager();

        }

        return instance;

    }

    private void extractFFmpeg(){

        File ffmpeg =
                new File(
                        ffmpegFolder,
                        "ffmpeg.exe"
                );

        if(ffmpeg.exists()){

            System.out.println(
                    "[Nusic] FFmpeg found"
            );

            return;

        }

        try(
                InputStream input =
                        NusicManager.class
                                .getResourceAsStream(
                                        "/nusic/ffmpeg/ffmpeg.exe"
                                )
        ){

            if(input == null){

                System.out.println(
                        "[Nusic] FFmpeg missing"
                );

                return;

            }

            Files.copy(
                    input,
                    ffmpeg.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public synchronized void reload(){

        try{

            tracks =
                    scanner.scan();

            if(tracks == null){

                tracks =
                        new ArrayList<>();

            }

            PlaybackManager
                    .setQueue(
                            tracks
                    );

            System.out.println(
                    "[Nusic] Tracks: "
                            +
                    tracks.size()
            );

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public synchronized void addFile(
            String path
    ){

        File source =
                new File(path);

        if(!source.exists())
            return;

        File target =
                new File(
                        musicFolder,
                        source.getName()
                );

        try{

            Files.copy(
                    source.toPath(),
                    target.toPath(),
                    StandardCopyOption.REPLACE_EXISTING
            );

            reload();

            System.out.println(
                    "[Nusic] Added: "
                            +
                    target.getName()
            );

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public synchronized void deleteTrack(
            Track track
    ){

        if(track == null)
            return;

        try{

            Track current =
                    PlaybackManager
                            .getPlayer()
                            .getCurrentTrack();

            if(current != null &&
                    current.getPath()
                            .equals(
                                    track.getPath()
                            )){

                PlaybackManager.stop();

            }

            File file =
                    track.getFile();

            if(file.exists()){

                Files.delete(
                        file.toPath()
                );

            }

            File cache =
                    new File(
                            cacheFolder,
                            removeExtension(
                                    file.getName()
                            )
                            +
                            ".wav"
                    );

            if(cache.exists()){

                Files.delete(
                        cache.toPath()
                );

            }

            reload();

            System.out.println(
                    "[Nusic] Deleted: "
                            +
                    track.getName()
            );

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public synchronized void scanFile(
            String path
    ){

        reload();

    }

    private String removeExtension(
            String name
    ){

        int dot =
                name.lastIndexOf('.');

        if(dot <= 0)
            return name;

        return name.substring(
                0,
                dot
        );

    }

    public List<Track> getTracks(){

        return tracks;

    }

    public MusicPlayer getPlayer(){

        return PlaybackManager.getPlayer();

    }

    public SettingsData getSettings(){

        return settings;

    }

    public void saveSettings(){

        storage.save(settings);

    }

    public File getMusicFolder(){

        return musicFolder;

    }

    public File getCacheFolder(){

        return cacheFolder;

    }

    public File getFfmpegFolder(){

        return ffmpegFolder;

    }

    public File getConfigFolder(){

        return configFolder;

    }

}