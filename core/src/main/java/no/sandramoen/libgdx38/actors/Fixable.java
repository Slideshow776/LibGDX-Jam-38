package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.GameUtils;

public class Fixable extends BaseActor {
    public Image shelf_image;
    public Array<Piece> pieces;

    private int num_pieces;


    public Fixable(float x, float y, Stage stage, int num_pieces, String image_path) {
        super(x, y, stage);

        this.num_pieces = num_pieces;
        spawn_pieces(image_path);
        pieces = new Array<Piece>();

        //setDebug(true);
    }


    public boolean remove() {
        for (Piece piece : pieces)
            piece.remove();

        addAction(Actions.sequence(
            Actions.delay(Piece.REMOVE_DURATION),
            Actions.run(() -> {
                AssetLoader.fixed_forever_music.stop();
                GameUtils.playLoopingMusic(AssetLoader.level_music);
            }),
            Actions.removeActor()
        ));
        return true; // TODO: prolly bad practice...
    }


    public boolean is_fixed() {
        return pieces.size >= num_pieces;
    }


    public void add(Piece piece) {
        pieces.add(piece);
    }


    private void spawn_pieces(String image_path) {
        for (int i = 0; i < num_pieces; i++) {
            Piece piece = new Piece(getStage(), image_path + i, 2, 2);

            if(!BaseGame.DISABLE_RANDOM) {
                float random = 3f;
                piece.centerAtPosition(
                    getX() + MathUtils.random(-random, random),
                    getY() + MathUtils.random(-random, random)
                );
            }

            getStage().addActor(piece);
        }
    }
}
