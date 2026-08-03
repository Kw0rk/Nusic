package com.kwork.nusic;

import com.kwork.nusic.core.NusicManager;
import com.kwork.nusic.core.PlaybackManager;
import com.kwork.nusic.core.audio.AudioConverter;
import com.kwork.nusic.core.audio.FFmpegManager;
import com.kwork.nusic.gui.NusicScreen;
import com.kwork.nusic.input.NusicDropHandler;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;

public class NusicMod implements ClientModInitializer {

    public static NusicManager NUSIC;

    private boolean wasInWorld;
    private boolean libraryLoaded;

    @Override
    public void onInitializeClient() {

        FFmpegManager.init();
        AudioConverter.init();

        NUSIC = NusicManager.getInstance();

        NusicDropHandler.register();
        KeybindManager.register();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            checkWorld(client);
            checkKey(client);
        });

        System.out.println("[Nusic] Loaded successfully");
    }

    private void checkWorld(MinecraftClient client) {

        boolean inWorld = client.world != null;

        if (inWorld && !libraryLoaded) {
            NUSIC.reload();
            libraryLoaded = true;
        }

        if (wasInWorld && !inWorld) {
            PlaybackManager.stop();
            libraryLoaded = false;
        }

        wasInWorld = inWorld;
    }

    private void checkKey(MinecraftClient client) {

        if (KeybindManager.pressed()
                && client.currentScreen == null) {

            client.setScreen(new NusicScreen());

        }

    }

}