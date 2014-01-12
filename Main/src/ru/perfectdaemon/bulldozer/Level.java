package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.scenes.scene2d.*;

import java.io.*;
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

    private Vector2[] earthPoints, dynamicBlocksPositions;

    private short readShort(InputStream stream) throws IOException
    {
        byte[] b = new byte[2];
        stream.read(b, 0, 2);
        ByteBuffer buffer = ByteBuffer.wrap(b);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        return buffer.getShort();
    }

    private byte[] shortToByteArray(short value)
    {
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        buffer.putShort(value);
        return buffer.array();
    }

    private byte[] vector2ArrayToByteArray(Vector2[] v)
    {
        ByteBuffer buffer = ByteBuffer.allocate(v.length * 4 * 2);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (int i = 0; i < v.length; i++)
        {
            buffer.putFloat(8 * i,     v[i].x);
            buffer.putFloat(8 * i + 4, v[i].y);
        }
        return buffer.array();
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
                this.earthPoints[i] = new Vector2(x, y);//.div(40f);
            }
            createEarth();

            //read dynamic blocks
            count = readShort(stream);
            this.dynamicBlocksPositions = new Vector2[count];
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
            createDynamicBlocks();
            stream.close();
        }
        catch (Exception e)
        {
            Gdx.app.log("Game", "Unable to load level from " + levelFileHandle.name() + ". Exception message: " + e.getMessage());
        }
    }

    public byte[] saveLevelToBinary()
    {
        Short countEarth = (short) earthPoints.length;
        Short countDynamic = (short) b2dynamicBlocks.length;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        try
        {
            stream.write(shortToByteArray(countEarth));
            stream.write(vector2ArrayToByteArray(earthPoints));
            stream.write(shortToByteArray(countDynamic));
            stream.write(vector2ArrayToByteArray(dynamicBlocksPositions));
        }
        catch (Exception e)
        {
            Gdx.app.log("Game", "Unable to serialize level to binary. Exception message: " + e.getMessage());
        }
        return stream.toByteArray();
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

    private void createDynamicBlocks()
    {
        b2dynamicBlocks = new Body[dynamicBlocksPositions.length];
        for (int i = 0; i < dynamicBlocksPositions.length; i++)
        {
            BodyDef bodyDef = new BodyDef();
            bodyDef.type = BodyDef.BodyType.DynamicBody;
            bodyDef.position.set(dynamicBlocksPositions[i]);//.div(40f));

            PolygonShape shape = new PolygonShape();
            shape.setAsBox(0.3f, 0.3f);

            FixtureDef fixtureDef = new FixtureDef();
            fixtureDef.density = 0.03f;
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

    public Vector2 getLevelLimitMin()
    {
        return new Vector2(this.earthPoints[0]).add(Const.LEVEL_LIMIT_MIN_X, Const.LEVEL_LIMIT_MIN_Y);
    }

    public Vector2 getLevelLimitMax()
    {
        return new Vector2(this.earthPoints[earthPoints.length - 1]).add(Const.LEVEL_LIMIT_MAX_X, Const.LEVEL_LIMIT_MAX_Y);
    }

    public void dispose()
    {
        for (int i = 0; i < b2dynamicBlocks.length; i++)
            Global.world.destroyBody(b2dynamicBlocks[i]);
        Global.world.destroyBody(earth);
    }
}
