package com.kwork.nusic.storage;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.kwork.nusic.data.SettingsData;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class SettingsStorage {

    private final File file;

    private final Gson gson =
            new GsonBuilder()
                    .setPrettyPrinting()
                    .create();

    public SettingsStorage(
            File configFolder
    ){

        if(!configFolder.exists()){

            configFolder.mkdirs();

        }

        file =
                new File(
                        configFolder,
                        "settings.json"
                );

    }

    public SettingsData load(){

        try {

            if(!file.exists()){

                SettingsData data =
                        new SettingsData();

                save(
                        data
                );

                return data;

            }

            FileReader reader =
                    new FileReader(
                            file
                    );

            SettingsData data =
                    gson.fromJson(
                            reader,
                            SettingsData.class
                    );

            reader.close();

            if(data == null){

                return new SettingsData();

            }

            return data;

        }
        catch(Exception e){

            e.printStackTrace();

            return new SettingsData();

        }

    }

    public void save(
            SettingsData data
    ){

        try {

            FileWriter writer =
                    new FileWriter(
                            file
                    );

            gson.toJson(
                    data,
                    writer
            );

            writer.flush();

            writer.close();

        }
        catch(Exception e){

            e.printStackTrace();

        }

    }

    public File getFile(){

        return file;

    }

}