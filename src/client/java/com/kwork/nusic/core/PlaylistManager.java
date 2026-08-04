package com.kwork.nusic.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import com.kwork.nusic.data.Playlist;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class PlaylistManager {

    private static final Gson GSON =
            new GsonBuilder().setPrettyPrinting().create();

    private static final Type LIST_TYPE =
            new TypeToken<List<Playlist>>() {}.getType();

    private static List<Playlist> playlists;

    private static File file() {
        return new File(
                NusicManager.getInstance().getConfigFolder(),
                "playlists.json"
        );
    }

    public static synchronized List<Playlist> getPlaylists() {
        if (playlists == null) {
            load();
        }
        return playlists;
    }

    public static synchronized Playlist create(String name) {
        if (name == null || name.isBlank()) {
            return null;
        }

        String unique = name.trim();
        int counter = 2;

        while (byName(unique) != null) {
            unique = name.trim() + " " + counter;
            counter++;
        }

        Playlist playlist = new Playlist(unique);

        getPlaylists().add(playlist);
        save();

        return playlist;
    }

    public static synchronized void delete(Playlist playlist) {
        if (playlist == null) {
            return;
        }
        getPlaylists().remove(playlist);
        save();
    }

    public static synchronized void rename(Playlist playlist, String name) {
        if (playlist == null || name == null || name.isBlank()) {
            return;
        }
        playlist.setName(name.trim());
        save();
    }

    public static synchronized void addTrack(Playlist playlist, Track track) {
        if (playlist == null || track == null) {
            return;
        }
        playlist.add(track.getPath());
        save();
    }

    public static synchronized void removeTrack(Playlist playlist, Track track) {
        if (playlist == null || track == null) {
            return;
        }
        playlist.remove(track.getPath());
        save();
    }

    public static synchronized Playlist byName(String name) {
        for (Playlist playlist : getPlaylists()) {
            if (playlist.getName().equalsIgnoreCase(name)) {
                return playlist;
            }
        }
        return null;
    }

    /**
     * Resolves the playlist's stored paths against the scanned library,
     * skipping tracks whose files no longer exist.
     */
    public static synchronized List<Track> getTracks(Playlist playlist) {
        List<Track> result = new ArrayList<>();

        if (playlist == null) {
            return result;
        }

        List<Track> library =
                NusicManager.getInstance().getTracks();

        for (String path : playlist.getTrackPaths()) {
            Track found = null;

            if (library != null) {
                for (Track track : library) {
                    if (track.getPath().equals(path)) {
                        found = track;
                        break;
                    }
                }
            }

            if (found != null) {
                result.add(found);
            } else {
                File file = new File(path);
                if (file.exists()) {
                    result.add(new Track(file.getName(), path));
                }
            }
        }

        return result;
    }

    private static synchronized void load() {
        playlists = new ArrayList<>();

        File file = file();

        if (!file.exists()) {
            return;
        }

        try (FileReader reader = new FileReader(file)) {
            List<Playlist> loaded = GSON.fromJson(reader, LIST_TYPE);
            if (loaded != null) {
                for (Playlist playlist : loaded) {
                    if (playlist != null && playlist.getName() != null) {
                        playlists.add(playlist);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static synchronized void save() {
        try (FileWriter writer = new FileWriter(file())) {
            GSON.toJson(getPlaylists(), LIST_TYPE, writer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
