package no.sandramoen.libgdx38.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.*;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import com.badlogic.gdx.utils.Align;
import no.sandramoen.libgdx38.actors.Background;
import no.sandramoen.libgdx38.actors.DisplayShelfImage;
import no.sandramoen.libgdx38.actors.Fixable;
import no.sandramoen.libgdx38.actors.broken.Broken;
import no.sandramoen.libgdx38.actors.particles.EffectBurst;
import no.sandramoen.libgdx38.actors.particles.EffectHolyFire;
import no.sandramoen.libgdx38.actors.particles.EffectHolyFireNoGravity;
import no.sandramoen.libgdx38.actors.particles.ParticleActor;
import no.sandramoen.libgdx38.screens.gameplay.LevelScreen;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.BaseScreen;
import no.sandramoen.libgdx38.utils.GameUtils;


public class MenuScreen extends BaseScreen {

    private BaseActor overlay;
    private ParticleActor effect;

    @Override
    public void initialize() {
        //
        BaseGame.create_select_cursor();

        // audio
        GameUtils.setMusicVolume(0.4f); // TODO: tweak for release/publish
        GameUtils.playLoopingMusic(AssetLoader.level_music);
        for (int i = 0; i < 10; i++)
            AssetLoader.wheel_sounds.get(i).stop();

        // overlay
        overlay = new Background("whitePixel", uiStage);
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.setColor(Color.BLACK);
        overlay.addAction(Actions.sequence(Actions.fadeOut(0.25f)));

        // background
        BaseActor background = new BaseActor(0f, 0f, mainStage);
        background.setTouchable(Touchable.disabled);
        background.loadImage("Shelf Background 960x960");
        background.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);

        // shelf
        BaseActor shelf = new BaseActor(0f, -0.75f, mainStage);
        shelf.setTouchable(Touchable.disabled);
        shelf.loadImage("Shelf");
        shelf.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT * 1.15f);

        // display shelf
        Table display_shelf = new Table();
        for (int i = 0; i < BaseGame.brokens.size; i++) {
            if (i % 3 == 0)
                display_shelf.row();

            Broken broken = BaseGame.brokens.get(i);
            Actor item;
            if(broken.fixed_fixable == null || !broken.fixed_fixable.hasChildren()) {
                item = new DisplayShelfImage(broken.image_path + "/" + "shelf_image/shelf_image", broken.particleActor);
//                item = new DisplayShelfImage(broken.image_path + "/" + MathUtils.random(0, broken.num_pieces - 1));
                item.setScale(0.7f);
            } else {
                Fixable fixable = broken.fixed_fixable;
                fixable.clearActions();
                // if the actions are cleared, the action that changes the music also gets wiped.
                // we need to stop at least the shrinking-to-nothing action.
                AssetLoader.beethoven_ode_to_joy_music.stop();
                AssetLoader.firework_ambiant_music.stop();
                GameUtils.playLoopingMusic(AssetLoader.level_music);

                fixable.setScale(50);
                fixable.setSize(uiStage.getWidth() / 3f, uiStage.getHeight() / 2f);
                item = new Container<Fixable>(fixable).padBottom(-40);
            }
            int finalI1 = i;
            item.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    _go_to_level_screen(finalI1);
                    return super.touchDown(event, x, y, pointer, button);
                }
            });

            item.addListener(new InputListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    if (item instanceof DisplayShelfImage) {
                        ((DisplayShelfImage) item).is_glow_enabled = true;
                        ((DisplayShelfImage) item).ceramic_sound.play(BaseGame.soundVolume, ((DisplayShelfImage) item).ceramic_sound_pitch + MathUtils.random(-0.1f, 0.1f), 0f);
                        ((DisplayShelfImage) item).addAction(Actions.scaleTo(0.8f, 0.8f, 0.25f, Interpolation.circleOut));
                    }
                    effect = broken.particleActor;

                    Vector2 position = item.localToStageCoordinates(new Vector2(item.getWidth() / 2f, item.getHeight() / 2f));

                    effect.setPosition(
                        position.x - effect.getWidth() / 2f,
                        position.y - effect.getHeight() / 2f
                    );

                    effect.setScale(0.75f);
                    uiStage.addActor(effect);
                    effect.setZIndex(0);
                    effect.start();
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    effect.stop();
                    if (item instanceof DisplayShelfImage) {
                        ((DisplayShelfImage) item).is_glow_enabled = false;
                        ((DisplayShelfImage) item).addAction(Actions.scaleTo(0.7f, 0.7f, 0.25f, Interpolation.circleOut));
                    }
                }
            });

            display_shelf.add(item)
                .width(Gdx.graphics.getWidth() * 0.85f / 3f)
                .height(Gdx.graphics.getHeight() * 0.5f / 2f)
                .spaceBottom(Gdx.graphics.getHeight() * 0.1f)
//                .spaceTop(Gdx.graphics.getHeight() * 0.02f)
            ;
        }

        uiTable.add(display_shelf)
            .padTop(Gdx.graphics.getHeight() * 0.25f)
            .padRight(Gdx.graphics.getWidth() * 0.08f)
            .padBottom(Gdx.graphics.getHeight() * 0.2f)
            .padLeft(Gdx.graphics.getWidth() * 0.07f)
            .expand()
        ;

        for(Actor item : display_shelf.getChildren()){
            item.setOrigin(Gdx.graphics.getWidth() * 0.94f / 3f * 0.5f, Gdx.graphics.getHeight() * 0.7f / 2f * 0.5f);
        }

        if(BaseGame.DEBUG) uiTable.debugAll();
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
            Gdx.app.exit();
        } else if (keycode == Keys.S) {
            BaseGame.isGameOverShowEnabled = !BaseGame.isGameOverShowEnabled;
            if (BaseGame.isGameOverShowEnabled)
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(1.1f, 1.3f), 0f);
            else
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(0.5f, 0.7f), 0f);
            System.out.println("isGameOverShowEnabled: " + BaseGame.isGameOverShowEnabled);
        } else if (keycode == Keys.C) {
            BaseGame.isCameraShakeEnabled = !BaseGame.isCameraShakeEnabled;
            if (BaseGame.isCameraShakeEnabled)
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(1.1f, 1.3f), 0f);
            else
                AssetLoader.click_sound.play(BaseGame.soundVolume, MathUtils.random(0.5f, 0.7f), 0f);
            System.out.println("isCameraShakeEnabled: " + BaseGame.isCameraShakeEnabled);
        } else if (keycode == Keys.B) {
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

        overlay.setOpacity(0f);

        return super.touchDown(screenX, screenY, pointer, button);
    }


    private void _go_to_level_screen(int index) {
        AssetLoader.ceramic_sound.play(BaseGame.soundVolume, MathUtils.random(0.75f, 1.25f), 0f);
        BaseActor overlay = new Background("whitePixel", uiStage);
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.setColor(Color.BLACK);
        overlay.setOpacity(0f);
        overlay.addAction(Actions.sequence(
            Actions.fadeIn(0.25f),
            Actions.run(() -> BaseGame.setActiveScreen(new LevelScreen(BaseGame.brokens.get(index))))
        ));
    }
}
