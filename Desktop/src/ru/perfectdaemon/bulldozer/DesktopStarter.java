package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.backends.lwjgl.*;

/**
 * Created by daemon on 31.12.13.
 */
public class DesktopStarter {
    public static void main(String[] args) {
        LwjglApplicationConfiguration cfg = new LwjglApplicationConfiguration();
        cfg.title = "Bulldozer game by perfectdaemon";
        cfg.useGL20 = true;
        cfg.width = 800;
        cfg.height = 480;
        new LwjglApplication(new BulldozerGame(), cfg);
    }
}
