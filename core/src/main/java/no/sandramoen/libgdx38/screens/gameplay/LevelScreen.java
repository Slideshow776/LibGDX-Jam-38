package no.sandramoen.libgdx38.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.github.tommyettinger.textra.TextraLabel;

import no.sandramoen.libgdx38.actors.*;
import no.sandramoen.libgdx38.actors.particles.EffectBurst;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.BaseScreen;
import no.sandramoen.libgdx38.utils.GameUtils;
import no.sandramoen.libgdx38.actors.Fixable.DIFFICULTY;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;

    private Piece piece_being_moved = null;

    public LevelScreen() {}


    @Override
    public void initialize() {
        // audio
        GameUtils.setMusicVolume(0.1f); // TODO: tweak for release/publish
        GameUtils.playLoopingMusic(AssetLoader.level_music);

        // actors
        background = new Background(mainStage);

        /*Fixable test_vase = new Fixable(
            BaseGame.WORLD_WIDTH / 2,
            BaseGame.WORLD_HEIGHT / 2,
            mainStage,
            DIFFICULTY.EASY
        );*/

        float num_pieces = 4;
        for (int i = 0; i < num_pieces; i++) {
            Piece piece = new Piece(mainStage, "vases/easy/0/" + i, 1, 2);

            float range = 3f;
            piece.centerAtPosition(
                BaseGame.WORLD_WIDTH / 2 + MathUtils.random(-range, range),
                BaseGame.WORLD_HEIGHT / 2 + MathUtils.random(-range, range)
            );

            mainStage.addActor(piece);
        }

        initialize_gui();

        // Gdx.input.setCursorCatched(true);
    }


    @Override
    public void update(float delta) {
        if (piece_being_moved != null)
            move_piece(piece_being_moved);
    }


    private void move_piece(Piece piece) {
        Vector2 mouse_stage_position = mainStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        piece.addAction(
            Actions.moveTo(
                mouse_stage_position.x - piece.getWidth() / 2,
                mouse_stage_position.y - piece.getHeight() / 2,
                piece.inertia,
                Interpolation.bounceOut
            )
        );
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

        Actor hit = mainStage.hit(world_position.x, world_position.y, true);
        /*if (!(hit instanceof Piece) || !((Piece) hit).is_movable)
            return super.touchDown(screenX, screenY, pointer, button);*/

        Piece piece = (Piece) hit;
        if (
            piece_being_moved == null &&
            piece != null && piece.is_movable
        ) {
            pickup(piece);
        } else if (piece_being_moved != null) {
            glue(piece_being_moved);
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }

    private void pickup(Piece piece) {
        // TODO: add pick-up sound

        piece_being_moved = piece;

    }

    private void glue(Piece piece) {
        // TODO: add glue sound

        piece_being_moved = null;

        piece.is_movable = false;
        piece.stop_rotating();
    }


    private void initialize_gui() {
        // resources setup

        //score_label = new TextraLabel("test", AssetLoader.getLabelStyle("Play-Bold20white"));

        // ui setup
        uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;

        uiTable.add()
            .padTop(Gdx.graphics.getHeight() * .1f)
            .row()
        ;

        //uiTable.add(score_label);
        //uiTable.setDebug(true);
    }
}
