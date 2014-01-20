package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import java.nio.charset.Charset;

/**
 * Created by daemon on 08.01.14.
 */
public class DozerParams
{
    public float BodyR, BodyD, BodyF,
            WheelRearR, WheelRearD, WheelRearF,
            WheelFrontR, WheelFrontD, WheelFrontF;

    public Vector2 WheelRearOffset, WheelFrontOffset,
            SuspRearOffset, SuspFrontOffset,
            BodyMassCenterOffset, BodySize;

    public float WheelRearSize, WheelFrontSize;

    public Vector2 SuspRearLimit, SuspFrontLimit;

    public float SuspRearMotorSpeed, SuspRearMaxMotorForce,
            SuspFrontMotorSpeed, SuspFrontMaxMotorForce;

    public float MaxMotorSpeed, Acceleration;
    public int GearCount;
    public float[] Gears;

    public Vector2 ShovelOffset;
    public float ShovelScale;
    public int ShovelCount;
    public Vector2[] ShovelPoints;

    public DozerParams()
    {
    }

    private float readFloat(String string)
    {
        return new Float(string.split(";")[0]);
    }

    private int readInt(String string)
    {
        return new Integer(string.split(";")[0]);
    }

    private Vector2 readVector(String string)
    {
        String[] values = string.split(";")[0].split(" ");
        return new Vector2(new Float(values[0]), new Float(values[1]));
    }

    public DozerParams(FileHandle fileHandle)
    {
        String fileContent = new String(fileHandle.readBytes(), Charset.defaultCharset());
        String[] lines = fileContent.split("(\\r\\n)|(\\n\\r)");
        int i = 0;
        BodyR = readFloat(lines[i++]);
        BodyD = readFloat(lines[i++]);
        BodyF = readFloat(lines[i++]);
        WheelRearR = readFloat(lines[i++]);
        WheelRearD = readFloat(lines[i++]);
        WheelRearF = readFloat(lines[i++]);
        WheelFrontR = readFloat(lines[i++]);
        WheelFrontD = readFloat(lines[i++]);
        WheelFrontF = readFloat(lines[i++]);

        BodySize = readVector(lines[i++]);
        WheelRearSize = readFloat(lines[i++]);
        WheelFrontSize = readFloat(lines[i++]);

        WheelRearOffset = readVector(lines[i++]);
        WheelFrontOffset = readVector(lines[i++]);
        SuspRearOffset = readVector(lines[i++]);
        SuspFrontOffset = readVector(lines[i++]);
        BodyMassCenterOffset = readVector(lines[i++]);

        SuspRearLimit = readVector(lines[i++]);
        SuspFrontLimit = readVector(lines[i++]);

        SuspRearMotorSpeed = readFloat(lines[i++]);
        SuspRearMaxMotorForce = readFloat(lines[i++]);
        SuspFrontMotorSpeed = readFloat(lines[i++]);
        SuspFrontMaxMotorForce = readFloat(lines[i++]);

        MaxMotorSpeed = readFloat(lines[i++]);
        Acceleration = readFloat(lines[i++]);
        GearCount = readInt(lines[i++]);

        Gears = new float[GearCount];
        for (int j = 0; j < Gears.length; j++)
            Gears[j] = readFloat(lines[i++]);

        ShovelOffset = readVector(lines[i++]);
        ShovelScale = readFloat(lines[i++]);
        ShovelCount = readInt(lines[i++]);
        ShovelPoints = new Vector2[ShovelCount];
        for (int j = 0; j < ShovelCount; j++)
            ShovelPoints[j] = readVector(lines[i++]).scl(ShovelScale);
    }

}
