package no.sandramoen.libgdx38.actors.broken;

import no.sandramoen.libgdx38.actors.particles.broken.EffectTeapot;

public class Teapot extends Broken{

    public Teapot() {
        num_pieces = 8;
        image_path += "Teapot";
        particleActor = new EffectTeapot();
    }
}
