package com.kwork.nusic.core.importer;

import com.kwork.nusic.core.NusicManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;

public class MusicImportManager {

    private static final String[] SUPPORTED = {

            "mp3",
            "wav",
            "ogg",
            "flac",
            "m4a"

    };

    private MusicImportManager(){

    }

    public static void importFile(
            String path
    )
            throws IOException {

        File source =
                new File(path);

        if(!source.exists()){

            return;

        }

        if(source.isDirectory()){

            importFolder(
                    source
            );

            return;

        }

        if(!isSupported(source)){

            return;

        }

        File targetFolder =
                NusicManager
                        .getInstance()
                        .getMusicFolder();

        if(!targetFolder.exists()){

            targetFolder.mkdirs();

        }

        File target =
                getUniqueFile(
                        targetFolder,
                        source
                );

        Files.copy(
                source.toPath(),
                target.toPath(),
                StandardCopyOption.REPLACE_EXISTING
        );

        System.out.println(
                "[Nusic] Imported: "
                +
                target.getName()
        );

    }

    public static void importFolder(
            File folder
    )
            throws IOException {

        File[] files =
                folder.listFiles();

        if(files == null){

            return;

        }

        for(File file : files){

            if(file.isDirectory()){

                importFolder(
                        file
                );

            }
            else {

                importFile(
                        file.getAbsolutePath()
                );

            }

        }

    }

    private static File getUniqueFile(
            File folder,
            File original
    ){

        File result =
                new File(
                        folder,
                        original.getName()
                );

        if(!result.exists()){

            return result;

        }

        String name =
                original.getName();

        String extension = "";

        int dot =
                name.lastIndexOf('.');

        if(dot > 0){

            extension =
                    name.substring(
                            dot
                    );

            name =
                    name.substring(
                            0,
                            dot
                    );

        }

        int index = 1;

        while(true){

            File test =
                    new File(
                            folder,
                            name
                            +
                            "_"
                            +
                            index
                            +
                            extension
                    );

            if(!test.exists()){

                return test;

            }

            index++;

        }

    }

    private static boolean isSupported(
            File file
    ){

        String name =
                file.getName()
                        .toLowerCase(
                                Locale.ROOT
                        );

        for(String ext : SUPPORTED){

            if(name.endsWith(
                    "."
                    +
                    ext
            )){

                return true;

            }

        }

        return false;

    }

}