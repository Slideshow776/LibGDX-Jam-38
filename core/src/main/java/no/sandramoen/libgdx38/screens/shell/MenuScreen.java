package no.sandramoen.libgdx38.screens.shell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import no.sandramoen.libgdx38.actors.Background;
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
        // audio
        GameUtils.setMusicVolume(0.1f); // TODO: tweak for release/publish
        GameUtils.playLoopingMusic(AssetLoader.level_music);

        // background
        BaseActor background = new BaseActor(0f, 0f, mainStage);
        background.setTouchable(Touchable.disabled);

        background.loadImage("shelf");

        background.setSize(BaseGame.WORLD_WIDTH, BaseGame.WORLD_HEIGHT);
        background.setColor(new Color(0x567560FF));

        // shelves
        /*uiTable.defaults()
            .padTop(Gdx.graphics.getHeight() * .02f)
        ;*/

        Table display_shelf = new Table();

        for (int i = 0; i < 17; i++) {
            if (i % 5 == 0)
                display_shelf.row();

            float shelf_width = 0.17f;
            float shelf_height = 0.175f;
            Image item = new Image(AssetLoader.textureAtlas.findRegion("vases/easy/1/1"));
            item.addListener(new InputListener(){
                @Override
                public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                    BaseGame.setActiveScreen(new LevelScreen());
                    return super.touchDown(event, x, y, pointer, button);
                }
            });
            display_shelf.add(item)
                .width(Gdx.graphics.getWidth() * shelf_width)
                .height(Gdx.graphics.getHeight() * shelf_height)
            ;
        }

        uiTable.add(display_shelf)
            .padLeft(Gdx.graphics.getWidth() * 0.0225f)
            .padTop(Gdx.graphics.getHeight() * -0.15f)
        ;

        display_shelf.setDebug(true);
        uiTable.setDebug(true);
    }


    @Override
    public void update(float delta) {}


    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Keys.ESCAPE || keycode == Keys.Q) {
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
}
