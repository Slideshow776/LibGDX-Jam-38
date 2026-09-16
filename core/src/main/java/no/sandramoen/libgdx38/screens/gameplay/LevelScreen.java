package no.sandramoen.libgdx38.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;

import com.badlogic.gdx.utils.reflect.ClassReflection;
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

        //_start_game_over_show();

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
                Actions.delay(Fixable.REMOVE_DURATION),
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

        double how_fixed = fixable.rate() * 100.0;
        System.out.println("Fix score for " + ClassReflection.getSimpleName(fixable.broken.getClass()) + ": " + Math.round(how_fixed) + "%");
        // floating animation
        float amount = 0.25f;
        float duration = 2.1f;

        fixable.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f, amount, duration),
            Actions.moveBy(0f, -amount * 2, duration * 2),
            Actions.moveBy(0f, amount, duration)
        )));

        _start_game_over_show();

        //
        broken.fixed_fixable = fixable;
    }


    private void _start_game_over_show() {
        /*BaseActor overlay_show = new BaseActor(0f, 0f, mainStage);
        overlay_show.setTouchable(Touchable.disabled);
        overlay_show.loadImage("whitePixel");
        overlay_show.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);
        overlay_show.setPosition(0f, 0f);
        overlay_show.setColor(new Color(0f, 0f, 0f, 0.5f));
        overlay_show.setZIndex(background.getZIndex() + 1);*/

        BaseActor wheel = new BaseActor(0f, 0f, mainStage);
        wheel.setTouchable(Touchable.disabled);
        wheel.loadImage("wheel");
        wheel.setSize(BaseGame.WORLD_WIDTH * 1.8f, BaseGame.WORLD_WIDTH * 1.8f);
        wheel.setPosition(BaseGame.WORLD_WIDTH / 2 - wheel.getWidth() / 2, 0 - wheel.getHeight());
        wheel.setOrigin(Align.center);
        wheel.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.moveBy(0f, wheel.getHeight() / 8, 1f, Interpolation.bounceOut),
            Actions.forever(Actions.rotateBy(50f, 1f))
        ));
        wheel.setZIndex(background.getZIndex() + 1);

        BaseActor angel_0 = new BaseActor(0f, 0f, mainStage);
        angel_0.setTouchable(Touchable.disabled);
        angel_0.loadImage("angel_trumpet");
        angel_0.setSize(8, 8);
        angel_0.setPosition(BaseGame.WORLD_WIDTH, 0);
        angel_0.setOrigin(Align.center);
        angel_0.setZIndex(background.getZIndex() + 1);

        float trumpet_duration = 0.2125f;
        float scale_to = 1.2f;
        float rotate_to = -5f;
        angel_0.addAction((Actions.sequence(
            Actions.delay(0.5f),
            Actions.moveTo(BaseGame.WORLD_WIDTH - angel_0.getWidth() * 0.6f, 0f, 1f, Interpolation.bounceOut),
            Actions.delay(0.4f),
            Actions.forever(
                Actions.parallel(
                    Actions.sequence(
                        Actions.scaleTo(scale_to, scale_to, trumpet_duration),
                        Actions.scaleTo(1f, 1f, trumpet_duration)
                    ),
                    Actions.sequence(
                        Actions.rotateTo(rotate_to, trumpet_duration),
                        Actions.rotateTo(0f, trumpet_duration)
                    )
                )
        ))));

        BaseActor angel_1 = new BaseActor(0f, 0f, mainStage);
        angel_1.setTouchable(Touchable.disabled);
        angel_1.loadImage("angel_trumpet");
        angel_1.setSize(8, 8);
        angel_1.setPosition(0f - angel_1.getWidth(), 0);
        angel_1.setOrigin(Align.center);
        angel_1.flip();
        angel_1.setZIndex(background.getZIndex() + 1);

        angel_1.addAction((Actions.sequence(
            Actions.delay(0.5f),
            Actions.moveTo(0f - angel_1.getWidth() * 0.4f, 0f, 1f, Interpolation.bounceOut),
            Actions.delay(0.4f),
            Actions.forever(
                Actions.parallel(
                    Actions.sequence(
                        Actions.scaleTo(scale_to, scale_to, trumpet_duration),
                        Actions.scaleTo(1f, 1f, trumpet_duration)
                    ),
                    Actions.sequence(
                        Actions.rotateTo(-rotate_to, trumpet_duration),
                        Actions.rotateTo(0f, trumpet_duration)
                    )
                )
            ))));
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
