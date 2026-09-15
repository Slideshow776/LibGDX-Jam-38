package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Array;

import com.badlogic.gdx.utils.IntArray;

import no.sandramoen.libgdx38.actors.broken.Broken;
import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.BaseActor;
import no.sandramoen.libgdx38.utils.BaseGame;
import no.sandramoen.libgdx38.utils.GameUtils;

import java.util.Arrays;

public class Fixable extends BaseActor {
    public Array<Piece> glued_pieces;

    private Broken broken;


    public Fixable(Broken broken, Stage stage) {
        super(0f, 0f, stage);
        this.broken = broken;

        setSize(0.05f, 0.05f);
        centerAtPosition(BaseGame.WORLD_WIDTH / 2, BaseGame.WORLD_HEIGHT / 2);

        spawn_pieces();
        //break_into_pieces(Gdx.files.internal("images/included/test_vase.png"));
        glued_pieces = new Array<Piece>();

        //setDebug(true);
    }


    public boolean remove() {
        for (Piece piece : glued_pieces)
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
        return glued_pieces.size >= broken.num_pieces;
    }


    public void add(Piece piece) {
        glued_pieces.add(piece);
    }


    private void spawn_pieces() {

        System.out.println(broken);

        for (int i = 0; i < broken.num_pieces; i++) {
            Piece piece = new Piece(getStage(), broken.image_path + "/" + i, 2, 2);

            if(!BaseGame.DISABLE_RANDOM) {
                float random_x = 6f;
                float random_y = 3f;
                piece.centerAtPosition(
                    getX() + MathUtils.random(-random_x, random_x),
                    getY() + MathUtils.random(-random_y, random_y)
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
        broken.num_pieces = 4;

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
        for (int y = 1; y < original.getHeight() - 1; y++) {
            start = Math.min(Math.max(start + moves.get(y), 1), original.getWidth() - 1);
            for (int x = 1; x < start - 1; x++) {
                portions[0].drawPixel(x, y, 0);
            }
            for (int x = start + 1; x < original.getWidth() - 1; x++) {
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
            start = Math.min(Math.max(start + moves.get(x), 1), original.getHeight() - 1);
            for (int y = 1; y < start - 1; y++) {
                portions[0].drawPixel(x, y, 0);
            }
            for (int y = start + 1; y < original.getHeight() - 1; y++) {
                portions[2].drawPixel(x, y, 0);
            }
        }

        moves.shuffle();

        start = original.getHeight() / 2 + MathUtils.random(-original.getHeight() / 12, original.getHeight() / 12);
        for (int x = 0; x < original.getWidth(); x++) {
            start = Math.min(Math.max(start + moves.get(x), 1), original.getHeight() - 1);
            for (int y = 1; y < start - 1; y++) {
                portions[1].drawPixel(x, y, 0);
            }
            for (int y = start + 1; y < original.getHeight() - 1; y++) {
                portions[3].drawPixel(x, y, 0);
            }
        }

        for (int i = 0; i < 4; i++) {
            PER_ROW:
            for (int y = 1; y < original.getHeight() - 1; y++) {
                for (int x = 1; x < original.getWidth() - 1; x++) {
                    if ((portions[i].getPixel(x, y) & 0xFF) == 0xFF) {
                        if ((portions[i].getPixel(x + 1, y) & 0xFF) == 0) {
                            portions[i].drawPixel(x + 1, y, 0x000000FF);
                            continue PER_ROW;
                        } else {
                            if((portions[i].getPixel(x + 1, y) & 0xFF) != 0xFF)
                                portions[i].drawPixel(x + 1, y, 0x000000FF);
                        }
                    }
                }
            }
            PER_ROW:
            for (int y = 1; y < original.getHeight() - 1; y++) {
                for (int x = original.getWidth() - 1; x >= 1; x--) {
                    if ((portions[i].getPixel(x, y) & 0xFF) == 0xFF) {
                        if ((portions[i].getPixel(x - 1, y) & 0xFF) == 0) {
                            portions[i].drawPixel(x - 1, y, 0x000000FF);
                            continue PER_ROW;
                        } else {
                            if((portions[i].getPixel(x - 1, y) & 0xFF) != 0xFF)
                                portions[i].drawPixel(x - 1, y, 0x000000FF);
                        }
                    }
                }
            }
            PER_COL:
            for (int x = 1; x < original.getWidth() - 1; x++) {
                for (int y = 1; y < original.getHeight() - 1; y++) {
                    if ((portions[i].getPixel(x, y) & 0xFF) == 0xFF) {
                        if ((portions[i].getPixel(x, y + 1) & 0xFF) == 0) {
                            portions[i].drawPixel(x, y + 1, 0x000000FF);
                            continue PER_COL;
                        } else {
                            if((portions[i].getPixel(x, y + 1) & 0xFF) != 0xFF)
                                portions[i].drawPixel(x, y + 1, 0x000000FF);
                        }
                    }
                }
            }
            PER_COL:
            for (int x = 1; x < original.getWidth() - 1; x++) {
                for (int y = original.getHeight() - 1; y >= 1; y--) {
                    if ((portions[i].getPixel(x, y) & 0xFF) == 0xFF) {
                        if ((portions[i].getPixel(x, y - 1) & 0xFF) == 0) {
                            portions[i].drawPixel(x, y - 1, 0x000000FF);
                            continue PER_COL;
                        } else {
                            if((portions[i].getPixel(x, y - 1) & 0xFF) != 0xFF)
                                portions[i].drawPixel(x, y - 1, 0x000000FF);
                        }
                    }
                }
            }
        }

        for (int i = 0; i < 4; i++) {
            Piece piece = new Piece(getStage(), portions[i]);

            if (!BaseGame.DISABLE_RANDOM) {
                float randomX = 6f;
                float randomY = 3f;
                piece.centerAtPosition(
                    getX() + MathUtils.random(-randomX, randomX),
                    getY() + MathUtils.random(-randomY, randomY)
                );
                // This makes it MUCH harder, haha...
                // The origin will be a float near 0.5 for x and for y, representing position in the piece.
                piece.setOrigin(MathUtils.random(0.25f, 0.75f), MathUtils.random(0.25f, 0.75f));
            }

            getStage().addActor(piece);
        }
    }
}
