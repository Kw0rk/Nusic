package com.kwork.nusic.config;

import java.io.File;

public class ConfigManager {

    private static NusicConfig config;

    public static void load(
            File dir
    ){

        config =
                new NusicConfig();

        System.out.println(
                "[Nusic] Config loaded"
        );

    }

    public static NusicConfig get(){

        if(config == null){

            config =
                    new NusicConfig();

        }

        return config;

    }

}