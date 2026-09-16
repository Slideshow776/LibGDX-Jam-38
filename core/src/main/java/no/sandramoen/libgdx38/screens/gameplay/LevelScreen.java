package no.sandramoen.libgdx38.screens.gameplay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
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
import no.sandramoen.libgdx38.gui.BaseProgressBar;
import no.sandramoen.libgdx38.gui.BaseSlider;
import no.sandramoen.libgdx38.screens.shell.MenuScreen;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.BaseScreen;
import no.sandramoen.libgdx38.utils.GameUtils;

public class LevelScreen extends BaseScreen {

    private BaseActor overlay;
    private Background background;

    private Piece piece_being_moved;
    private Fixable fixable;
    private Broken broken;

    private BaseProgressBar score_bar;


    public LevelScreen(Broken broken) {
        this.broken = broken;
        fixable = new Fixable(broken, mainStage);

        background = new Background(broken.image_path + "/background/background", mainStage);
        background.setZIndex(0);
        background.setWorldBounds(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);
        //background.setWorldBounds(background);
    }


    @Override
    public void initialize() {
        // audio

        // actors
        BaseActor overlay = new Background("whitePixel", uiStage);
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.setColor(Color.BLACK);
        overlay.addAction(Actions.sequence(Actions.fadeOut(0.25f)));

        //_start_game_over_show();

        // gui
        initialize_gui();
        BaseGame.create_glue_cursor();
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
        } else if (keycode == Input.Keys.S) {
            BaseGame.isGameOverShowEnabled = !BaseGame.isGameOverShowEnabled;
            if (BaseGame.isGameOverShowEnabled)
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(1.1f, 1.3f), 0f);
            else
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(0.5f, 0.7f), 0f);
            System.out.println("isGameOverShowEnabled: " + BaseGame.isGameOverShowEnabled);
        } else if (keycode == Input.Keys.C) {
            BaseGame.isCameraShakeEnabled = !BaseGame.isCameraShakeEnabled;
            if (BaseGame.isCameraShakeEnabled)
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(1.1f, 1.3f), 0f);
            else
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(0.5f, 0.7f), 0f);
            System.out.println("isCameraShakeEnabled: " + BaseGame.isCameraShakeEnabled);
        } else if (keycode == Input.Keys.B) {
            BaseGame.isScoreBarEnabled = !BaseGame.isScoreBarEnabled;
            if (BaseGame.isScoreBarEnabled)
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(1.1f, 1.3f), 0f);
            else
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(0.5f, 0.7f), 0f);
            System.out.println("isScoreBarEnabled: " + BaseGame.isScoreBarEnabled);
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

        // audio
        AssetLoader.glue_sounds.get(MathUtils.random(0, AssetLoader.glue_sounds.size - 1)).play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);

        // fixed complete
        if (fixable.is_fixed()) {
            fixable.remove();

            BaseActor overlay = new Background("whitePixel", uiStage);
            overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
            overlay.setColor(Color.BLACK);
            overlay.setOpacity(0f);

            mainStage.addAction(Actions.sequence(
                Actions.parallel(
                    Actions.delay(Fixable.REMOVE_DURATION),
                    Actions.run(() -> overlay.addAction(Actions.fadeIn(Fixable.REMOVE_DURATION)))
                ),
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
        AssetLoader.ceramic_sounds.get(MathUtils.random(0, AssetLoader.ceramic_sounds.size - 1)).play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);

        piece.pick_up();
        piece_being_moved = piece;
    }


    private void glue(Piece piece) {
        AssetLoader.ceramic_sounds.get(MathUtils.random(0, AssetLoader.ceramic_sounds.size - 1)).play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);

        piece_being_moved = null;
        piece.glue();

        fixable.add(piece);

        if (fixable.is_fixed()) {
            _set_game_over();
        }
    }


    private void _set_game_over() {
        // score bar
        if (BaseGame.isScoreBarEnabled) {
            //double how_fixed = fixable.rate() * 100.0;
            //System.out.println("Fix score for " + ClassReflection.getSimpleName(fixable.broken.getClass()) + ": " + Math.round(how_fixed) + "%");
            score_bar.addAction(Actions.sequence(
                Actions.delay(1.5f),
                Actions.fadeIn(0.75f, Interpolation.bounceOut),
                Actions.run(() -> {
                    double how_fixed = fixable.rate() * 100.0;
                    score_bar.animateProgress((int) Math.round(how_fixed));
                    AssetLoader.wheel_sounds.get(MathUtils.round((float) how_fixed / 10f) - 1).play(BaseGame.soundVolume, MathUtils.random(0.9f, 1.1f), 0f);
                    //System.out.println("wheel sound: " + MathUtils.round((float) how_fixed / 10f));
                })
            ));
        }

        // floating animation
        float amount = 0.25f;
        float duration = 2.1f;
        fixable.addAction(Actions.forever(Actions.sequence(
            Actions.moveBy(0f, amount, duration),
            Actions.moveBy(0f, -amount * 2, duration * 2),
            Actions.moveBy(0f, amount, duration)
        )));
        broken.fixed_fixable = fixable;

        // show
        _start_game_over_show();
    }


    private void _start_game_over_show() {
        if (!BaseGame.isGameOverShowEnabled)
            return;

        float singing_start_delay = 2.1f;
        float beat_speed = 60f / 131.9f;

        // click sound for testing
        /*background.addAction(Actions.sequence(
            Actions.delay(singing_start_delay),
            Actions.forever(Actions.sequence(
                Actions.run(() -> AssetLoader.click_sound.play(1f)),
                Actions.delay(beat_speed)
        ))));*/

        // camera shake
        background.shakyCamIntensity = 0.0025f;
        background.addAction(Actions.sequence(
            Actions.delay(singing_start_delay),
            Actions.forever(Actions.sequence(
                Actions.run(() -> background.isShakyCam = true),
                Actions.delay(beat_speed * 0.5f),
                Actions.run(() -> {
                    //background.isShakyCam = false;
                    EffectBurst effect = new EffectBurst();
                    effect.setPosition(
                        MathUtils.random(0f, BaseGame.WORLD_WIDTH),
                        MathUtils.random(0f, BaseGame.WORLD_HEIGHT)
                    );
                    effect.setScale(0.005f);
                    effect.setColor(GameUtils.randomLightColor());
                    mainStage.addActor(effect);
                    effect.start();
                }),
                Actions.delay(beat_speed * 0.5f)
        ))));

        // audio
        AssetLoader.level_music.pause();
        AssetLoader.fixed_forever_music.setVolume(BaseGame.musicVolume * 1.5f);
        AssetLoader.fixed_forever_music.play();

        // animation

        //overlay
        /*BaseActor overlay_show = new BaseActor(0f, 0f, mainStage);
        overlay_show.setTouchable(Touchable.disabled);
        overlay_show.loadImage("whitePixel");
        overlay_show.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);
        overlay_show.setPosition(0f, 0f);
        overlay_show.setColor(new Color(0f, 0f, 0f, 0.5f));
        overlay_show.setZIndex(background.getZIndex() + 1);*/

        // wheel
        float wheel_direction = 1f;
        if (MathUtils.randomBoolean())
            wheel_direction *= -1;
        BaseActor wheel = new BaseActor(0f, 0f, mainStage);
        wheel.setTouchable(Touchable.disabled);
        wheel.loadImage("wheel");
        wheel.setSize(BaseGame.WORLD_WIDTH * 1.8f, BaseGame.WORLD_WIDTH * 1.8f);
        wheel.setPosition(BaseGame.WORLD_WIDTH / 2 - wheel.getWidth() / 2, 0 - wheel.getHeight());
        wheel.setOrigin(Align.center);
        wheel.addAction(Actions.sequence(
            Actions.delay(0.5f),
            Actions.moveBy(0f, wheel.getHeight() / 8.5f, 1f, Interpolation.bounceOut),
            Actions.forever(Actions.rotateBy(50f * wheel_direction, 1f))
        ));
        wheel.setZIndex(background.getZIndex() + 1);

        // angles
        BaseActor angel_0 = new BaseActor(0f, 0f, mainStage);
        angel_0.setTouchable(Touchable.disabled);
        angel_0.loadImage("angel_trumpet");
        angel_0.setSize(8, 8);
        angel_0.setPosition(BaseGame.WORLD_WIDTH, 0);
        angel_0.setOrigin(Align.center);
        angel_0.setZIndex(background.getZIndex() + 1);

        float scale_to = 1.2f;
        float rotate_to = -5f;
        angel_0.addAction((Actions.sequence(
            Actions.delay(singing_start_delay * 0.25f),
            Actions.moveTo(BaseGame.WORLD_WIDTH - angel_0.getWidth() * 0.6f, 0f, singing_start_delay * 0.5f, Interpolation.bounceOut),
            Actions.delay(singing_start_delay * 0.25f),
            Actions.forever(
                Actions.parallel(
                    Actions.sequence(
                        Actions.scaleTo(scale_to, scale_to, beat_speed / 2f),
                        Actions.scaleTo(1f, 1f, beat_speed / 2f)
                    ),
                    Actions.sequence(
                        Actions.rotateTo(rotate_to, beat_speed / 2f),
                        Actions.rotateTo(0f, beat_speed / 2f)
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
            Actions.delay(singing_start_delay * 0.25f),
            Actions.moveTo(0f - angel_1.getWidth() * 0.4f, 0f, singing_start_delay * 0.5f, Interpolation.bounceOut),
            Actions.delay(singing_start_delay * 0.25f),
            Actions.forever(
                Actions.parallel(
                    Actions.sequence(
                        Actions.scaleTo(scale_to, scale_to, beat_speed / 2f),
                        Actions.scaleTo(1f, 1f, beat_speed / 2f)
                    ),
                    Actions.sequence(
                        Actions.rotateTo(-rotate_to, beat_speed / 2f),
                        Actions.rotateTo(0f, beat_speed / 2f)
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

        score_bar = new BaseProgressBar(0f, Gdx.graphics.getHeight() * 0.975f, uiStage);
        score_bar.setOpacity(0f);

        //uiTable.add(score_label);
        //uiTable.setDebug(true);
    }
}
