package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;

/**
 * Created by daemon on 31.12.13.
 */
public class BulldozerGame extends Game
{
    GameScreen gameScreen;
    @Override
    public void create()
    {
        Gdx.app.log("Game", "Start to create a game class");
        Assets.load();
        gameScreen = new GameScreen(this);
        setScreen(gameScreen);
    }

    @Override
    public void render()
    {
        super.render();
    }

    @Override
    public void dispose()
    {
        super.dispose();
        gameScreen.dispose();
        Assets.dispose();
    }
}
