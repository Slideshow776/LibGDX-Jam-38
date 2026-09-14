package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx38.utils.BaseActor;

public class Piece extends BaseActor {
    public boolean is_movable = true;
    public float inertia = MathUtils.random(0.025f, 0.1f);

    private Action rotation_action;

    public Piece(Stage stage, String image_path, float width, float height) {
        super(0f, 0f, stage);

        loadImage(image_path);

        setSize(width,height);
        setOrigin(Align.center);

        setBoundaryRectangle(1f);

        float direction = 1;
        if (MathUtils.randomBoolean())
            direction = -1;

        rotation_action = Actions.forever(Actions.rotateBy(
                MathUtils.random(10f, 200f) * direction,
                1f
        ));
        addAction(rotation_action);
    }

    public void stop_rotating() {
        removeAction(rotation_action);
    }
}
