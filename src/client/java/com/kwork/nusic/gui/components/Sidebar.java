package com.kwork.nusic.gui.components;

import com.kwork.nusic.core.PlaylistManager;
import com.kwork.nusic.data.Playlist;
import com.kwork.nusic.gui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.List;

public class Sidebar {

    public static final int WIDTH = 130;

    private static final int ROW_HEIGHT = 16;

    public interface Listener {

        void onHome();

        void onPlaylistSelected(Playlist playlist);

        void onCreatePlaylist();

    }

    private final Listener listener;

    private int screenHeight;

    private int playlistTop;

    private int playlistBottom;

    private int scrollOffset;

    private boolean homeSelected = true;

    private Playlist selectedPlaylist;

    public Sidebar(Listener listener) {
        this.listener = listener;
    }

    public void setSelection(boolean home, Playlist playlist) {
        homeSelected = home;
        selectedPlaylist = playlist;
    }

    public void render(
            DrawContext context,
            int screenHeight,
            int mouseX,
            int mouseY
    ) {

        this.screenHeight = screenHeight;

        TextRenderer renderer =
                MinecraftClient.getInstance().textRenderer;

        int bottom = screenHeight - PlayerBar.HEIGHT;

        context.fill(0, 0, WIDTH, bottom, Theme.SIDEBAR);
        context.fill(WIDTH, 0, WIDTH + 1, bottom, Theme.DIVIDER);

        context.drawText(
                renderer,
                Text.literal("NUSIC"),
                14, 14,
                Theme.ACCENT,
                false
        );

        context.drawText(
                renderer,
                Text.literal("Music player"),
                14, 26,
                Theme.TEXT_DIM,
                false
        );

        boolean homeHover =
                inside(mouseX, mouseY, 0, 44, WIDTH, ROW_HEIGHT);

        if (homeHover && !homeSelected) {
            context.fill(0, 44, WIDTH, 44 + ROW_HEIGHT, Theme.HOVER);
        }

        context.drawText(
                renderer,
                Text.literal("Home"),
                14, 48,
                homeSelected ? Theme.TEXT : Theme.TEXT_MUTED,
                false
        );

        if (homeSelected) {
            context.fill(0, 44, 2, 44 + ROW_HEIGHT, Theme.ACCENT);
        }

        context.drawText(
                renderer,
                Text.literal("PLAYLISTS"),
                14, 74,
                Theme.TEXT_DIM,
                false
        );

        boolean plusHover =
                inside(mouseX, mouseY, WIDTH - 24, 70, 16, 14);

        context.fill(
                WIDTH - 24, 70,
                WIDTH - 8, 84,
                plusHover ? Theme.ACCENT : Theme.HOVER
        );

        context.drawText(
                renderer,
                Text.literal("+"),
                WIDTH - 18, 73,
                plusHover ? 0xFF000000 : Theme.TEXT,
                false
        );

        playlistTop = 90;
        playlistBottom = bottom - 16;

        List<Playlist> playlists =
                PlaylistManager.getPlaylists();

        if (playlists.isEmpty()) {

            context.drawText(
                    renderer,
                    Text.literal("No playlists"),
                    14, playlistTop + 4,
                    Theme.TEXT_DIM,
                    false
            );

        } else {

            clampScroll(playlists.size());

            context.enableScissor(0, playlistTop, WIDTH, playlistBottom);

            int rowY = playlistTop - scrollOffset;

            for (Playlist playlist : playlists) {

                if (rowY + ROW_HEIGHT >= playlistTop && rowY <= playlistBottom) {

                    boolean selected = playlist == selectedPlaylist;

                    boolean hover =
                            inside(mouseX, mouseY, 0, rowY, WIDTH, ROW_HEIGHT)
                            && mouseY >= playlistTop
                            && mouseY <= playlistBottom;

                    if (hover && !selected) {
                        context.fill(0, rowY, WIDTH, rowY + ROW_HEIGHT, Theme.HOVER);
                    }

                    if (selected) {
                        context.fill(0, rowY, 2, rowY + ROW_HEIGHT, Theme.ACCENT);
                    }

                    context.drawText(
                            renderer,
                            Text.literal(trim(renderer, playlist.getName())),
                            14, rowY + 4,
                            selected ? Theme.TEXT : Theme.TEXT_MUTED,
                            false
                    );

                }

                rowY += ROW_HEIGHT;

            }

            context.disableScissor();

        }

        context.drawText(
                renderer,
                Text.literal("ESC  Close"),
                14, bottom - 12,
                Theme.TEXT_DIM,
                false
        );

    }

    public boolean mouseClicked(Click click) {

        if (click.button() != 0) {
            return false;
        }

        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        if (mouseX > WIDTH) {
            return false;
        }

        if (inside(mouseX, mouseY, WIDTH - 24, 70, 16, 14)) {
            listener.onCreatePlaylist();
            return true;
        }

        if (inside(mouseX, mouseY, 0, 44, WIDTH, ROW_HEIGHT)) {
            listener.onHome();
            return true;
        }

        if (mouseY >= playlistTop && mouseY <= playlistBottom) {

            List<Playlist> playlists =
                    PlaylistManager.getPlaylists();

            int index =
                    (mouseY - playlistTop + scrollOffset) / ROW_HEIGHT;

            if (index >= 0 && index < playlists.size()) {
                listener.onPlaylistSelected(playlists.get(index));
                return true;
            }

        }

        return mouseY < screenHeight - PlayerBar.HEIGHT;

    }

    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double amount
    ) {

        if (mouseX > WIDTH ||
                mouseY < playlistTop ||
                mouseY > playlistBottom) {
            return false;
        }

        scrollOffset -= (int) (amount * ROW_HEIGHT);

        clampScroll(PlaylistManager.getPlaylists().size());

        return true;

    }

    private void clampScroll(int count) {

        int viewHeight = playlistBottom - playlistTop;

        int max = Math.max(0, count * ROW_HEIGHT - viewHeight);

        scrollOffset = Math.max(0, Math.min(scrollOffset, max));

    }

    private String trim(TextRenderer renderer, String name) {

        if (renderer.getWidth(name) <= WIDTH - 24) {
            return name;
        }

        String result = name;

        while (result.length() > 1 &&
                renderer.getWidth(result + "...") > WIDTH - 24) {
            result = result.substring(0, result.length() - 1);
        }

        return result + "...";

    }

    private boolean inside(
            int mx, int my,
            int x, int y,
            int w, int h
    ) {
        return mx >= x && mx < x + w && my >= y && my < y + h;
    }

}
