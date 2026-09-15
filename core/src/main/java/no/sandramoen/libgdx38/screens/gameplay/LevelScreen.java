package no.sandramoen.libgdx38.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import no.sandramoen.libgdx38.actors.*;
import no.sandramoen.libgdx38.actors.broken.Broken;
import no.sandramoen.libgdx38.actors.particles.EffectBurst;
import no.sandramoen.libgdx38.screens.shell.MenuScreen;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.BaseScreen;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;

    private Piece piece_being_moved;
    private Fixable fixable;
    private Broken broken;

    public LevelScreen(Broken broken) {
        this.broken = broken;
        fixable = new Fixable(broken, mainStage);
    }


    @Override
    public void initialize() {
        // audio

        // actors
        background = new Background(mainStage);

        // gui
        initialize_gui();
    }


    @Override
    public void update(float delta) {
        if (piece_being_moved != null)
            move_piece(piece_being_moved);
    }


    private void move_piece(Piece piece) {
        Vector2 mouse_stage_position = mainStage.screenToStageCoordinates(new Vector2(Gdx.input.getX(), Gdx.input.getY()));
        piece.setPosition(
            mouse_stage_position.x - piece.getWidth() / 2,
            mouse_stage_position.y - piece.getHeight() / 2
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

        // fixed complete
        if (fixable.is_fixed()) {
            fixable.remove();

            mainStage.addAction(Actions.sequence(
                Actions.delay(Piece.REMOVE_DURATION),
                Actions.run(() -> BaseGame.setActiveScreen(new MenuScreen()))
            ));

            return super.touchDown(screenX, screenY, pointer, button);
        }

        // pieces
        Actor hit = mainStage.hit(world_position.x, world_position.y, true);
        Piece piece = (Piece) hit;
        if (
            piece_being_moved == null &&
            piece != null && piece.isTouchable()
        ) {
            pickup(piece);
        } else if (piece_being_moved != null && piece != null) {
            glue(piece_being_moved);
        }

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void pickup(Piece piece) {
        // TODO: add pick-up sound

        piece.pick_up();
        piece_being_moved = piece;
    }


    private void glue(Piece piece) {
        // TODO: add glue sound

        piece_being_moved = null;
        piece.glue();

        fixable.add(piece);

        if (fixable.is_fixed()) {
            _set_game_over();
        }
    }


    private void _set_game_over() {
        // audio
        AssetLoader.level_music.pause();
        AssetLoader.fixed_forever_music.setVolume(BaseGame.musicVolume * 1.5f);
        AssetLoader.fixed_forever_music.play();

        // floating animation
        float amount = 0.25f;
        float duration = 2.1f;

        fixable.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f, amount, duration),
            Actions.moveBy(0f, -amount * 2, duration * 2),
            Actions.moveBy(0f, amount, duration)
        )));

        //
        broken.fixed_fixable = fixable;
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
