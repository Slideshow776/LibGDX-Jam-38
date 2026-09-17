package no.sandramoen.libgdx38.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx38.actors.Background;
import no.sandramoen.libgdx38.actors.Fixable;
import no.sandramoen.libgdx38.actors.Piece;
import no.sandramoen.libgdx38.actors.broken.BlueThick;
import no.sandramoen.libgdx38.actors.broken.BlueThin;
import no.sandramoen.libgdx38.actors.broken.Broken;
import no.sandramoen.libgdx38.actors.broken.CatMug;
import no.sandramoen.libgdx38.actors.particles.EffectBurst;
import no.sandramoen.libgdx38.screens.gameplay.LevelScreen;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.BaseScreen;
import no.sandramoen.libgdx38.utils.GameUtils;


public class MenuScreen extends BaseScreen {

    private BaseActor overlay;

    @Override
    public void initialize() {
        //
        BaseGame.create_select_cursor();

        // audio
        GameUtils.setMusicVolume(0.4f); // TODO: tweak for release/publish
        GameUtils.playLoopingMusic(AssetLoader.level_music);
        for (int i = 0; i < 10; i++) {
            AssetLoader.wheel_sounds.get(i).stop();
        }

        // overlay
        BaseActor overlay = new Background("whitePixel", uiStage);
        overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        overlay.setColor(Color.BLACK);
        overlay.addAction(Actions.sequence(Actions.fadeOut(0.25f)));

        // background
        BaseActor background = new BaseActor(0f, 0f, mainStage);
        background.setTouchable(Touchable.disabled);

        background.loadImage("shelf");

        background.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);
        background.setColor(new Color(0x567560FF));

        Table display_shelf = new Table();

        for (int i = 0; i < BaseGame.brokens.size; i++) {
            if (i % 5 == 0)
                display_shelf.row();

            float shelf_width = 0.17f;
            float shelf_height = 0.18f;
            Actor item;
            if(BaseGame.brokens.get(i).fixed_fixable == null || !BaseGame.brokens.get(i).fixed_fixable.hasChildren())
                item = new Image(AssetLoader.textureAtlas.findRegion(BaseGame.brokens.get(i).image_path + "/shelf_image/shelf_image"));
            else {
                Fixable fixable = BaseGame.brokens.get(i).fixed_fixable;
                Stack stack = new Stack();
                for(Piece piece : fixable.glued_pieces) {
                    piece.setScale(40);
                    stack.add(piece);
                }
                System.out.println(fixable);
                item = stack;
            }
            int finalI = i;
            item.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    AssetLoader.ceramic_sound.play(BaseGame.soundVolume, MathUtils.random(0.75f, 1.25f), 0f);
                    BaseActor overlay = new Background("whitePixel", uiStage);
                    overlay.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
                    overlay.setColor(Color.BLACK);
                    overlay.setOpacity(0f);
                    overlay.addAction(Actions.sequence(
                        Actions.fadeIn(0.25f),
                        Actions.run(() -> BaseGame.setActiveScreen(new LevelScreen(BaseGame.brokens.get(finalI))))
                    ));
                    return super.touchDown(event, x, y, pointer, button);
                }
            });

            display_shelf.add(item)
                .width(Gdx.graphics.getWidth() * shelf_width)
                .height(Gdx.graphics.getHeight() * shelf_height)
            ;
        }

        uiTable.add(display_shelf)
            .padTop(Gdx.graphics.getHeight() * 0.062f)
            .padRight(Gdx.graphics.getWidth() * 0.065f)
            .padBottom(Gdx.graphics.getHeight() * 0.045f)
            .padLeft(Gdx.graphics.getWidth() * 0.09f)
            .expand()
            .top()
            .left()
        ;

        //display_shelf.setDebug(true);
        //uiTable.setDebug(true);
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
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

        return super.touchDown(screenX, screenY, pointer, button);
    }
}
