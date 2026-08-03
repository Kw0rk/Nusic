package com.kwork.nusic.gui.components;

import com.kwork.nusic.core.MusicPlayer;
import com.kwork.nusic.core.PlaybackManager;
import com.kwork.nusic.core.Track;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PlayerBar {

    private static final int HEIGHT = 75;

    private boolean hoverPlay;
    private boolean hoverNext;
    private boolean hoverPrev;

    public void render(
            DrawContext context,
            int width,
            int height
    ){

        TextRenderer renderer =
                MinecraftClient
                        .getInstance()
                        .textRenderer;

        MusicPlayer player =
                PlaybackManager
                        .getPlayer();

        int y =
                height - HEIGHT;

        context.fill(
                12,
                y,
                width - 12,
                height,
                0xFF111318
        );

        context.fill(
                12,
                y,
                width - 12,
                y + 1,
                0xFF30343B
        );

        Track track =
                player.getCurrentTrack();

        String title =
                "No track selected";

        if(track != null){

            title =
                    track.getName();

        }

        context.drawText(
                renderer,
                Text.literal(title),
                110,
                y + 12,
                0xFFFFFFFF,
                false
        );

        drawButton(
                context,
                renderer,
                "<<",
                25,
                y + 34,
                hoverPrev
        );

        String icon =
                player.isPlaying()
                        ?
                        "||"
                        :
                        ">";

        drawButton(
                context,
                renderer,
                icon,
                60,
                y + 34,
                hoverPlay
        );

        drawButton(
                context,
                renderer,
                ">>",
                95,
                y + 34,
                hoverNext
        );

        int start =
                140;

        int end =
                width - 30;

        context.fill(
                start,
                y + 45,
                end,
                y + 47,
                0xFF343840
        );

        long duration =
                player.getDuration();

        long position =
                player.getPosition();

        float progress = 0;

        if(duration > 0){

            progress =
                    Math.min(
                            1f,
                            (float) position /
                            (float) duration
                    );

        }

        context.fill(
                start,
                y + 45,
                start +
                        (int)
                        (
                                (end-start)
                                *
                                progress
                        ),
                y + 47,
                0xFF8EE6B0
        );

        context.drawText(
                renderer,
                Text.literal(
                        formatTime(position)
                        +
                        " / "
                        +
                        formatTime(duration)
                ),
                start,
                y + 60,
                0xFFAAAAAA,
                false
        );

    }

    public boolean mouseClicked(
            Click click,
            boolean doubled
    ){

        if(click.button() != 0){

            return false;

        }

        int x =
                (int) click.x();

        int y =
                (int) click.y();

        int screenHeight =
                MinecraftClient
                        .getInstance()
                        .getWindow()
                        .getScaledHeight();

        if(y < screenHeight - HEIGHT){

            return false;

        }

        MusicPlayer player =
                PlaybackManager
                        .getPlayer();

        if(x >= 25 && x <= 53){

            PlaybackManager.previous();

            return true;

        }

        if(x >= 60 && x <= 88){

            player.togglePause();

            return true;

        }

        if(x >= 95 && x <= 123){

            PlaybackManager.next();

            return true;

        }

        return false;

    }

    public void mouseMoved(
            double mouseX,
            double mouseY,
            int height
    ){

        int y =
                height - HEIGHT + 34;

        hoverPrev =
                inside(
                        mouseX,
                        mouseY,
                        25,
                        y
                );

        hoverPlay =
                inside(
                        mouseX,
                        mouseY,
                        60,
                        y
                );

        hoverNext =
                inside(
                        mouseX,
                        mouseY,
                        95,
                        y
                );

    }

    private boolean inside(
            double mx,
            double my,
            int x,
            int y
    ){

        return mx >= x &&
                mx <= x + 28 &&
                my >= y &&
                my <= y + 24;

    }

    private void drawButton(
            DrawContext context,
            TextRenderer renderer,
            String text,
            int x,
            int y,
            boolean hover
    ){

        context.fill(
                x,
                y,
                x + 28,
                y + 24,
                hover
                        ?
                        0xFF8EE6B0
                        :
                        0xFF22262D
        );

        context.drawText(
                renderer,
                Text.literal(text),
                x + 10,
                y + 7,
                hover
                        ?
                        0xFF000000
                        :
                        0xFFFFFFFF,
                false
        );

    }

    private String formatTime(
            long millis
    ){

        long seconds =
                millis / 1000;

        long minutes =
                seconds / 60;

        seconds =
                seconds % 60;

        return String.format(
                "%02d:%02d",
                minutes,
                seconds
        );

    }

}