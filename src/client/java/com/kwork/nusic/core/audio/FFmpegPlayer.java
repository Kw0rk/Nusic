package com.kwork.nusic.core.audio;


import javax.sound.sampled.*;

import java.io.*;



public class FFmpegPlayer {


    private Thread thread;

    private Process process;

    private SourceDataLine line;


    private File currentFile;


    private volatile boolean playing;

    private volatile boolean paused;


    private volatile boolean stopped;


    private long positionMs;

    private long durationMs;


    private long startPosition;


    private Runnable finishedCallback;







    public synchronized void play(
            File file,
            Runnable finished
    ){

        playFrom(
                file,
                0,
                finished
        );

    }







    public synchronized void playFrom(
            File file,
            long position,
            Runnable finished
    ){

        stop();


        currentFile = file;

        positionMs =
                Math.max(
                        0,
                        position
                );


        startPosition =
                positionMs;


        finishedCallback =
                finished;


        durationMs =
                getDuration(
                        file
                );


        start();


    }







    private void start(){


        stopped = false;

        paused = false;



        thread =
                new Thread(() -> {


                    try{


                        process =
                                new ProcessBuilder(

                                        FFmpegManager.getPath(),

                                        "-loglevel",
                                        "quiet",

                                        "-ss",
                                        String.valueOf(
                                                positionMs / 1000.0
                                        ),

                                        "-i",
                                        currentFile.getAbsolutePath(),

                                        "-vn",

                                        "-f",
                                        "s16le",

                                        "-acodec",
                                        "pcm_s16le",

                                        "-ar",
                                        "44100",

                                        "-ac",
                                        "2",

                                        "-"

                                )
                                .start();




                        InputStream input =
                                process.getInputStream();




                        AudioFormat format =
                                new AudioFormat(
                                        44100,
                                        16,
                                        2,
                                        true,
                                        false
                                );



                        line =
                                AudioSystem
                                        .getSourceDataLine(
                                                format
                                        );



                        line.open(
                                format,
                                16384
                        );


                        line.start();



                        playing = true;



                        long last =
                                System.currentTimeMillis();




                        byte[] buffer =
                                new byte[8192];



                        int read;



                        while(
                                !stopped &&
                                (read = input.read(buffer)) != -1
                        ){



                            if(stopped)
                                break;




                            int size =
                                    read -
                                    read % 4;




                            if(size > 0){


                                line.write(
                                        buffer,
                                        0,
                                        size
                                );


                            }




                            long now =
                                    System.currentTimeMillis();



                            positionMs +=
                                    now - last;



                            last = now;


                        }



                        input.close();



                    }
                    catch(Exception e){


                        if(!stopped)
                            e.printStackTrace();


                    }
                    finally{


                        cleanup();



                        if(!stopped &&
                                finishedCallback != null){


                            finishedCallback.run();


                        }


                    }



                });



        thread.setDaemon(true);

        thread.setName(
                "Nusic Audio Thread"
        );


        thread.start();


    }







    public synchronized void pause(){


        if(!playing)
            return;



        paused = true;



        startPosition =
                positionMs;



        stopEngine();



    }







    public synchronized void resume(){


        if(!paused)
            return;



        paused = false;



        start();



    }







    public synchronized void seek(
            long millis
    ){


        if(currentFile == null)
            return;



        positionMs =
                Math.max(
                        0,
                        millis
                );



        boolean wasPlaying =
                playing;



        stopEngine();



        if(wasPlaying)
            start();



    }







    private void stopEngine(){


        stopped = true;

        playing = false;



        if(line != null){


            try{


                line.stop();

                line.flush();

                line.close();


            }
            catch(Exception ignored){}



            line = null;


        }




        if(process != null){


            process.destroyForcibly();

            process = null;


        }




        if(thread != null){


            thread.interrupt();

            thread = null;


        }


    }







    public synchronized void stop(){


        paused = false;


        stopEngine();


        positionMs = 0;


    }







    private void cleanup(){


        if(line != null){


            try{


                line.stop();

                line.close();


            }
            catch(Exception ignored){}


        }



        if(process != null){


            process.destroy();


        }



        playing = false;


    }







    private long getDuration(
            File file
    ){


        try{


            Process p =
                    new ProcessBuilder(

                            FFmpegManager.getPath(),

                            "-i",

                            file.getAbsolutePath()

                    )
                    .redirectErrorStream(true)
                    .start();



            String text =
                    new String(
                            p.getInputStream()
                                    .readAllBytes()
                    );



            p.destroy();



            int index =
                    text.indexOf(
                            "Duration:"
                    );



            if(index < 0)
                return 0;



            String[] t =
                    text.substring(
                            index + 9,
                            index + 20
                    )
                    .trim()
                    .split(":");



            return
                    Long.parseLong(t[0]) * 3600000L
                    +
                    Long.parseLong(t[1]) * 60000L
                    +
                    (long)
                    (
                            Double.parseDouble(t[2])
                            * 1000
                    );


        }
        catch(Exception e){

            return 0;

        }


    }







    public boolean isPlaying(){

        return playing && !paused;

    }





    public boolean isPaused(){

        return paused;

    }





    public long getPosition(){

        return positionMs;

    }





    public long getDuration(){

        return durationMs;

    }


}