package no.sandramoen.libgdx38.actors;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;

import no.sandramoen.libgdx38.utils.AssetLoader;
import no.sandramoen.libgdx38.utils.GameUtils;

public class DisplayShelfImage extends Image {
    public boolean is_glow_enabled = false;
    public Sound ceramic_sound = AssetLoader.ceramic_sounds.get(MathUtils.random(0, AssetLoader.ceramic_sounds.size - 1));
    public float ceramic_sound_pitch = MathUtils.random(0.5f, 1.5f);

    private float time;
    private ShaderProgram shaderProgram;

    public DisplayShelfImage(String region_name) {
        super(AssetLoader.textureAtlas.findRegion(region_name));
        _start_hover_animation();
        
        shaderProgram = GameUtils.initShaderProgram(AssetLoader.defaultShader, AssetLoader.glowShader);
    }


    private void _start_hover_animation() {
        // hover stuff
        float hover_speed = MathUtils.random(1f, 8f);
        float hover_direction = 1f;
        if (MathUtils.randomBoolean())
            hover_direction *= -1;
        float hover_amount = MathUtils.random(Gdx.graphics.getHeight() * 0.01f, Gdx.graphics.getHeight() * 0.08f);

        // rotation stuff
        float rotation_speed = MathUtils.random(1, 8f);
        float rotation_amount = MathUtils.random(1f, 8f);
        float rotation_direction = 1f;
        if (MathUtils.randomBoolean())
            rotation_direction *= -1;

        addAction(Actions.forever(Actions.parallel(
            Actions.sequence( // hover
                Actions.moveBy(0f, -hover_amount * hover_direction, hover_speed, _get_random_interpolation()),
                Actions.moveBy(0f, hover_amount * hover_direction, hover_speed, _get_random_interpolation())
            ),
            Actions.sequence( // rotation
                Actions.rotateBy(rotation_amount * rotation_direction, rotation_speed, _get_random_interpolation()),
                Actions.rotateBy(-rotation_amount * rotation_direction, rotation_speed, _get_random_interpolation())
            )
        )));
    }


    private Interpolation _get_random_interpolation() {
        Interpolation random_interpolation = null;
        float random = MathUtils.random();
        if (random <= 1/8f)
            random_interpolation = Interpolation.pow2;
        else if (random <= 2/8f)
            random_interpolation = Interpolation.pow3;
        else if (random <= 3/8f)
            random_interpolation = Interpolation.pow4;
        else if (random <= 4/8f)
            random_interpolation = Interpolation.pow5;
        else if (random <= 5/8f)
            random_interpolation = Interpolation.smooth;
        else if (random <= 6/8f)
            random_interpolation = Interpolation.smoother;
        else if (random <= 7/8f)
            random_interpolation = Interpolation.sine;
        else
            random_interpolation = Interpolation.exp10;
        return random_interpolation;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        time += delta;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if(!is_glow_enabled)
            super.draw(batch, parentAlpha);
        else {
            try {
                batch.setShader(shaderProgram);
                shaderProgram.setUniformf("u_time", time * .25f);
                shaderProgram.setUniformf("u_imageSize", new Vector2(getWidth(), getHeight()));
                shaderProgram.setUniformf("u_glowRadius", 0.1f);
                super.draw(batch, parentAlpha);
                batch.setShader(null);
            } catch (Throwable error) {
                super.draw(batch, parentAlpha);
            }
        }
    }
}
