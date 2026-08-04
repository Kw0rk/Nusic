package com.kwork.nusic.gui.pages;

import com.kwork.nusic.core.PlaybackManager;
import com.kwork.nusic.core.PlaylistManager;
import com.kwork.nusic.core.Track;
import com.kwork.nusic.data.Playlist;
import com.kwork.nusic.gui.Theme;
import com.kwork.nusic.gui.components.PlayerBar;
import com.kwork.nusic.gui.components.Sidebar;
import com.kwork.nusic.gui.components.TrackList;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public class PlaylistPage implements Page {

    private final Playlist playlist;

    private final Runnable onDeleted;

    private final TrackList trackList;

    private int playX;
    private int playY;
    private int playWidth;

    private int deleteX;
    private int deleteY;
    private int deleteWidth;

    public PlaylistPage(
            Playlist playlist,
            Runnable onDeleted
    ){

        this.playlist = playlist;

        this.onDeleted = onDeleted;

        trackList =
                new TrackList(
                        TrackList.Mode.PLAYLIST
                );

        trackList.setPlaylist(
                playlist
        );

    }

    public Playlist getPlaylist(){

        return playlist;

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

        int contentX =
                Sidebar.WIDTH + 16;

        List<Track> tracks =
                PlaylistManager.getTracks(
                        playlist
                );

        context.drawText(
                renderer,
                Text.literal("PLAYLIST"),
                contentX,
                10,
                Theme.TEXT_DIM,
                false
        );

        context.drawText(
                renderer,
                Text.literal(
                        playlist.getName()
                ),
                contentX,
                22,
                Theme.TEXT,
                false
        );

        context.drawText(
                renderer,
                Text.literal(
                        tracks.size() + " tracks"
                ),
                contentX,
                36,
                Theme.TEXT_MUTED,
                false
        );

        playX = contentX;
        playY = 50;

        playWidth =
                renderer.getWidth("> Play") + 16;

        boolean playHover =
                inside(
                        mouseX, mouseY,
                        playX, playY,
                        playWidth, 16
                );

        context.fill(
                playX,
                playY,
                playX + playWidth,
                playY + 16,
                playHover
                        ? Theme.ACCENT
                        : Theme.ACCENT_DARK
        );

        context.drawText(
                renderer,
                Text.literal("> Play"),
                playX + 8,
                playY + 4,
                0xFF000000,
                false
        );

        deleteWidth =
                renderer.getWidth("Delete") + 16;

        deleteX =
                screenWidth - 16 - deleteWidth;

        deleteY = 50;

        boolean deleteHover =
                inside(
                        mouseX, mouseY,
                        deleteX, deleteY,
                        deleteWidth, 16
                );

        context.fill(
                deleteX,
                deleteY,
                deleteX + deleteWidth,
                deleteY + 16,
                deleteHover
                        ? Theme.DANGER
                        : 0xFF2A2A2A
        );

        context.drawText(
                renderer,
                Text.literal("Delete"),
                deleteX + 8,
                deleteY + 4,
                deleteHover
                        ? 0xFF000000
                        : Theme.TEXT_MUTED,
                false
        );

        context.fill(
                contentX,
                72,
                screenWidth - 16,
                73,
                Theme.DIVIDER
        );

        trackList.setTracks(
                tracks
        );

        trackList.render(
                context,
                mouseX,
                mouseY,
                contentX,
                80,
                screenWidth - contentX - 16,
                screenHeight - 80 - PlayerBar.HEIGHT - 6
        );

    }

    @Override
    public boolean mouseClicked(
            Click click,
            boolean doubled
    ){

        int mouseX =
                (int) click.x();

        int mouseY =
                (int) click.y();

        if(click.button() == 0 &&
                inside(
                        mouseX, mouseY,
                        playX, playY,
                        playWidth, 16
                )){

            List<Track> tracks =
                    PlaylistManager.getTracks(
                            playlist
                    );

            if(!tracks.isEmpty()){

                PlaybackManager.setQueue(
                        tracks
                );

                PlaybackManager.playIndex(
                        0
                );

            }

            return true;

        }

        if(click.button() == 0 &&
                inside(
                        mouseX, mouseY,
                        deleteX, deleteY,
                        deleteWidth, 16
                )){

            PlaylistManager.delete(
                    playlist
            );

            if(onDeleted != null){

                onDeleted.run();

            }

            return true;

        }

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

        trackList.reload();

    }

    private boolean inside(
            int mx, int my,
            int x, int y,
            int w, int h
    ){

        return mx >= x &&
                mx < x + w &&
                my >= y &&
                my < y + h;

    }

}
