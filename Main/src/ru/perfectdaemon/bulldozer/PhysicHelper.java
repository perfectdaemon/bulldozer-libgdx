package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Created by daemon on 08.01.14.
 */
public class PhysicHelper
{
    public static Body createBodyWithShape(World world, BodyDef.BodyType type, Vector2 pos, Shape shape,
                                           float d, float f, float r, short cat, short mask, short groupIndex)
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = type;
        bodyDef.position.set(pos);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.density = d;
        fixtureDef.friction = f;
        fixtureDef.restitution = r;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = cat;
        fixtureDef.filter.maskBits = mask;
        fixtureDef.filter.groupIndex = groupIndex;

        Body body = world.createBody(bodyDef);
        body.createFixture(fixtureDef);

        shape.dispose();

        return body;
    }

    public static Body createBoxBody(World world, BodyDef.BodyType type, Vector2 pos, Vector2 halfSize,
                                     float d, float f, float r, short cat, short mask, short groupIndex)
    {
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(halfSize.x, halfSize.y);

        return createBodyWithShape(world, type, pos, shape, d, f, r, cat, mask, groupIndex);
    }

    public static Body createCircleBody(World world, BodyDef.BodyType type, Vector2 pos, float radius,
                                        float d, float f, float r, short cat, short mask, short groupIndex)
    {
        CircleShape shape = new CircleShape();
        shape.setRadius(radius);

        return createBodyWithShape(world, type, pos, shape, d, f, r, cat, mask, groupIndex);
    }
}
