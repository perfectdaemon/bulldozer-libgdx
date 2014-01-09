package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Created by daemon on 08.01.14.
 */
public class Assets
{
    public static CheetahTextureAtlas Atlas;
    private static Texture fontTexture;
    public static BitmapFont font;

    public static void load()
    {
        Atlas = new CheetahTextureAtlas(Gdx.files.internal("main/atlas.atlas"));
        fontTexture = new Texture(Gdx.files.internal("main/cooper.png"));
        fontTexture.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        font = new BitmapFont(Gdx.files.internal("main/cooper.fnt"), new TextureRegion(fontTexture), true);
    }

    public static void dispose()
    {
        Atlas.dispose();
        font.dispose();
        fontTexture.dispose();
    }
}
