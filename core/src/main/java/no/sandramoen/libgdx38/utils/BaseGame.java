package no.sandramoen.libgdx38.utils;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.assets.AssetDescriptor;
import com.badlogic.gdx.assets.AssetErrorListener;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx38.actors.broken.BlueThick;
import no.sandramoen.libgdx38.actors.broken.BlueThin;
import no.sandramoen.libgdx38.actors.broken.Broken;
import no.sandramoen.libgdx38.actors.broken.CatMug;
import no.sandramoen.libgdx38.screens.gameplay.LevelScreen;


public abstract class BaseGame extends Game implements AssetErrorListener {

    private static BaseGame game;
    public static AssetManager assetManager;

    // game assets
    public static LevelScreen levelScreen;

    // game state
    public static Preferences preferences;
    public static boolean loadPersonalParameters;
    public static boolean isCustomShadersEnabled = true;
    public static boolean isCameraShakeEnabled = true;
    public static boolean isScoreBarEnabled = false;
    public static boolean isGameOverShowEnabled = true;
    public static boolean isHideUI = false;
    public static float voiceVolume = 1f;
    public static float soundVolume = 1.0f;
    public static float musicVolume = 0.75f;
    public static float vibrationStrength = 1f;
    public static final float UNIT_SCALE = 1 / 16f;
    public static final float WORLD_WIDTH = 16f;
    public static final float WORLD_HEIGHT = 9f;
    public static final float MOVEMENT_THRESHOLD = 0.4f;
    public static final boolean DISABLE_RANDOM = false; // Should be true only when testing the fully-fixed state.

    /**
     * Single point of configuration to enable all debug drawing.
     */
    public static final boolean DEBUG = true;

    public static Array<Broken> brokens;


    public BaseGame() {
        game = this;
    }

    public void create() {
        Gdx.input.setInputProcessor(new InputMultiplexer());
        loadGameState();
        new no.sandramoen.libgdx38.utils.AssetLoader();

        brokens = new Array<Broken>();
        brokens.add(new CatMug());
        brokens.add(new BlueThick());
        brokens.add(new BlueThin());
        brokens.add(new CatMug());
        brokens.add(new CatMug());
        brokens.add(new CatMug());

    }

    public static void setActiveScreen(no.sandramoen.libgdx38.utils.BaseScreen screen) {
        game.setScreen(screen);
    }

    @Override
    public void dispose() {
        super.dispose();
        try {
            assetManager.dispose();
        } catch (Error error) {
            Gdx.app.error(this.getClass().getSimpleName(), error.toString());
        }
    }

    public void error(AssetDescriptor asset, Throwable throwable) {
        Gdx.app.error(this.getClass().getSimpleName(), "Could not load asset: " + asset.fileName, throwable);
    }

    private void loadGameState() {
        no.sandramoen.libgdx38.utils.GameUtils.loadGameState();
        if (!loadPersonalParameters) {
            soundVolume = .75f;
            musicVolume = .5f;
            voiceVolume = 1f;
        }
    }


    public static void create_glue_cursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("images/excluded/cursor glue.png"));
        //pixmap.setFilter(Pixmap.Filter.BiLinear);
        int xHotspot = 1, yHotspot = 46;
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        pixmap.dispose();
        Gdx.graphics.setCursor(cursor);
    }


    public static void create_select_cursor() {
        Pixmap pixmap = new Pixmap(Gdx.files.internal("images/excluded/cursor tool.png"));
        //pixmap.setFilter(Pixmap.Filter.BiLinear);
        int xHotspot = 10, yHotspot = 1;
        Cursor cursor = Gdx.graphics.newCursor(pixmap, xHotspot, yHotspot);
        pixmap.dispose();
        Gdx.graphics.setCursor(cursor);
    }
}
