package ru.perfectdaemon.bulldozer;

import com.badlogic.gdx.*;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Created by daemon on 07.01.14.
 */
public class GameScreen implements Screen, InputProcessor
{
    private final BulldozerGame game;
    private Stage stage;
    public Level level;
    public Dozer dozer;

    public GameScreen(BulldozerGame game)
    {
        this.game = game;

        OrthographicCamera camera = new OrthographicCamera();
        camera.setToOrtho(true, Const.GAME_WIDTH, Const.GAME_HEIGHT);

        stage = new Stage();
        stage.setCamera(camera);
        Global.load();

        level = new Level(Gdx.files.internal("main/level1.conf"));
        loadDozer();

        Gdx.gl.glClearColor(0 / 255.0f, 30 / 255.0f, 60 / 250.0f, 1.0f);
    }

    private void loadDozer()
    {
        if (dozer != null)
            dozer.dispose();
        dozer = new Dozer(new DozerParams(Gdx.files.internal("main/car.conf")), new Vector2(2.5f, 7.5f));
    }

    @Override
    public void render(float delta)
    {
        Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
        stage.draw();
        Global.debugRenderer.render(Global.world, stage.getCamera().combined);

        Global.batch.setProjectionMatrix(Global.camera.combined);
        Global.batch.begin();
        Assets.font.setScale(0.5f);
        Assets.font.draw(Global.batch, "Привет, world!", 10, 1);
        Global.batch.end();

        stage.act(delta);
        dozer.update(delta);
        Global.world.step(Const.PHYSIC_STEP, Const.PHYSIC_VEL_IT, Const.PHYSIC_POS_IT);
    }

    @Override
    public void resize(int width, int height)
    {
        stage.setViewport(Const.GAME_WIDTH, Const.GAME_HEIGHT, true);
        stage.getCamera().translate(-stage.getGutterWidth(),
                -stage.getGutterHeight(), 0);

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
        Global.dispose();
    }

    @Override
    public boolean keyDown(int keycode)
    {
        if (keycode == Input.Keys.TAB)
            loadDozer();
        return false;
    }

    @Override
    public boolean keyUp(int keycode)
    {
        return false;
    }

    @Override
    public boolean keyTyped(char character)
    {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button)
    {
        return false;
    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button)
    {
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
