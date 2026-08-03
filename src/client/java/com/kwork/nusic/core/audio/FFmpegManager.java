package com.kwork.nusic.core.audio;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.InputStream;
import java.nio.file.Files;

public class FFmpegManager {

    private static File ffmpegFile;

    public static void init(){

        File config =
                new File(
                        MinecraftClient
                                .getInstance()
                                .runDirectory,
                        "config/nusic/bin"
                );

        if(!config.exists()){

            config.mkdirs();

        }

        String name =
                System
                        .getProperty(
                                "os.name"
                        )
                        .toLowerCase()
                        .contains("win")
                        ?
                        "ffmpeg.exe"
                        :
                        "ffmpeg";

        ffmpegFile =
                new File(
                        config,
                        name
                );

        if(ffmpegFile.exists()){

            System.out.println(
                    "[Nusic] FFmpeg found: "
                            +
                    ffmpegFile
            );

            return;

        }

        /*
         * Пробуем достать встроенный ffmpeg
         */

        extractBundledFFmpeg();

        if(ffmpegFile.exists()){

            System.out.println(
                    "[Nusic] FFmpeg extracted"
            );

        }
        else {

            System.out.println(
                    "[Nusic] FFmpeg not bundled, using PATH"
            );

        }

    }

    private static void extractBundledFFmpeg(){

        try{

            String resource =
                    "/nusic/ffmpeg/"
                    +
                    ffmpegFile.getName();

            InputStream stream =
                    FFmpegManager.class
                            .getResourceAsStream(
                                    resource
                            );

            if(stream == null){

                return;

            }

            Files.copy(
                    stream,
                    ffmpegFile.toPath()
            );

            stream.close();

            ffmpegFile.setExecutable(
                    true
            );

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public static String getPath(){

        if(ffmpegFile == null){

            init();

        }

        if(ffmpegFile.exists()){

            return ffmpegFile
                    .getAbsolutePath();

        }

        return "ffmpeg";

    }

    public static boolean isInstalled(){

        return ffmpegFile != null &&
                ffmpegFile.exists();

    }

    public static File getFile(){

        return ffmpegFile;

    }

}