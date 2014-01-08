package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;

/**
 * Created by daemon on 08.01.14.
 */
public class Assets
{
    public static CheetahTextureAtlas Atlas;

    public static void load()
    {
        Atlas = new CheetahTextureAtlas(Gdx.files.internal("atlas.atlas"));
    }

    public static void dispose()
    {
        Atlas.dispose();
    }
}
