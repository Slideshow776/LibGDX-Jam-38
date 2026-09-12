package no.sandramoen.libgdx38.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx38.actors.*;
import no.sandramoen.libgdx38.actors.particles.EffectBurst;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;

    private TextraLabel score_label;
    /*private BaseProgressBar life_bar;
    private BaseProgressBar fulfillment_bar;*/

    public LevelScreen() {}


    @Override
    public void initialize() {
        // audio
        //AssetLoader.dividerMusic.setVolume(BaseGame.soundVolume);

        // actors
        background = new Background(mainStage);

        initialize_gui();

        // Gdx.input.setCursorCatched(true);
    }


    @Override
    public void update(float delta) {

    }


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.ESCAPE || keycode == Input.Keys.Q) {
            Gdx.app.exit();
        }
        return super.keyDown(keycode);
    }


    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        Vector2 world_position = mainStage.screenToStageCoordinates(new Vector2(screenX, screenY));

        // particle effect
        EffectBurst effect = new EffectBurst();
        effect.setPosition(world_position.x, world_position.y);
        effect.setScale(0.00125f);
        mainStage.addActor(effect);
        effect.start();

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void initialize_gui() {
        // resources setup
        /*life_bar = new BaseProgressBar(Gdx.graphics.getWidth() * .0325f, Gdx.graphics.getHeight() * 0.9725f, uiStage);
        life_bar.setProgress(100);
        life_bar.set_color(Color.FIREBRICK);
        life_bar.setProgressBarColor(Color.PINK);
        uiStage.addActor(life_bar);

        fulfillment_bar = new BaseProgressBar(Gdx.graphics.getWidth() * .0325f, Gdx.graphics.getHeight() * 0.0555f, uiStage);
        fulfillment_bar.setProgress(0);
        fulfillment_bar.set_color(Color.BROWN);
        fulfillment_bar.setProgressBarColor(Color.GOLD);
        uiStage.addActor(fulfillment_bar);*/

        // ui setup
        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add()
            .padTop(Gdx.graphics.getHeight() * .1f)
            .row()
        ;

        //uiTable.setDebug(true);
    }
}
