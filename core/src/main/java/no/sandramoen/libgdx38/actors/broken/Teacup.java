package no.sandramoen.libgdx38.actors.broken;

import no.sandramoen.libgdx38.actors.particles.broken.EffectTeacup;
import no.sandramoen.libgdx38.actors.particles.broken.EffectTeapot;

public class Teacup extends Broken{

    public Teacup() {
        num_pieces = 4;
        image_path += "Teacup";
        particleActor = new EffectTeacup();
    }
}
