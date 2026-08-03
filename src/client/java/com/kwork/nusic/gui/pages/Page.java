package com.kwork.nusic.gui.pages;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;

public interface Page {

    void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta,
            int screenWidth,
            int screenHeight
    );

    default boolean mouseClicked(
            Click click,
            boolean doubled
    ){

        return false;

    }

    default boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontal,
            double vertical
    ){

        return false;

    }

    default void mouseMoved(
            double mouseX,
            double mouseY
    ){

    }

    default void tick(){

    }

    default void reload(){

    }

}