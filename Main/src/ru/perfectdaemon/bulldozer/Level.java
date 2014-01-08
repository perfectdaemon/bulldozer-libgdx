package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by daemon on 07.01.14.
 */
public class Level
{
    private Body earth;
    private Body[] b2dynamicBlocks;
    private Actor[] dynamicBlocks;

    private Vector2[] earthPoints;

    private short readShort(InputStream stream) throws IOException
    {
        byte[] b = new byte[2];
        stream.read(b, 0, 2);
        ByteBuffer buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    public Level(FileHandle levelFileHandle)
    {
        InputStream stream = levelFileHandle.read();
        byte[] b;
        ByteBuffer byteBuffer;
        Short count;
        try
        {
            //read earth
            count = readShort(stream);
            this.earthPoints = new Vector2[count];
            b = new byte[count * 4 * 2];
            stream.read(b, 0, b.length);
            byteBuffer = ByteBuffer.wrap(b);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < count; i++)
            {
                float x = byteBuffer.getFloat(8 * i);
                float y = byteBuffer.getFloat(8 * i + 4);
                this.earthPoints[i] = new Vector2(x, y).div(40f);
            }
            createEarth();

            //read dynamic blocks
            count = readShort(stream);
            Vector2[] dynamicBlocksPositions = new Vector2[count];
            b = new byte[count * 4 * 2]; //Two 4-byte float values per 1 point
            stream.read(b, 0, b.length);
            byteBuffer = ByteBuffer.wrap(b);
            byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
            for (int i = 0; i < count; i++)
            {
                float x = byteBuffer.getFloat(8 * i);
                float y = byteBuffer.getFloat(8 * i + 4);
                dynamicBlocksPositions[i] = new Vector2(x, y);
            }
            createDynamicBlocks(dynamicBlocksPositions);
            stream.close();
        }
        catch (Exception e)
        {
            Gdx.app.log("Game", "Unable to load level from " + levelFileHandle.name() + ". Exception message: " + e.getMessage());
        }
    }

    private void createEarth()
    {
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        bodyDef.position.set(0, 0);

        ChainShape shape = new ChainShape();
        shape.createChain(this.earthPoints);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.friction = 0.8f;
        fixtureDef.restitution = 0.1f;
        fixtureDef.shape = shape;
        fixtureDef.filter.categoryBits = Const.CAT_STATIC;
        fixtureDef.filter.maskBits = Const.MASK_EARTH;

        earth = Global.world.createBody(bodyDef);
        earth.createFixture(fixtureDef);

        shape.dispose();
    }

    private void createDynamicBlocks(Vector2[] positions)
    {
        b2dynamicBlocks = new Body[positions.length];
        for (int i = 0; i < positions.length; i++)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(positions[i].div(40f));

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.3f, 0.3f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 0.02f;
            fixtureDef.friction = 0.4f;
            fixtureDef.restitution = 0.4f;
            fixtureDef.shape = shape;
            fixtureDef.filter.categoryBits = Const.CAT_DYNAMIC;
            fixtureDef.filter.maskBits = Const.MASK_DYNAMIC;

            b2dynamicBlocks[i] = Global.world.createBody(bodyDef);
            b2dynamicBlocks[i].createFixture(fixtureDef);

            shape.dispose();
        }
    }
}
