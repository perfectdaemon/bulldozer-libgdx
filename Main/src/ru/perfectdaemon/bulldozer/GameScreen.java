package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by daemon on 07.01.14.
 */
public class GameScreen implements Screen, InputProcessor
{
    private Rectangle rectGas, rectBrake, rectHandbrake, rectPause, rectReset;

    private final BulldozerGame game;
    private String debug;
    private Stage stage;
    private float camChangeDirTimeout, camDelta;
    private Vector2 camMin, camMax;

    public Level level;
    public Dozer dozer;

    public GameScreen(BulldozerGame game)
    {
        this.game = game;

        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true, Const.GAME_WIDTH, Const.GAME_HEIGHT);
        Gdx.gl.glClearColor(0 / 255.0f, 30 / 255.0f, 60 / 250.0f, 1.0f);
        stage = new Stage();
        stage.setCamera(camera);

        Global.load();
        loadLevel(Gdx.files.internal("main/level1.conf"));
        loadDozer();
    }

    private void loadDozer()
    {
        if (dozer != null)
            dozer.dispose();
        dozer = new Dozer(new DozerParams(Gdx.files.internal("main/car.conf")), new Vector2(2.5f, 7.5f));
    }

    private void loadLevel(FileHandle handle)
    {
        if (level != null)
            level.dispose();
        level = new Level(handle);
        camMin = level.getLevelLimitMin();
        camMax = level.getLevelLimitMax();
    }

    private void loadGui()
    {
        rectGas = new Rectangle(0.75f * Gdx.graphics.getWidth(), 0.2f * Gdx.graphics.getHeight(),
                0.25f * Gdx.graphics.getWidth(), 0.8f * Gdx.graphics.getHeight());
        rectBrake = new Rectangle(0.0f * Gdx.graphics.getWidth(), 0.2f * Gdx.graphics.getHeight(),
                0.25f * Gdx.graphics.getWidth(), 0.8f * Gdx.graphics.getHeight());
        rectHandbrake = new Rectangle(0.25f * Gdx.graphics.getWidth(), 0.75f * Gdx.graphics.getHeight(),
                0.5f * Gdx.graphics.getWidth(), 0.25f * Gdx.graphics.getHeight());
        rectReset = new Rectangle(0.25f * Gdx.graphics.getWidth(), 0.0f * Gdx.graphics.getHeight(),
                0.5f * Gdx.graphics.getWidth(), 0.50f * Gdx.graphics.getHeight());
    }

    private void cameraUpdate(float dt)
    {
        Vector2 camTarget = new Vector2(stage.getCamera().position.x, stage.getCamera().position.y);
        camChangeDirTimeout -= dt;

        if (camChangeDirTimeout <= 0)
        {
            switch (dozer.Direction)
            {
                case NoMove:
                    if (Math.abs(camDelta) > Const.EPSILON)
                    {
                        camDelta = 0;
                        camChangeDirTimeout = Const.CAM_CHANGEDIR_TIMEOUT;
                    }
                    break;
                case Left:
                    if (Math.abs(camDelta + 10) > Const.EPSILON) // != -10
                    {
                        camDelta = -10;
                        camChangeDirTimeout = Const.CAM_CHANGEDIR_TIMEOUT;
                    }
                    break;
                case Right:
                    if (Math.abs(camDelta - 10) > Const.EPSILON)    // != 10
                    {
                        camDelta = 10;
                        camChangeDirTimeout = Const.CAM_CHANGEDIR_TIMEOUT;
                    }
                    break;
            }
        }

        if (Math.abs(camTarget.x - (dozer.b2CarBody.getPosition().x + camDelta)) < Const.EPSILON)
            camTarget.x = dozer.b2CarBody.getPosition().x + camDelta;
        else
            camTarget.x = Global.lerp(camTarget.x, dozer.b2CarBody.getPosition().x + camDelta, Const.CAM_SMOOTH * dt);

        if (Math.abs(camTarget.y - dozer.b2CarBody.getPosition().x) < Const.EPSILON)
            camTarget.y = dozer.b2CarBody.getPosition().y;
        else
            camTarget.y = Global.lerp(camTarget.y, dozer.b2CarBody.getPosition().y, Const.CAM_SMOOTH * dt);

        camTarget = Global.clamp(camTarget, camMin, camMax);

        stage.getCamera().position.x = camTarget.x;
        stage.getCamera().position.y = camTarget.y;
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();
        Global.debugRenderer.render(Global.world, stage.getCamera().combined);

        debug = String.format("gas: %b \nbrake: %b \nhandbrake: %b", dozer.isGas, dozer.isBrake, dozer.isHandbrake);
        Global.batch.setProjectionMatrix(Global.camera.combined);
        Global.batch.begin();
        //Assets.font.setScale(0.5f);
        Assets.font.drawMultiLine(Global.batch, debug, 10, 1);
        Global.batch.end();

        stage.act(delta);
        dozer.update(delta);
        cameraUpdate(delta);
        Global.world.step(Const.PHYSIC_STEP, Const.PHYSIC_VEL_IT, Const.PHYSIC_POS_IT);
    }

    @Override
    public void resize(int width, int height)
    {
        stage.setViewport(Const.GAME_WIDTH, Const.GAME_HEIGHT, true);
        stage.getCamera().translate(-stage.getGutterWidth(),
                -stage.getGutterHeight(), 0);
        loadGui();
        //Global.camera.setToOrtho(true, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Override
    public void show()
    {

    }

    @Override
    public void hide()
    {

    }

    @Override
    public void pause()
    {

    }

    @Override
    public void resume()
    {

    }

    public void dispose()
    {
        dozer.dispose();
        level.dispose();
        Global.dispose();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        switch (keycode)
        {
            case Input.Keys.TAB: loadDozer(); return true;
            case Input.Keys.LEFT: dozer.setBrake(true); return true;
            case Input.Keys.RIGHT: dozer.setGas(true); return true;
            case Input.Keys.SPACE: dozer.setHandBrake(true); return true;
            default: return false;
        }
    }

    @Override
    public boolean keyUp(int keycode)
    {
        switch (keycode)
        {
            case Input.Keys.LEFT: dozer.setBrake(false); return true;
            case Input.Keys.RIGHT: dozer.setGas(false); return true;
            case Input.Keys.SPACE: dozer.setHandBrake(false); return true;
            default: return false;
        }
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        if (rectGas.contains(screenX, screenY))
            dozer.setGas(true);
        if (rectBrake.contains(screenX, screenY))
            dozer.setBrake(true);
        if (rectHandbrake.contains(screenX, screenY))
            dozer.setHandBrake(true);
        if (rectReset.contains(screenX, screenY))
            loadDozer();
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
        if (rectGas.contains(screenX, screenY))
            dozer.setGas(false);
        if (rectBrake.contains(screenX, screenY))
            dozer.setBrake(false);
        if (rectHandbrake.contains(screenX, screenY))
            dozer.setHandBrake(false);
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer)
    {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY)
    {
        return false;
    }

    @Override
    public boolean scrolled(int amount)
    {
        return false;
    }
}
