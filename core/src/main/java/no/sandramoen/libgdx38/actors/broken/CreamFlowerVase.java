package no.sandramoen.libgdx38.actors.broken;

import no.sandramoen.libgdx38.actors.particles.broken.EffectCreamFlowerVases;
import no.sandramoen.libgdx38.actors.particles.broken.EffectTeapot;

public class CreamFlowerVase extends Broken{

    public CreamFlowerVase() {
        num_pieces = 8;
        image_path += "Cream Flower Vase";
        particleActor = new EffectCreamFlowerVases();
    }
}
