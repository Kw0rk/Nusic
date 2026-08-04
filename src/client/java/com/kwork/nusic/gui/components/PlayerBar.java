package com.kwork.nusic.gui.components;

import com.kwork.nusic.core.MusicPlayer;
import com.kwork.nusic.core.NusicManager;
import com.kwork.nusic.core.PlaybackManager;
import com.kwork.nusic.core.Track;
import com.kwork.nusic.data.SettingsData;
import com.kwork.nusic.gui.Theme;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.Click;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class PlayerBar {

    public static final int HEIGHT = 56;

    private static final int BUTTON = 20;

    private int width;
    private int height;

    private boolean draggingSeek;
    private boolean draggingVolume;

    public void render(
            DrawContext context,
            int width,
            int height,
            int mouseX,
            int mouseY
    ) {

        this.width = width;
        this.height = height;

        TextRenderer renderer =
                MinecraftClient.getInstance().textRenderer;

        MusicPlayer player = PlaybackManager.getPlayer();

        int top = height - HEIGHT;

        context.fill(0, top, width, height, Theme.PANEL);
        context.fill(0, top, width, top + 1, Theme.DIVIDER);

        Track track = player.getCurrentTrack();

        String title =
                track == null
                        ? "Nothing playing"
                        : track.getDisplayName();

        int titleMax = centerStart() - 20;

        context.drawText(
                renderer,
                Text.literal(trimTo(renderer, title, titleMax)),
                12, top + 12,
                track == null ? Theme.TEXT_DIM : Theme.TEXT,
                false
        );

        if (track != null) {

            context.drawText(
                    renderer,
                    Text.literal(trimTo(renderer, track.getArtist(), titleMax)),
                    12, top + 26,
                    Theme.TEXT_MUTED,
                    false
            );

        }

        SettingsData settings =
                NusicManager.getInstance().getSettings();

        drawControl(
                context, renderer, "S",
                shuffleX(), controlY(),
                hovered(mouseX, mouseY, shuffleX(), controlY()),
                settings.shuffle
        );

        drawControl(
                context, renderer, "<<",
                prevX(), controlY(),
                hovered(mouseX, mouseY, prevX(), controlY()),
                false
        );

        drawControl(
                context, renderer,
                player.isPlaying() ? "||" : ">",
                playX(), controlY(),
                hovered(mouseX, mouseY, playX(), controlY()),
                player.isPlaying()
        );

        drawControl(
                context, renderer, ">>",
                nextX(), controlY(),
                hovered(mouseX, mouseY, nextX(), controlY()),
                false
        );

        drawControl(
                context, renderer, "R",
                repeatX(), controlY(),
                hovered(mouseX, mouseY, repeatX(), controlY()),
                settings.repeatPlaylist
        );

        long duration = player.getDuration();
        long position = player.getPosition();

        float progress =
                duration > 0
                        ? Math.min(1f, (float) position / duration)
                        : 0f;

        int barY = seekBarY();

        context.fill(
                seekStart(), barY,
                seekEnd(), barY + 3,
                0xFF404040
        );

        int filled =
                seekStart() +
                (int) ((seekEnd() - seekStart()) * progress);

        boolean seekHover =
                draggingSeek ||
                insideSeek(mouseX, mouseY);

        context.fill(
                seekStart(), barY,
                filled, barY + 3,
                seekHover ? Theme.ACCENT : Theme.TEXT_MUTED
        );

        if (seekHover && duration > 0) {
            context.fill(
                    filled - 1, barY - 2,
                    filled + 2, barY + 5,
                    Theme.TEXT
            );
        }

        context.drawText(
                renderer,
                Text.literal(formatTime(position)),
                seekStart() - 34, barY - 2,
                Theme.TEXT_MUTED,
                false
        );

        context.drawText(
                renderer,
                Text.literal(formatTime(duration)),
                seekEnd() + 6, barY - 2,
                Theme.TEXT_MUTED,
                false
        );

        float volume = player.getVolume();

        context.drawText(
                renderer,
                Text.literal("Vol"),
                volumeStart() - 22, volumeY() - 2,
                Theme.TEXT_DIM,
                false
        );

        context.fill(
                volumeStart(), volumeY(),
                volumeEnd(), volumeY() + 3,
                0xFF404040
        );

        int volumeFilled =
                volumeStart() +
                (int) ((volumeEnd() - volumeStart()) * volume);

        boolean volumeHover =
                draggingVolume ||
                insideVolume(mouseX, mouseY);

        context.fill(
                volumeStart(), volumeY(),
                volumeFilled, volumeY() + 3,
                volumeHover ? Theme.ACCENT : Theme.TEXT_MUTED
        );

    }

    public boolean mouseClicked(Click click, boolean doubled) {

        if (click.button() != 0) {
            return false;
        }

        int mouseX = (int) click.x();
        int mouseY = (int) click.y();

        if (mouseY < height - HEIGHT) {
            return false;
        }

        MusicPlayer player = PlaybackManager.getPlayer();

        SettingsData settings =
                NusicManager.getInstance().getSettings();

        if (hovered(mouseX, mouseY, shuffleX(), controlY())) {
            settings.shuffle = !settings.shuffle;
            NusicManager.getInstance().saveSettings();
            return true;
        }

        if (hovered(mouseX, mouseY, prevX(), controlY())) {
            PlaybackManager.previous();
            return true;
        }

        if (hovered(mouseX, mouseY, playX(), controlY())) {

            if (player.isPlaying() || player.isPaused()) {
                player.togglePause();
            } else {
                PlaybackManager.play();
            }

            return true;

        }

        if (hovered(mouseX, mouseY, nextX(), controlY())) {
            PlaybackManager.next();
            return true;
        }

        if (hovered(mouseX, mouseY, repeatX(), controlY())) {
            settings.repeatPlaylist = !settings.repeatPlaylist;
            NusicManager.getInstance().saveSettings();
            return true;
        }

        if (insideSeek(mouseX, mouseY)) {
            draggingSeek = true;
            seekTo(mouseX);
            return true;
        }

        if (insideVolume(mouseX, mouseY)) {
            draggingVolume = true;
            volumeTo(mouseX);
            return true;
        }

        return true;

    }

    public boolean mouseDragged(Click click) {

        int mouseX = (int) click.x();

        if (draggingSeek) {
            seekTo(mouseX);
            return true;
        }

        if (draggingVolume) {
            volumeTo(mouseX);
            return true;
        }

        return false;

    }

    public boolean mouseReleased(Click click) {

        boolean handled = draggingSeek || draggingVolume;

        if (draggingVolume) {
            NusicManager.getInstance().saveSettings();
        }

        draggingSeek = false;
        draggingVolume = false;

        return handled;

    }

    private void seekTo(int mouseX) {

        MusicPlayer player = PlaybackManager.getPlayer();

        long duration = player.getDuration();

        if (duration <= 0) {
            return;
        }

        float ratio = ratio(mouseX, seekStart(), seekEnd());

        player.seek((long) (duration * ratio));

    }

    private void volumeTo(int mouseX) {

        float ratio = ratio(mouseX, volumeStart(), volumeEnd());

        PlaybackManager.getPlayer().setVolume(ratio);

    }

    private float ratio(int mouseX, int start, int end) {

        if (end <= start) {
            return 0f;
        }

        return Math.max(
                0f,
                Math.min(1f, (float) (mouseX - start) / (end - start))
        );

    }

    private void drawControl(
            DrawContext context,
            TextRenderer renderer,
            String label,
            int x,
            int y,
            boolean hover,
            boolean active
    ) {

        int background;

        if (hover) {
            background = Theme.ACCENT;
        } else if (active) {
            background = Theme.ACCENT_DARK;
        } else {
            background = 0xFF2A2A2A;
        }

        context.fill(x, y, x + BUTTON, y + BUTTON, background);

        int labelWidth = renderer.getWidth(label);

        context.drawText(
                renderer,
                Text.literal(label),
                x + (BUTTON - labelWidth) / 2,
                y + 6,
                hover || active ? 0xFF000000 : Theme.TEXT,
                false
        );

    }

    private boolean hovered(int mx, int my, int x, int y) {
        return mx >= x && mx < x + BUTTON &&
                my >= y && my < y + BUTTON;
    }

    private boolean insideSeek(int mx, int my) {
        return mx >= seekStart() - 4 && mx <= seekEnd() + 4 &&
                my >= seekBarY() - 5 && my <= seekBarY() + 8;
    }

    private boolean insideVolume(int mx, int my) {
        return mx >= volumeStart() - 4 && mx <= volumeEnd() + 4 &&
                my >= volumeY() - 5 && my <= volumeY() + 8;
    }

    private int centerStart() {
        return shuffleX();
    }

    private int controlY() {
        return height - HEIGHT + 8;
    }

    private int shuffleX() {
        return width / 2 - BUTTON * 2 - 28;
    }

    private int prevX() {
        return width / 2 - BUTTON - 14;
    }

    private int playX() {
        return width / 2 - BUTTON / 2;
    }

    private int nextX() {
        return width / 2 + 14;
    }

    private int repeatX() {
        return width / 2 + BUTTON + 28;
    }

    private int seekBarY() {
        return height - HEIGHT + 38;
    }

    private int seekStart() {
        return Math.max(width / 2 - 120, 180);
    }

    private int seekEnd() {
        return Math.min(width / 2 + 120, width - 160);
    }

    private int volumeStart() {
        return width - 90;
    }

    private int volumeEnd() {
        return width - 14;
    }

    private int volumeY() {
        return height - HEIGHT + 16;
    }

    private String formatTime(long millis) {

        long seconds = millis / 1000;

        return String.format(
                "%02d:%02d",
                seconds / 60,
                seconds % 60
        );

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

}
