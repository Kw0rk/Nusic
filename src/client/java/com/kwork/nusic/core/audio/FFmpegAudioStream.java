package com.kwork.nusic.core.audio;

import net.minecraft.client.sound.AudioStream;

import javax.sound.sampled.AudioFormat;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class FFmpegAudioStream implements AudioStream {

    private final Process process;

    private final InputStream stream;

    private final AudioFormat format;

    private volatile boolean closed;

    public FFmpegAudioStream(
            Process process
    ){

        if(process == null){

            throw new IllegalArgumentException(
                    "FFmpeg process cannot be null"
            );

        }

        this.process = process;

        this.stream =
                process.getInputStream();

        this.format =
                new AudioFormat(
                        44100,
                        16,
                        2,
                        true,
                        false
                );

    }

    @Override
    public AudioFormat getFormat(){

        return format;

    }

    @Override
    public synchronized ByteBuffer read(
            int size
    )
            throws IOException {

        if(closed){

            return ByteBuffer
                    .allocate(0);

        }

        byte[] buffer =
                stream.readNBytes(
                        size
                );

        if(buffer.length == 0){

            return ByteBuffer
                    .allocate(0);

        }

        return ByteBuffer
                .wrap(
                        buffer
                );

    }

    @Override
    public synchronized void close()
            throws IOException {

        if(closed){

            return;

        }

        closed = true;

        try{

            stream.close();

        }
        finally {

            process.destroy();

        }

    }

}