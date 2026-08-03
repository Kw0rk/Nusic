package com.kwork.nusic.gui;

import com.kwork.nusic.gui.components.PlayerBar;
import com.kwork.nusic.gui.components.Sidebar;
import com.kwork.nusic.gui.pages.HomePage;
import com.kwork.nusic.gui.pages.Page;

import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class NusicScreen extends Screen {

    private final Sidebar sidebar;

    private final PlayerBar playerBar;

    private final Page page;

    public NusicScreen(){

        super(
                Text.literal(
                        "Nusic"
                )
        );

        sidebar =
                new Sidebar();

        playerBar =
                new PlayerBar();

        page =
                new HomePage();

    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ){

        super.render(
                context,
                mouseX,
                mouseY,
                delta
        );

        context.fill(
                0,
                0,
                width,
                height,
                0xFF0D0F12
        );

        if(sidebar != null){

            sidebar.render(
                    context,
                    height,
                    mouseY
            );

        }

        if(page != null){

            page.render(
                    context,
                    mouseX,
                    mouseY,
                    delta,
                    width,
                    height
            );

        }

        if(playerBar != null){

            playerBar.render(
                    context,
                    width,
                    height
            );

        }

    }

    @Override
    public boolean mouseClicked(
            Click click,
            boolean doubled
    ){

        if(playerBar != null &&
                playerBar.mouseClicked(
                        click,
                        doubled
                )){

            return true;

        }

        if(page != null &&
                page.mouseClicked(
                        click,
                        doubled
                )){

            return true;

        }

        return super.mouseClicked(
                click,
                doubled
        );

    }

    @Override
    public void mouseMoved(
            double mouseX,
            double mouseY
    ){

        if(playerBar != null){

            playerBar.mouseMoved(
                    mouseX,
                    mouseY,
                    height
            );

        }

        super.mouseMoved(
                mouseX,
                mouseY
        );

    }

    @Override
    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double horizontal,
            double vertical
    ){

        if(page != null &&
                page.mouseScrolled(
                        mouseX,
                        mouseY,
                        horizontal,
                        vertical
                )){

            return true;

        }

        return super.mouseScrolled(
                mouseX,
                mouseY,
                horizontal,
                vertical
        );

    }

    @Override
    public boolean shouldPause(){

        return false;

    }

}