package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;

/**
 * Created by daemon on 08.01.14.
 */
public class Global
{
    public static World world;
    public static Box2DDebugRenderer debugRenderer;

    public static void load()
    {
        world = new World(new Vector2(0, 10), true);
        debugRenderer = new Box2DDebugRenderer();
    }

    public static void dispose()
    {
        debugRenderer.dispose();
        world.dispose();
    }
}
