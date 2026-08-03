package com.kwork.nusic.input;

import com.kwork.nusic.core.NusicManager;

import net.minecraft.client.MinecraftClient;

import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWDropCallback;

public class NusicDropHandler {

    private static boolean registered = false;

    public static void register(){

        if(registered){
            return;
        }

        MinecraftClient client =
                MinecraftClient.getInstance();

        if(client.getWindow() == null){

            System.out.println(
                    "[Nusic] Window not ready, retrying..."
            );

            new Thread(() -> {

                try {

                    Thread.sleep(3000);

                }
                catch(Exception ignored){}

                register();

            }).start();

            return;

        }

        long handle =
                client.getWindow()
                        .getHandle();

        GLFW.glfwSetDropCallback(
                handle,
                new GLFWDropCallback(){

                    @Override
                    public void invoke(
                            long window,
                            int count,
                            long names
                    ){

                        for(int i = 0; i < count; i++){

                            String path =
                                    GLFWDropCallback.getName(
                                            names,
                                            i
                                    );

                            System.out.println(
                                    "[Nusic] Dropped: "
                                    + path
                            );

                            NusicManager
                                    .getInstance()
                                    .addFile(
                                            path
                                    );

                        }

                    }

                }
        );

        registered = true;

        System.out.println(
                "[Nusic] Drag&Drop enabled"
        );

    }

}