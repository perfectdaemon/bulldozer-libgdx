package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.MassData;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJoint;
import com.badlogic.gdx.physics.box2d.joints.PrismaticJointDef;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;

/**
 * Created by daemon on 08.01.14.
 */
public class Dozer implements Disposable
{
    public enum MoveDirection {NoMove, Left, Right};

    boolean isGas, isBrake, isHandbrake;

    Actor WheelRear, WheelFront, SuspRear, SuspFront, CarBody, LightStop, LightRear;
    Body b2WheelRear, b2WheelFront, b2CarBody, b2SuspRear, b2SuspFront;
    RevoluteJoint b2WheelJointRear, b2WheelJointFront;
    PrismaticJoint b2SuspJointRear, b2SuspJointFront;

    float CurrentMotorSpeed, MaxMotorSpeed, Acceleration, BodySpeed, WheelSpeed;
    int Gear;
    float[] Gears;

    MoveDirection Direction;

    boolean AutomaticTransmission;

    public Dozer(DozerParams params, Vector2 startPoint)
    {
        initBodies(params, startPoint);
        initJoints(params);
        MaxMotorSpeed = params.MaxMotorSpeed;
        Acceleration = params.Acceleration;
        Gear = 1;
        //Gears = new float[params.GearCount];
        Gears = params.Gears.clone();
    }

    @Override
    public void dispose()
    {
        Global.world.destroyJoint(b2SuspJointFront);
        Global.world.destroyJoint(b2SuspJointRear);
        Global.world.destroyJoint(b2WheelJointFront);
        Global.world.destroyJoint(b2WheelJointRear);

        Global.world.destroyBody(b2CarBody);
        Global.world.destroyBody(b2SuspFront);
        Global.world.destroyBody(b2SuspRear);
        Global.world.destroyBody(b2WheelFront);
        Global.world.destroyBody(b2WheelRear);
    }

    private void initBodies(DozerParams params, Vector2 startPoint)
    {
        b2CarBody = PhysicHelper.createBoxBody(Global.world, BodyDef.BodyType.DynamicBody, startPoint,
                params.BodySize.div(2f), params.BodyD, params.BodyF, params.BodyR,
                Const.CAT_PLAYER, Const.MASK_PLAYER, Const.GROUP_PLAYER);
        b2CarBody.getMassData().center.set(params.BodyMassCenterOffset);
        b2WheelFront = PhysicHelper.createCircleBody(Global.world, BodyDef.BodyType.DynamicBody, params.WheelFrontOffset.cpy().add(startPoint),
                params.WheelFrontSize / 2f, params.WheelFrontD, params.WheelFrontF, params.WheelFrontR,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
        b2WheelRear = PhysicHelper.createCircleBody(Global.world, BodyDef.BodyType.DynamicBody, params.WheelRearOffset.cpy().add(startPoint),
                params.WheelRearSize / 2f, params.WheelRearD, params.WheelRearF, params.WheelRearR,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
        b2SuspFront = PhysicHelper.createBoxBody(Global.world, BodyDef.BodyType.DynamicBody, params.SuspFrontOffset.cpy().add(startPoint),
                new Vector2(0.125f, 0.5f).div(2f), 2.0f, 0.0f, 0.0f,
                Const.CAT_WHEELS, Const.MASK_PLAYER_WHEELS, Const.GROUP_PLAYER);
        b2SuspRear = PhysicHelper.createBoxBody(Global.world, BodyDef.BodyType.DynamicBody, params.SuspRearOffset.cpy().add(startPoint),
                new Vector2(0.125f, 0.5f).div(2f), 2.0f, 0.0f, 0.0f,
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

    private void tryGearUp()
    {
        if ((Gear != 0) && (Gear < (Gears.length) - 1))
        {
            Gear++; //Повышаем передачу
            CurrentMotorSpeed = CurrentMotorSpeed * (Gears[Gear - 1] / Gears[Gear]) * 0.7f;
        }
    }

    private void tryGearDown()
    {
        if (Gear > 1)
        {
            Gear--;
            CurrentMotorSpeed = CurrentMotorSpeed * (Gears[Gear + 1] / Gears[Gear]);
        }
    }

    private void addDownForce(float dt)
    {
        b2CarBody.applyLinearImpulse(new Vector2(0, 2.0f * Math.abs(BodySpeed) * dt), b2CarBody.getWorldCenter(), true);
    }

    private void addAccel(float dt)
    {
        CurrentMotorSpeed = MathUtils.clamp(CurrentMotorSpeed + dt * Acceleration, 0, MaxMotorSpeed);
    }

    private void reduceAccel(float dt)
    {
        CurrentMotorSpeed = MathUtils.clamp(CurrentMotorSpeed - dt * Acceleration, 0, MaxMotorSpeed);
    }

    private void calcMotorSpeed(float dt)
    {
        CurrentMotorSpeed = Global.lerp(CurrentMotorSpeed, Math.abs(WheelSpeed / Gears[Gear]), 1 / Math.abs(Gears[Gear]));
    }

    private void brake(boolean shouldBrake)
    {
        b2WheelJointFront.setMotorSpeed(0);
        b2WheelJointFront.enableMotor(shouldBrake);
        b2WheelJointFront.setMaxMotorTorque(8);
    }

    public boolean isGasDown()
    {
        return Gdx.input.isKeyPressed(Input.Keys.RIGHT)
                || (Gdx.input.isTouched() && ((float)Gdx.input.getX() / Gdx.graphics.getWidth()) > (2.0 / 3f));
    }

    public boolean isBrakeDown()
    {
        return Gdx.input.isKeyPressed(Input.Keys.LEFT)
                || (Gdx.input.isTouched() && ((float)Gdx.input.getX() / Gdx.graphics.getWidth()) < (1.0 / 3f));
    }

    public boolean isHandbrakeDown()
    {
        return Gdx.input.isKeyPressed(Input.Keys.SPACE)
                || (Gdx.input.isTouched()
                        && ((float)Gdx.input.getX() / Gdx.graphics.getWidth()) > 1 / 3f
                        && ((float)Gdx.input.getX() / Gdx.graphics.getWidth()) < 2 / 3f);
    }

    public void setGas(boolean isActive)
    {
        this.isGas = isActive;
    }

    public void setBrake(boolean isActive)
    {
        this.isBrake = isActive;
    }

    public void setHandBrake(boolean isActive)
    {
        this.isHandbrake = isActive;
    }

    public void update(float dt)
    {
        brake(false);
        boolean isAccelerating = false;

        if (isGas)
        {
            b2WheelJointRear.enableMotor(true);
            //Если включена задняя передача
            if (Gear == 0)
            {
                if (Math.abs(BodySpeed) < Const.PLAYER_CHANGE_GEAR_THRESHOLD)
                {
                    //Можно переключаться на первую
                    //RearLight.Visible := False;
                    Gear = 1;
                    CurrentMotorSpeed = 0;
                }
                else
                {
                    //Просто снижаем скорость
                    reduceAccel(3 * dt);
                    brake(true);
                    isAccelerating = false;
                    b2WheelJointRear.enableMotor(false);
                }
            }
            else
            {
                addAccel(dt);
                isAccelerating = true;
            }

        }
        if (isBrake)
        {
            b2WheelJointRear.enableMotor(true);
            if (Gear > 0)
            {
                if (BodySpeed < Const.PLAYER_CHANGE_GEAR_THRESHOLD)
                {
                    //Можно переключаться на заднюю
                    Gear = 0;
                    CurrentMotorSpeed = 0;
                    //RearLight.Visible = true;
                }
                else
                {
                    //Прост снижаем скорость
                    reduceAccel(2 * dt);
                    brake(true);
                    isAccelerating = false;
                    b2WheelJointRear.enableMotor(false);
                }
            }
            else
            {
                addAccel(dt);
                isAccelerating = true;
            }
        }
        //не нажато ни вперед, ни назад
        if (!isBrake && !isGas)
        {
            b2WheelJointRear.enableMotor(false);
            isAccelerating = false;
            reduceAccel(0.5f * dt);
            calcMotorSpeed(dt);
        }


        if (isAccelerating)
        {
            b2WheelJointRear.setMotorSpeed(-CurrentMotorSpeed * Gears[Gear]);
            b2WheelJointRear.setMaxMotorTorque(5 / Math.abs(Gears[Gear]));
            //if (WheelPoints > 0){ and (Gear > 0)} then
            addDownForce(dt);
        }

        if (isHandbrake)
        {
            b2WheelJointRear.enableMotor(true);
            b2WheelJointRear.setMotorSpeed(0);
            b2WheelJointRear.setMaxMotorTorque(10);
        }

        automaticTransmissionUpdate(dt);
        defineCarDynamicParams(dt);

        Actor actor = new Actor();
    }

    private void defineCarDynamicParams(float dt)
    {
        WheelSpeed = b2WheelRear.getAngularVelocity();
        BodySpeed = b2CarBody.getLinearVelocity().x;

        if (BodySpeed > Const.CAM_CHANGEDIR_THRESHOLD)
            Direction = MoveDirection.Right;
        else if (BodySpeed < -Const.CAM_CHANGEDIR_THRESHOLD)
            Direction = MoveDirection.Left;
        else
            Direction = MoveDirection.NoMove;
    }

    private void automaticTransmissionUpdate(float dt)
    {
        if (Math.abs(CurrentMotorSpeed - MaxMotorSpeed) <= Const.EPSILON)
            tryGearUp();
        else if (CurrentMotorSpeed < Const.PLAYER_MIN_MOTOR_SPEED)
            tryGearDown();
    }
}
