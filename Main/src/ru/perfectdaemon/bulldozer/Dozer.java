package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Created by daemon on 08.01.14.
 */
public class Dozer
{
    public enum MoveDirection {NoMove, Left, Right};

    Actor WheelRear, WheelFront, SuspRear, SuspFront, CarBody, LightStop, LightRear;
    Body b2WheelRear, b2WheelFront, b2CarBody, b2SuspRear, b2SuspFront;
    RevoluteJoint b2WheelJointRear, b2WheelJointFront;
    PrismaticJoint b2SuspJointRear, b2SuspJointFront;

    float CurrentMotorSpeed, MaxMotorSpeed, Acceleration, BodySpeed, WheelSpeed;
    int Gear;
    float[] Gears;

    MoveDirection Direction;


    boolean AutomaticTransmission;

    private void initBodies(DozerParams params, Vector2 startPoint)
    {
        b2CarBody = PhysicHelper.createBoxBody(Global.world, BodyDef.BodyType.DynamicBody, startPoint,
                params.BodySize.div(2f), params.BodyD, params.BodyF, params.BodyR,
                Const.CAT_PLAYER, Const.MASK_PLAYER, Const.GROUP_PLAYER);
        b2WheelFront = PhysicHelper.createCircleBody(Global.world, BodyDef.BodyType.DynamicBody, params.WheelFrontOffset.cpy().add(startPoint),
                params.WheelFrontSize / 2f, params.WheelFrontD, params.WheelFrontF, params.WheelFrontR,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
        b2WheelRear = PhysicHelper.createCircleBody(Global.world, BodyDef.BodyType.DynamicBody, params.WheelRearOffset.cpy().add(startPoint),
                params.WheelRearSize / 2f, params.WheelRearD, params.WheelRearF, params.WheelRearR,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
        b2SuspFront = PhysicHelper.createBoxBody(Global.world, BodyDef.BodyType.DynamicBody, params.SuspFrontOffset.cpy().add(startPoint),
                new Vector2(0.125f, 0.5f).div(2f), 1.0f, 0.0f, 0.0f,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
        b2SuspRear = PhysicHelper.createBoxBody(Global.world, BodyDef.BodyType.DynamicBody, params.SuspRearOffset.cpy().add(startPoint),
                new Vector2(0.125f, 0.5f).div(2f), 1.0f, 0.0f, 0.0f,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
    }

    private void initJoints(DozerParams params)
    {
        PrismaticJointDef def = new PrismaticJointDef();
        Vector2 suspAxis = params.WheelRearOffset.cpy().sub(params.SuspRearOffset);
        def.initialize(b2CarBody, b2SuspRear, b2SuspRear.getPosition(), suspAxis);
        def.enableMotor = true;
        def.enableLimit = true;
        b2SuspJointRear = (PrismaticJoint) Global.world.createJoint(def);
        b2SuspJointRear.setLimits(params.SuspRearLimit.x, params.SuspRearLimit.y);
        b2SuspJointRear.setMotorSpeed(params.SuspRearMotorSpeed);
        b2SuspJointRear.setMaxMotorForce(params.SuspRearMaxMotorForce);

        def = new PrismaticJointDef();
        suspAxis = params.WheelFrontOffset.cpy().sub(params.SuspFrontOffset);
        def.initialize(b2CarBody, b2SuspFront, b2SuspFront.getPosition(), suspAxis);
        def.enableMotor = true;
        def.enableLimit = true;
        b2SuspJointFront = (PrismaticJoint) Global.world.createJoint(def);
        b2SuspJointFront.setLimits(params.SuspFrontLimit.x, params.SuspFrontLimit.y);
        b2SuspJointFront.setMotorSpeed(params.SuspFrontMotorSpeed);
        b2SuspJointFront.setMaxMotorForce(params.SuspFrontMaxMotorForce);

        RevoluteJointDef rdef = new RevoluteJointDef();
        rdef.initialize(b2WheelRear, b2SuspRear, b2WheelRear.getPosition());
        b2WheelJointRear = (RevoluteJoint) Global.world.createJoint(rdef);
        //b2WheelJointRear.setMaxMotorTorque();

        rdef = new RevoluteJointDef();
        rdef.initialize(b2WheelFront, b2SuspFront, b2WheelFront.getPosition());
        b2WheelJointFront = (RevoluteJoint) Global.world.createJoint(rdef);
    }

    public Dozer(DozerParams params, Vector2 startPoint)
    {
        this.initBodies(params, startPoint);
        this.initJoints(params);
    }

    public void tryGearUp()
    {}

    public void tryGearDown()
    {}

    public void addAccel()
    {}
}
