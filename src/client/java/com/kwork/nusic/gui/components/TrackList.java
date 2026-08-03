package com.kwork.nusic.gui.components;

import com.kwork.nusic.core.NusicManager;
import com.kwork.nusic.core.PlaybackManager;
import com.kwork.nusic.core.Track;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public class TrackList {

    private static final int ROW_HEIGHT = 27;

    private static final int DELETE_WIDTH = 22;

    private int x;
    private int y;
    private int width;
    private int height;

    private int scrollOffset;

    private int hoverIndex = -1;

    private int deleteHover = -1;

    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            int x,
            int y,
            int width,
            int height
    ){

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        List<Track> tracks =
                NusicManager
                        .getInstance()
                        .getTracks();

        TextRenderer renderer =
                MinecraftClient
                        .getInstance()
                        .textRenderer;

        if(tracks == null ||
                tracks.isEmpty()){

            context.drawText(
                    renderer,
                    Text.literal(
                            "No music found"
                    ),
                    x+10,
                    y+10,
                    0xFFFFFFFF,
                    false
            );

            return;

        }

        clampScroll(
                tracks.size()
        );

        int start =
                scrollOffset / ROW_HEIGHT;

        int offset =
                scrollOffset % ROW_HEIGHT;

        int rowY =
                y-offset;

        for(int i=start;i<tracks.size();i++){

            if(rowY > y+height)
                break;

            if(rowY+ROW_HEIGHT >= y){

                drawTrack(
                        context,
                        renderer,
                        tracks.get(i),
                        i,
                        rowY
                );

            }

            rowY += ROW_HEIGHT;

        }

        drawScrollbar(
                context,
                tracks.size()
        );

    }

    private void drawTrack(
            DrawContext context,
            TextRenderer renderer,
            Track track,
            int index,
            int rowY
    ){

        Track current =
                PlaybackManager
                        .getCurrentTrack();

        boolean selected =
                current != null &&
                current.getPath()
                        .equals(
                                track.getPath()
                        );

        if(selected ||
                hoverIndex == index){

            context.fill(
                    x,
                    rowY,
                    x+width,
                    rowY+ROW_HEIGHT,
                    0x33222222
            );

        }

        context.drawText(
                renderer,
                Text.literal(
                        String.valueOf(index+1)
                ),
                x+8,
                rowY+9,
                0xFF8EE6B0,
                false
        );

        context.drawText(
                renderer,
                Text.literal(
                        track.getDisplayName()
                ),
                x+40,
                rowY+9,
                0xFFFFFFFF,
                false
        );

        int bx =
                x+width-DELETE_WIDTH-5;

        boolean button =
                deleteHover == index;

        context.fill(
                bx,
                rowY+4,
                bx+DELETE_WIDTH,
                rowY+23,
                button
                        ? 0xFFFF3333
                        : 0xFFAA2222
        );

        context.drawText(
                renderer,
                Text.literal("×"),
                bx+7,
                rowY+7,
                0xFFFFFFFF,
                false
        );

        context.fill(
                x,
                rowY+ROW_HEIGHT-1,
                x+width,
                rowY+ROW_HEIGHT,
                0xFF1B1E23
        );

    }

    public boolean mouseClicked(
            Click click
    ){

        if(click.button()!=0)
            return false;

        double mouseX =
                click.x();

        double mouseY =
                click.y();

        List<Track> tracks =
                NusicManager
                        .getInstance()
                        .getTracks();

        if(tracks == null ||
                tracks.isEmpty())
            return false;

        if(mouseX < x ||
                mouseX > x+width ||
                mouseY < y ||
                mouseY > y+height)
            return false;

        int index =
                (
                        (int)mouseY
                        -
                        y
                        +
                        scrollOffset
                )
                /
                ROW_HEIGHT;

        if(index < 0 ||
                index >= tracks.size())
            return false;

        if(mouseX >
                x+width-35){

            NusicManager
                    .getInstance()
                    .deleteTrack(
                            tracks.get(index)
                    );

            return true;

        }

        PlaybackManager
                .playIndex(
                        index
                );

        return true;

    }

    public void mouseMoved(
            double mouseX,
            double mouseY
    ){

        hoverIndex = -1;

        deleteHover = -1;

        if(mouseX<x ||
                mouseX>x+width ||
                mouseY<y ||
                mouseY>y+height)
            return;

        int index =
                (
                        (int)mouseY
                        -
                        y
                        +
                        scrollOffset
                )
                /
                ROW_HEIGHT;

        hoverIndex = index;

        if(mouseX >
                x+width-35){

            deleteHover = index;

        }

    }

    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double amount
    ){

        if(mouseX<x ||
                mouseX>x+width ||
                mouseY<y ||
                mouseY>y+height)
            return false;

        scrollOffset -=
                (int)(amount*28);

        List<Track> tracks =
                NusicManager
                        .getInstance()
                        .getTracks();

        clampScroll(
                tracks.size()
        );

        return true;

    }

    private void clampScroll(
            int count
    ){

        int max =
                Math.max(
                        0,
                        count*ROW_HEIGHT-height
                );

        scrollOffset =
                Math.max(
                        0,
                        Math.min(
                                scrollOffset,
                                max
                        )
                );

    }

    private void drawScrollbar(
            DrawContext context,
            int count
    ){

        int total =
                count*ROW_HEIGHT;

        if(total<=height)
            return;

        int bar =
                Math.max(
                        20,
                        height*height/total
                );

        int pos =
                y+
                scrollOffset*
                (height-bar)
                /
                (total-height);

        context.fill(
                x+width-2,
                pos,
                x+width,
                pos+bar,
                0xFF5F6770
        );

    }

    public void reload(){

        scrollOffset = 0;

        hoverIndex = -1;

        deleteHover = -1;

    }

}