package com.kwork.nusic.gui.pages;

import com.kwork.nusic.core.NusicManager;
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
                new TrackList();

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

        MinecraftClient client =
                MinecraftClient.getInstance();

        TextRenderer renderer =
                client.textRenderer;

        NusicManager manager =
                NusicManager.getInstance();

        int contentX =
                Sidebar.WIDTH + 20;

        context.drawText(
                renderer,
                Text.literal(
                        "Your music"
                ),
                contentX,
                18,
                0xFFFFFFFF,
                false
        );

        int count = 0;

        if(manager.getTracks()!=null){

            count =
                    manager.getTracks()
                            .size();

        }

        context.drawText(
                renderer,
                Text.literal(
                        count +
                        " tracks found"
                ),
                contentX,
                33,
                0xFF747B84,
                false
        );

        context.fill(
                contentX,
                49,
                screenWidth-16,
                50,
                0xFF272B32
        );

        trackList.render(
                context,
                mouseX,
                mouseY,
                contentX,
                58,
                screenWidth-contentX-16,
                screenHeight-112
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
    public void mouseMoved(
            double mouseX,
            double mouseY
    ){

        trackList.mouseMoved(
                mouseX,
                mouseY
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