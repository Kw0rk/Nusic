package com.kwork.nusic.gui;

import com.kwork.nusic.core.PlaylistManager;
import com.kwork.nusic.data.Playlist;
import com.kwork.nusic.gui.components.PlayerBar;
import com.kwork.nusic.gui.components.Sidebar;
import com.kwork.nusic.gui.pages.HomePage;
import com.kwork.nusic.gui.pages.Page;
import com.kwork.nusic.gui.pages.PlaylistPage;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.input.CharInput;
import net.minecraft.client.input.KeyInput;
import net.minecraft.text.Text;

import org.lwjgl.glfw.GLFW;

public class NusicScreen extends Screen {

    private static final int NAME_MAX_LENGTH = 40;

    private final Sidebar sidebar;

    private final PlayerBar playerBar;

    private final HomePage homePage;

    private Page page;

    private boolean creatingPlaylist;

    private final StringBuilder nameInput =
            new StringBuilder();

    public NusicScreen(){

        super(
                Text.literal(
                        "Nusic"
                )
        );

        homePage =
                new HomePage();

        page = homePage;

        playerBar =
                new PlayerBar();

        sidebar =
                new Sidebar(
                        new Sidebar.Listener(){

                            @Override
                            public void onHome(){

                                openHome();

                            }

                            @Override
                            public void onPlaylistSelected(
                                    Playlist playlist
                            ){

                                openPlaylist(
                                        playlist
                                );

                            }

                            @Override
                            public void onCreatePlaylist(){

                                creatingPlaylist = true;

                                nameInput.setLength(0);

                            }

                        }
                );

    }

    private void openHome(){

        page = homePage;

        sidebar.setSelection(
                true,
                null
        );

    }

    private void openPlaylist(
            Playlist playlist
    ){

        page =
                new PlaylistPage(
                        playlist,
                        this::openHome
                );

        sidebar.setSelection(
                false,
                playlist
        );

    }

    @Override
    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            float delta
    ){

        context.fill(
                0,
                0,
                width,
                height,
                Theme.BACKGROUND
        );

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

        sidebar.render(
                context,
                height,
                mouseX,
                mouseY
        );

        playerBar.render(
                context,
                width,
                height,
                mouseX,
                mouseY
        );

        if(creatingPlaylist){

            renderNameDialog(
                    context
            );

        }

    }

    private void renderNameDialog(
            DrawContext context
    ){

        TextRenderer renderer =
                MinecraftClient
                        .getInstance()
                        .textRenderer;

        context.fill(
                0,
                0,
                width,
                height,
                0xAA000000
        );

        int boxWidth = 200;

        int boxHeight = 64;

        int x =
                (width - boxWidth) / 2;

        int y =
                (height - boxHeight) / 2;

        context.fill(
                x,
                y,
                x + boxWidth,
                y + boxHeight,
                0xFF202020
        );

        context.fill(
                x,
                y,
                x + boxWidth,
                y + 1,
                Theme.ACCENT
        );

        context.drawText(
                renderer,
                Text.literal(
                        "New playlist"
                ),
                x + 10,
                y + 8,
                Theme.TEXT,
                false
        );

        context.fill(
                x + 10,
                y + 22,
                x + boxWidth - 10,
                y + 38,
                0xFF121212
        );

        String shown =
                nameInput + "_";

        context.drawText(
                renderer,
                Text.literal(shown),
                x + 14,
                y + 26,
                Theme.TEXT,
                false
        );

        context.drawText(
                renderer,
                Text.literal(
                        "Enter - create   Esc - cancel"
                ),
                x + 10,
                y + 46,
                Theme.TEXT_DIM,
                false
        );

    }

    @Override
    public boolean keyPressed(
            KeyInput input
    ){

        if(creatingPlaylist){

            if(input.key() == GLFW.GLFW_KEY_ESCAPE){

                creatingPlaylist = false;

                return true;

            }

            if(input.key() == GLFW.GLFW_KEY_ENTER ||
                    input.key() == GLFW.GLFW_KEY_KP_ENTER){

                String name =
                        nameInput
                                .toString()
                                .trim();

                if(!name.isEmpty()){

                    Playlist playlist =
                            PlaylistManager.create(
                                    name
                            );

                    creatingPlaylist = false;

                    if(playlist != null){

                        openPlaylist(
                                playlist
                        );

                    }

                }

                return true;

            }

            if(input.key() == GLFW.GLFW_KEY_BACKSPACE){

                if(nameInput.length() > 0){

                    nameInput.setLength(
                            nameInput.length() - 1
                    );

                }

                return true;

            }

            return true;

        }

        return super.keyPressed(
                input
        );

    }

    @Override
    public boolean charTyped(
            CharInput input
    ){

        if(creatingPlaylist){

            if(input.isValidChar() &&
                    nameInput.length() < NAME_MAX_LENGTH){

                nameInput.append(
                        input.asString()
                );

            }

            return true;

        }

        return super.charTyped(
                input
        );

    }

    @Override
    public boolean mouseClicked(
            Click click,
            boolean doubled
    ){

        if(creatingPlaylist){

            return true;

        }

        if(playerBar.mouseClicked(
                click,
                doubled
        )){

            return true;

        }

        if(sidebar.mouseClicked(
                click
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
    public boolean mouseDragged(
            Click click,
            double deltaX,
            double deltaY
    ){

        if(playerBar.mouseDragged(
                click
        )){

            return true;

        }

        return super.mouseDragged(
                click,
                deltaX,
                deltaY
        );

    }

    @Override
    public boolean mouseReleased(
            Click click
    ){

        if(playerBar.mouseReleased(
                click
        )){

            return true;

        }

        return super.mouseReleased(
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

        if(sidebar.mouseScrolled(
                mouseX,
                mouseY,
                vertical
        )){

            return true;

        }

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
