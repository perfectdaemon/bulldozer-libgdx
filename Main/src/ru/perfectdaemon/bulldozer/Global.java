package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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
    public static SpriteBatch batch;
    public static OrthographicCamera camera;

    public static float lerp(float start, float finish, float t)
    {
        return start + (finish - start) * t;
    }

    public static Vector2 clamp(Vector2 value, Vector2 min, Vector2 max)
    {
        return new Vector2(MathUtils.clamp(value.x, min.x, max.x), MathUtils.clamp(value.y, min.y, max.y));
    }


    public static void load()
    {

        world = new World(new Vector2(0, 10), true);
        debugRenderer = new Box2DDebugRenderer();
        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    public static void dispose()
    {
        debugRenderer.dispose();
        world.dispose();
    }
}
