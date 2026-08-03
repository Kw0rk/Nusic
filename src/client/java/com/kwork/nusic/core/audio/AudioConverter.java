package com.kwork.nusic.core.audio;

import net.minecraft.client.MinecraftClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

public class AudioConverter {

    private static boolean initialized;

    private static File cacheFolder;

    public static void init(){

        if(initialized)
            return;

        File gameDir =
                MinecraftClient
                        .getInstance()
                        .runDirectory;

        cacheFolder =
                new File(
                        gameDir,
                        "config/nusic/cache"
                );

        if(!cacheFolder.exists())
            cacheFolder.mkdirs();

        initialized = true;

        System.out.println(
                "[Nusic] AudioConverter ready"
        );

    }

    public static File convert(
            File input
    ){

        if(input == null || !input.exists())
            throw new RuntimeException(
                    "Audio file not found"
            );

        if(!initialized)
            init();

        if(input.getName()
                .toLowerCase()
                .endsWith(".wav")){

            return input;

        }

        File output =
                new File(
                        cacheFolder,
                        removeExtension(
                                input.getName()
                        )
                        +
                        ".wav"
                );

        if(output.exists()
                &&
                output.lastModified()
                        >
                        input.lastModified()){

            return output;

        }

        convertInternal(
                input,
                output
        );

        return output;

    }

    private static void convertInternal(
            File input,
            File output
    ){

        try{

            ProcessBuilder builder =
                    new ProcessBuilder(

                            FFmpegManager.getPath(),

                            "-y",

                            "-i",
                            input.getAbsolutePath(),

                            "-vn",

                            "-ac",
                            "2",

                            "-ar",
                            "44100",

                            "-c:a",
                            "pcm_s16le",

                            output.getAbsolutePath()

                    );

            builder.redirectErrorStream(false);

            Process process =
                    builder.start();

            Thread reader =
                    new Thread(() -> {

                try(InputStream stream =
                            process.getInputStream()){

                    byte[] buffer =
                            new byte[4096];

                    while(
                            stream.read(buffer) != -1
                    ){}

                }
                catch(Exception ignored){}

            });

            reader.setDaemon(true);

            reader.start();

            boolean done =
                    process.waitFor(
                            120,
                            TimeUnit.SECONDS
                    );

            if(!done){

                process.destroyForcibly();

                throw new RuntimeException(
                        "FFmpeg timeout"
                );

            }

            if(process.exitValue() != 0
                    ||
                    !output.exists()){

                throw new RuntimeException(
                        "FFmpeg failed"
                );

            }

            System.out.println(
                    "[Nusic] Converted: "
                            +
                    input.getName()
            );

        }
        catch(IOException |
              InterruptedException e){

            throw new RuntimeException(
                    "Cannot convert audio",
                    e
            );

        }

    }

    private static String removeExtension(
            String name
    ){

        int dot =
                name.lastIndexOf('.');

        if(dot == -1)
            return name;

        return name.substring(
                0,
                dot
        );

    }

}