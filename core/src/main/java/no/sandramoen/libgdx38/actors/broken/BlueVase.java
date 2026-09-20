package no.sandramoen.libgdx38.actors.broken;

import no.sandramoen.libgdx38.actors.particles.broken.EffectBlueVase;
import no.sandramoen.libgdx38.actors.particles.broken.EffectTeapot;

public class BlueVase extends Broken{

    public BlueVase() {
        num_pieces = 6;
        image_path += "Blue Vase";
        particleActor = new EffectBlueVase();
    }
}
