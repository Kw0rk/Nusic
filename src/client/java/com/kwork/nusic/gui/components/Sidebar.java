package com.kwork.nusic.gui.components;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Sidebar {

    public static final int WIDTH = 125;

    public void render(
            DrawContext context,
            int screenHeight,
            int mouseY
    ) {
        TextRenderer textRenderer =
                MinecraftClient.getInstance().textRenderer;

        // Только тонкая вертикальная линия.
        context.fill(
                WIDTH,
                14,
                WIDTH + 1,
                screenHeight - 52,
                0xFF292D34
        );

        context.drawText(
                textRenderer,
                Text.literal("NUSIC"),
                18,
                18,
                0xFF8EE6B0,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("Music player"),
                18,
                32,
                0xFF6B727C,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("> Home"),
                18,
                70,
                0xFFFFFFFF,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("  Library"),
                18,
                92,
                0xFF858B94,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("  Settings"),
                18,
                114,
                0xFF858B94,
                false
        );

        context.drawText(
                textRenderer,
                Text.literal("ESC  Close"),
                18,
                screenHeight - 68,
                0xFF555C65,
                false
        );
    }
}