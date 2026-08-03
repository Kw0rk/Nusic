package com.kwork.nusic.core.audio;

import java.io.File;

public class AudioDuration {

    public static long getDuration(
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

            String out =
                    new String(
                            p.getInputStream()
                                    .readAllBytes()
                    );

            p.waitFor();

            int i =
                    out.indexOf(
                            "Duration:"
                    );

            if(i < 0)
                return 0;

            String t =
                    out.substring(
                            i + 9,
                            i + 20
                    )
                    .trim();

            String[] a =
                    t.split(":");

            return
                    Long.parseLong(a[0]) * 3600000L
                    +
                    Long.parseLong(a[1]) * 60000L
                    +
                    (long)
                    (
                            Double.parseDouble(a[2])
                                    *
                            1000
                    );

        }
        catch(Exception e){

            return 0;

        }

    }

}