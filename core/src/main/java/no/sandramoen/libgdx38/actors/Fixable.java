package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Align;

import no.sandramoen.libgdx38.utils.BaseActor;

public class Fixable extends BaseActor {
    private int num_pieces = 0;
    public static enum DIFFICULTY { EASY, MEDIUM, HARD }


    public Fixable(float x, float y, Stage stage, DIFFICULTY difficulty) {
        super(x, y, stage);

        if (difficulty == DIFFICULTY.EASY) _easy_setup();
        else if (difficulty == DIFFICULTY.MEDIUM) _medium_setup();
        else if (difficulty == DIFFICULTY.HARD) _hard_setup();

        setDebug(true);
    }


    private void _easy_setup() {
        num_pieces = 4;

        for (int i = 0; i < num_pieces; i++) {
            Piece piece = new Piece(getStage(), "vases/easy/0/" + i, 1, 2);

            float range = 3f;
            piece.centerAtPosition(
                MathUtils.random(-range, range),
                MathUtils.random(-range, range)
            );

            addActor(piece);
        }
        //setDebug(true);
    }


    private void _medium_setup() {
        num_pieces = 6;
        System.out.println("TODO: difficulty not created!");
    }


    private void _hard_setup() {
        num_pieces = 8;
        System.out.println("TODO: difficulty not created!");
    }
}
