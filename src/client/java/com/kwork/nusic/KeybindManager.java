package com.kwork.nusic;

import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;

import org.lwjgl.glfw.GLFW;

public class KeybindManager {

    public static KeyBinding openMenu;

    public static void register() {

        openMenu = KeyBindingHelper.registerKeyBinding(
                new KeyBinding(
                        "key.nusic.open",
                        InputUtil.Type.KEYSYM,
                        GLFW.GLFW_KEY_N,
                        KeyBinding.Category.create(
                                Identifier.of(
                                        "nusic",
                                        "main"
                                )
                        )
                )
        );

    }

    public static boolean pressed() {

        return openMenu.wasPressed();

    }

}