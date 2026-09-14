package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx38.utils.BaseActor;

public class Piece extends BaseActor {
    public static final float REMOVE_DURATION = 1f;

    public float inertia = MathUtils.random(0.025f, 0.1f);

    private Action rotation_action;


    public Piece(Stage stage, String image_path, float width, float height) {
        super(0f, 0f, stage);

        loadImage(image_path);

        //setSize(width,height);
        setSize(getWidth() * 0.05f, getHeight() * 0.05f);
        setOrigin(Align.center);

        setBoundaryRectangle(1f);

        float direction = 1;
        if (MathUtils.randomBoolean())
            direction = -1;

        rotation_action = Actions.forever(Actions.rotateBy(
                MathUtils.random(20f, 200f) * direction,
                1f
        ));
        addAction(rotation_action);
    }


    @Override
    public boolean remove() {
        addAction(Actions.sequence(
            Actions.scaleTo(0f, 0f, REMOVE_DURATION),
            Actions.removeActor()
        ));
        return true; // TODO: prolly bad practice...
    }


    public void pick_up() {
        addAction(Actions.sequence(
                Actions.scaleTo(1.2f, 1.2f, 0.1f),
                Actions.scaleTo(1.0f, 1.0f, 0.25f)
        ));
    }


    public void glue() {
        addAction(Actions.sequence(
                Actions.scaleTo(0.9f, 0.9f, 0.125f),
                Actions.scaleTo(1.0f, 1.0f, 0.5f)
        ));

        setTouchable(Touchable.disabled);
        stop_rotating();
    }


    public void stop_rotating() {
        removeAction(rotation_action);
    }
}
