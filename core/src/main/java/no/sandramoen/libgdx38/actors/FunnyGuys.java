package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.actions.AddAction;
import com.badlogic.gdx.scenes.scene2d.actions.RepeatAction;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx38.utils.BaseActor;

public class FunnyGuys extends BaseActor {


    public FunnyGuys(float x, float y, Stage stage, int z_index, boolean is_right) {
        super(x, y, stage);

        setTouchable(Touchable.disabled);

        float scale = 1;
        float random = MathUtils.random();
        if (random <= 1 / 5f) {
            loadImage("funny_guys/funny guy 0");
            setSize(
                2f * scale * MathUtils.random(0.95f, 1.05f),
                2.2f * scale * MathUtils.random(0.95f, 1.05f)
            );
        } else if (random <= 2 / 5f) {
            loadImage("funny_guys/funny guy 1");
            setSize(
                4f * scale * MathUtils.random(0.95f, 1.05f),
                2f * scale * MathUtils.random(0.95f, 1.05f)
            );
        } else if (random <= 3 / 5f) {
            loadImage("funny_guys/funny guy 2");
            setSize(
                1.8f * scale * MathUtils.random(0.95f, 1.05f),
                1.6f * scale * MathUtils.random(0.95f, 1.05f)
            );
        } else if (random <= 4 / 5f) {
            loadImage("funny_guys/funny guy 3");
            setSize(
                1.4f * scale * MathUtils.random(0.95f, 1.05f),
                1.9f * scale * MathUtils.random(0.95f, 1.05f)
            );
        } else {
            loadImage("funny_guys/funny guy 4");
            setSize(
                1.75f * scale * MathUtils.random(0.95f, 1.05f),
                2.5f * scale * MathUtils.random(0.95f, 1.05f)
            );
        }

        setOrigin(Align.center);
        setZIndex(z_index);
        if (is_right)
            flip();
    }


    public void start_sing_animation(float beat_speed) {
        float scale_to = 1.2f;
        float rotate_to = -5f;
        addAction(Actions.forever(
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
        ));
    }
}
