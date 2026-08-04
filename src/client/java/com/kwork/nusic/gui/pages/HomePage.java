package com.kwork.nusic.gui.pages;

import com.kwork.nusic.core.NusicManager;
import com.kwork.nusic.gui.Theme;
import com.kwork.nusic.gui.components.PlayerBar;
import com.kwork.nusic.gui.components.Sidebar;
import com.kwork.nusic.gui.components.TrackList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class HomePage implements Page {

    private final TrackList trackList;

    public HomePage(){

        trackList =
                new TrackList(
                        TrackList.Mode.LIBRARY
                );

    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            int screenWidth,
            int screenHeight
    ){

        TextRenderer renderer =
                MinecraftClient
                        .getInstance()
                        .textRenderer;

        NusicManager manager =
                NusicManager.getInstance();

        int contentX =
                Sidebar.WIDTH + 16;

        context.drawText(
                renderer,
                Text.literal("Home"),
                contentX,
                14,
                Theme.TEXT,
                false
        );

        int count =
                manager.getTracks() == null
                        ? 0
                        : manager.getTracks().size();

        context.drawText(
                renderer,
                Text.literal(
                        count + " tracks in your library"
                ),
                contentX,
                28,
                Theme.TEXT_MUTED,
                false
        );

        context.fill(
                contentX,
                42,
                screenWidth - 16,
                43,
                Theme.DIVIDER
        );

        trackList.setTracks(
                manager.getTracks()
        );

        trackList.render(
                context,
                mouseX,
                mouseY,
                contentX,
                50,
                screenWidth - contentX - 16,
                screenHeight - 50 - PlayerBar.HEIGHT - 6
        );

    }

    @Override
    public boolean mouseClicked(
            Click click,
            boolean doubled
    ){

        return trackList.mouseClicked(
                click
        );

    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontal,
            double vertical
    ){

        return trackList.mouseScrolled(
                mouseX,
                mouseY,
                vertical
        );

    }

    @Override
    public void reload(){

        NusicManager
                .getInstance()
                .reload();

        trackList.reload();

    }

}
