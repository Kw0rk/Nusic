package com.kwork.nusic.gui.components;

import com.kwork.nusic.core.NusicManager;
import com.kwork.nusic.core.PlaybackManager;
import com.kwork.nusic.core.PlaylistManager;
import com.kwork.nusic.core.Track;
import com.kwork.nusic.data.Playlist;
import com.kwork.nusic.gui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * Scrollable list of tracks.
 *
 * LIBRARY mode shows a "+" button (add to playlist) and a "x" button
 * (delete file). PLAYLIST mode shows a "-" button (remove from playlist).
 */
public class TrackList {

    public enum Mode {
        LIBRARY,
        PLAYLIST
    }

    private static final int ROW_HEIGHT = 24;

    private static final int BUTTON_SIZE = 16;

    private final Mode mode;

    private Playlist playlist;

    private List<Track> tracks = new ArrayList<>();

    private Runnable onChanged;

    private int x;
    private int y;
    private int width;
    private int height;

    private int scrollOffset;

    private int popupIndex = -1;

    private int popupX;
    private int popupY;

    public TrackList(Mode mode) {
        this.mode = mode;
    }

    public void setPlaylist(Playlist playlist) {
        this.playlist = playlist;
    }

    public void setTracks(List<Track> tracks) {
        this.tracks = tracks == null ? new ArrayList<>() : tracks;
    }

    public void setOnChanged(Runnable onChanged) {
        this.onChanged = onChanged;
    }

    public void render(
            DrawContext context,
            int mouseX,
            int mouseY,
            int x,
            int y,
            int width,
            int height
    ) {

        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        TextRenderer renderer =
                MinecraftClient.getInstance().textRenderer;

        if (tracks.isEmpty()) {

            String message =
                    mode == Mode.LIBRARY
                            ? "No music found. Drop audio files into the game window."
                            : "Playlist is empty. Add tracks from Home with the + button.";

            context.drawText(
                    renderer,
                    Text.literal(message),
                    x + 6, y + 10,
                    Theme.TEXT_DIM,
                    false
            );

            return;

        }

        clampScroll();

        context.enableScissor(x, y, x + width, y + height);

        int start = scrollOffset / ROW_HEIGHT;
        int rowY = y - scrollOffset % ROW_HEIGHT;

        for (int i = start; i < tracks.size(); i++) {

            if (rowY > y + height) {
                break;
            }

            drawRow(context, renderer, tracks.get(i), i, rowY, mouseX, mouseY);

            rowY += ROW_HEIGHT;

        }

        context.disableScissor();

        drawScrollbar(context);

        if (popupIndex != -1) {
            drawPopup(context, renderer, mouseX, mouseY);
        }

    }

    private void drawRow(
            DrawContext context,
            TextRenderer renderer,
            Track track,
            int index,
            int rowY,
            int mouseX,
            int mouseY
    ) {

        Track current = PlaybackManager.getCurrentTrack();

        boolean playing =
                current != null &&
                current.getPath().equals(track.getPath());

        boolean hover =
                popupIndex == -1 &&
                insideList(mouseX, mouseY) &&
                mouseY >= rowY && mouseY < rowY + ROW_HEIGHT;

        if (hover) {
            context.fill(x, rowY, x + width, rowY + ROW_HEIGHT, Theme.HOVER);
        } else if (playing) {
            context.fill(x, rowY, x + width, rowY + ROW_HEIGHT, Theme.PANEL);
        }

        context.drawText(
                renderer,
                Text.literal(String.valueOf(index + 1)),
                x + 8, rowY + 8,
                playing ? Theme.ACCENT : Theme.TEXT_DIM,
                false
        );

        context.drawText(
                renderer,
                Text.literal(trim(renderer, track.getDisplayName())),
                x + 34, rowY + 8,
                playing ? Theme.ACCENT : Theme.TEXT,
                false
        );

        context.fill(
                x, rowY + ROW_HEIGHT - 1,
                x + width, rowY + ROW_HEIGHT,
                0xFF1B1B1B
        );

        if (!hover) {
            return;
        }

        if (mode == Mode.LIBRARY) {

            drawRowButton(
                    context, renderer, "+",
                    addButtonX(), rowY,
                    insideButton(mouseX, mouseY, addButtonX(), rowY),
                    Theme.ACCENT
            );

            drawRowButton(
                    context, renderer, "x",
                    deleteButtonX(), rowY,
                    insideButton(mouseX, mouseY, deleteButtonX(), rowY),
                    Theme.DANGER
            );

        } else {

            drawRowButton(
                    context, renderer, "-",
                    deleteButtonX(), rowY,
                    insideButton(mouseX, mouseY, deleteButtonX(), rowY),
                    Theme.DANGER
            );

        }

    }

    private void drawRowButton(
            DrawContext context,
            TextRenderer renderer,
            String label,
            int bx,
            int rowY,
            boolean hover,
            int hoverColor
    ) {

        int by = rowY + (ROW_HEIGHT - BUTTON_SIZE) / 2;

        context.fill(
                bx, by,
                bx + BUTTON_SIZE, by + BUTTON_SIZE,
                hover ? hoverColor : 0xFF333333
        );

        context.drawText(
                renderer,
                Text.literal(label),
                bx + 6, by + 4,
                hover ? 0xFF000000 : Theme.TEXT,
                false
        );

    }

    private void drawPopup(
            DrawContext context,
            TextRenderer renderer,
            int mouseX,
            int mouseY
    ) {

        List<Playlist> playlists =
                PlaylistManager.getPlaylists();

        int popupWidth = 110;

        int rows = Math.max(1, playlists.size());

        int popupHeight = 14 + rows * 14;

        int px = Math.min(popupX, x + width - popupWidth);

        int py = Math.min(popupY, y + height - popupHeight);

        context.fill(px, py, px + popupWidth, py + popupHeight, 0xFF202020);
        context.fill(px, py, px + popupWidth, py + 1, Theme.DIVIDER);
        context.fill(px, py + popupHeight - 1, px + popupWidth, py + popupHeight, Theme.DIVIDER);
        context.fill(px, py, px + 1, py + popupHeight, Theme.DIVIDER);
        context.fill(px + popupWidth - 1, py, px + popupWidth, py + popupHeight, Theme.DIVIDER);

        context.drawText(
                renderer,
                Text.literal("Add to playlist"),
                px + 6, py + 4,
                Theme.TEXT_DIM,
                false
        );

        if (playlists.isEmpty()) {

            context.drawText(
                    renderer,
                    Text.literal("No playlists yet"),
                    px + 6, py + 18,
                    Theme.TEXT_MUTED,
                    false
            );

            return;

        }

        int rowY = py + 14;

        for (Playlist playlist : playlists) {

            boolean hover =
                    mouseX >= px && mouseX < px + popupWidth &&
                    mouseY >= rowY && mouseY < rowY + 14;

            if (hover) {
                context.fill(px + 1, rowY, px + popupWidth - 1, rowY + 14, Theme.HOVER);
            }

            context.drawText(
                    renderer,
                    Text.literal(trimTo(renderer, playlist.getName(), popupWidth - 12)),
                    px + 6, rowY + 3,
                    Theme.TEXT,
                    false
            );

            rowY += 14;

        }

    }

    public boolean mouseClicked(Click click) {

        if (click.button() != 0) {
            return popupIndex != -1 && closePopup();
        }

        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        if (popupIndex != -1) {
            return handlePopupClick(mouseX, mouseY);
        }

        if (!insideList(mouseX, mouseY)) {
            return false;
        }

        int index = (mouseY - y + scrollOffset) / ROW_HEIGHT;

        if (index < 0 || index >= tracks.size()) {
            return false;
        }

        int rowY = y + index * ROW_HEIGHT - scrollOffset;

        Track track = tracks.get(index);

        if (mode == Mode.LIBRARY &&
                insideButton(mouseX, mouseY, addButtonX(), rowY)) {

            popupIndex = index;
            popupX = addButtonX() - 110;
            popupY = rowY + ROW_HEIGHT;

            return true;

        }

        if (insideButton(mouseX, mouseY, deleteButtonX(), rowY)) {

            if (mode == Mode.LIBRARY) {
                NusicManager
                        .getInstance()
                        .deleteTrack(track);
            } else {
                PlaylistManager.removeTrack(playlist, track);
            }

            changed();

            return true;

        }

        PlaybackManager.setQueue(tracks);
        PlaybackManager.playIndex(index);

        return true;

    }

    private boolean handlePopupClick(int mouseX, int mouseY) {

        List<Playlist> playlists =
                PlaylistManager.getPlaylists();

        int popupWidth = 110;

        int rows = Math.max(1, playlists.size());

        int popupHeight = 14 + rows * 14;

        int px = Math.min(popupX, x + width - popupWidth);

        int py = Math.min(popupY, y + height - popupHeight);

        if (mouseX < px || mouseX >= px + popupWidth ||
                mouseY < py || mouseY >= py + popupHeight) {
            return closePopup();
        }

        int row = (mouseY - py - 14) / 14;

        if (row >= 0 && row < playlists.size() &&
                popupIndex >= 0 && popupIndex < tracks.size()) {

            PlaylistManager.addTrack(
                    playlists.get(row),
                    tracks.get(popupIndex)
            );

        }

        return closePopup();

    }

    private boolean closePopup() {
        popupIndex = -1;
        return true;
    }

    public boolean mouseScrolled(
            double mouseX,
            double mouseY,
            double amount
    ) {

        if (!insideList((int) mouseX, (int) mouseY)) {
            return false;
        }

        scrollOffset -= (int) (amount * ROW_HEIGHT);

        clampScroll();

        return true;

    }

    private void changed() {
        if (onChanged != null) {
            onChanged.run();
        }
    }

    private int addButtonX() {
        return x + width - BUTTON_SIZE * 2 - 12;
    }

    private int deleteButtonX() {
        return x + width - BUTTON_SIZE - 6;
    }

    private boolean insideButton(int mx, int my, int bx, int rowY) {

        int by = rowY + (ROW_HEIGHT - BUTTON_SIZE) / 2;

        return mx >= bx && mx < bx + BUTTON_SIZE &&
                my >= by && my < by + BUTTON_SIZE;

    }

    private boolean insideList(int mx, int my) {
        return mx >= x && mx <= x + width &&
                my >= y && my <= y + height;
    }

    private void clampScroll() {

        int max = Math.max(0, tracks.size() * ROW_HEIGHT - height);

        scrollOffset = Math.max(0, Math.min(scrollOffset, max));

    }

    private void drawScrollbar(DrawContext context) {

        int total = tracks.size() * ROW_HEIGHT;

        if (total <= height) {
            return;
        }

        int bar = Math.max(20, height * height / total);

        int pos = y + scrollOffset * (height - bar) / (total - height);

        context.fill(
                x + width - 2, pos,
                x + width, pos + bar,
                0xFF5F5F5F
        );

    }

    private String trim(TextRenderer renderer, String name) {
        return trimTo(renderer, name, width - 100);
    }

    private String trimTo(TextRenderer renderer, String name, int max) {

        if (renderer.getWidth(name) <= max) {
            return name;
        }

        String result = name;

        while (result.length() > 1 &&
                renderer.getWidth(result + "...") > max) {
            result = result.substring(0, result.length() - 1);
        }

        return result + "...";

    }

    public void reload() {
        scrollOffset = 0;
        popupIndex = -1;
    }

}
