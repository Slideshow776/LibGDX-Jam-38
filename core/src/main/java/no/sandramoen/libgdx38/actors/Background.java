package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;

import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;

public class Background extends BaseActor {

    public Background(Stage stage) {
        super(0f, 0f, stage);

        setTouchable(Touchable.disabled);

        loadImage("whitePixel");

        setSize(BaseGame.WORLD_WIDTH + 2, BaseGame.WORLD_HEIGHT + 2);
        setPosition(-1, -1);

        setColor(new Color(0xFF1493FF));
    }
}
