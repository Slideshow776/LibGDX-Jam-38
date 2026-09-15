package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntArray;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.GameUtils;

import java.util.Arrays;

public class Fixable extends BaseActor {
    public Image shelf_image;
    public Array<Piece> pieces;

    private int num_pieces;


    public Fixable(float x, float y, Stage stage, int num_pieces, String image_path) {
        super(x, y, stage);

        this.num_pieces = num_pieces;
//        spawn_pieces(image_path);
        break_into_pieces(Gdx.files.internal("images/included/test_vase.png"));
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

    private void break_into_pieces(FileHandle file_handle) {
        if (!file_handle.exists()) {
            Gdx.app.error(getClass().getSimpleName(), "Error: Texture doesn't exist. Are you sure the image '" + file_handle + "' is present?");
        }
        // For now, this only breaks into 4 pieces.
        this.num_pieces = 4;

        Pixmap original = new Pixmap(file_handle);

        Pixmap[] portions = new Pixmap[4];
        for (int i = 0; i < 4; i++) {
            portions[i] = new Pixmap(original.getWidth(), original.getHeight(), original.getFormat());
            portions[i].setBlending(Pixmap.Blending.None);
        }
        portions[0].drawPixmap(original, 0, 0);
        portions[1].drawPixmap(original, 0, 0);

        IntArray moves = new IntArray(original.getHeight());

        int thirdAcross = original.getHeight() / 3;
        moves.setSize(original.getHeight()); // pads end with zeros
        Arrays.fill(moves.items, 0, thirdAcross, 1);
        Arrays.fill(moves.items, thirdAcross, thirdAcross + thirdAcross, -1);
        moves.shuffle();

        int start = original.getWidth() / 2 + MathUtils.random(-original.getWidth() / 12, original.getWidth() / 12);
        for (int y = 0; y < original.getHeight(); y++) {
            start += moves.get(y);
            for (int x = 0; x < start; x++) {
                portions[0].drawPixel(x, y, 0);
            }
            for (int x = start; x < original.getWidth(); x++) {
                portions[1].drawPixel(x, y, 0);
            }
        }

        portions[2].drawPixmap(portions[0], 0, 0);
        portions[3].drawPixmap(portions[1], 0, 0);

        moves = new IntArray(original.getWidth());
        thirdAcross = original.getWidth() / 3;
        moves.setSize(original.getWidth()); // pads end with zeros
        Arrays.fill(moves.items, 0, thirdAcross, 1);
        Arrays.fill(moves.items, thirdAcross, thirdAcross + thirdAcross, -1);
        moves.shuffle();

        start = original.getHeight() / 2 + MathUtils.random(-original.getHeight() / 12, original.getHeight() / 12);
        for (int x = 0; x < original.getWidth(); x++) {
            start += moves.get(x);
            for (int y = 0; y < start; y++) {
                portions[0].drawPixel(x, y, 0);
            }
            for (int y = start; y < original.getHeight(); y++) {
                portions[2].drawPixel(x, y, 0);
            }
        }

        moves.shuffle();

        start = original.getHeight() / 2 + MathUtils.random(-original.getHeight() / 12, original.getHeight() / 12);
        for (int x = 0; x < original.getWidth(); x++) {
            start += moves.get(x);
            for (int y = 0; y < start; y++) {
                portions[1].drawPixel(x, y, 0);
            }
            for (int y = start; y < original.getHeight(); y++) {
                portions[3].drawPixel(x, y, 0);
            }
        }

        for (int i = 0; i < 4; i++) {
            Piece piece = new Piece(getStage(), portions[i]);

            if (!BaseGame.DISABLE_RANDOM) {
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
