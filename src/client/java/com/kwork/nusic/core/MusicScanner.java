package com.kwork.nusic.core;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MusicScanner {

    private final File musicFolder;

    public MusicScanner(){

        this(
                new File(
                        MinecraftClient
                                .getInstance()
                                .runDirectory,
                        "config/nusic/music"
                )
        );

    }

    public MusicScanner(
            File folder
    ){

        this.musicFolder =
                folder;

    }

    public List<Track> scan(){

        List<Track> result =
                new ArrayList<>();

        if(!musicFolder.exists()){

            musicFolder.mkdirs();

        }

        scanFolder(
                musicFolder,
                result
        );

        result.sort(
                Comparator.comparing(
                        Track::getName,
                        String.CASE_INSENSITIVE_ORDER
                )
        );

        System.out.println(
                "[Nusic] Tracks found: "
                +
                result.size()
        );

        return result;

    }

    private void scanFolder(
            File folder,
            List<Track> result
    ){

        File[] files =
                folder.listFiles();

        if(files == null){

            return;

        }

        for(File file : files){

            if(file.isDirectory()){

                /*
                 * Поддержка:
                 *
                 * music/
                 *    Rock/
                 *       song.mp3
                 *
                 */

                scanFolder(
                        file,
                        result
                );

                continue;

            }

            if(!isMusicFile(file)){

                continue;

            }

            try{

                Track track =
                        new Track(
                                file.getName(),
                                file.getAbsolutePath()
                        );

                result.add(
                        track
                );

                System.out.println(
                        "[Nusic] Found: "
                        +
                        file.getName()
                );

            }
            catch(Exception e){

                System.err.println(
                        "[Nusic] Broken file skipped: "
                        +
                        file.getName()
                );

            }

        }

    }

    private boolean isMusicFile(
            File file
    ){

        String name =
                file.getName()
                        .toLowerCase();

        return name.endsWith(".mp3")
                ||
                name.endsWith(".wav")
                ||
                name.endsWith(".ogg")
                ||
                name.endsWith(".flac")
                ||
                name.endsWith(".m4a");

    }

    public File getMusicFolder(){

        return musicFolder;

    }

}