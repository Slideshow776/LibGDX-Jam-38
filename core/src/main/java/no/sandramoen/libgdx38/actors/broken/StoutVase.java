package no.sandramoen.libgdx38.actors.broken;

import no.sandramoen.libgdx38.actors.particles.broken.EffectStoutVase;
import no.sandramoen.libgdx38.actors.particles.broken.EffectTeapot;

public class StoutVase extends Broken{

    public StoutVase() {
        num_pieces = 4;
        image_path += "Stout Vase";
        particleActor = new EffectStoutVase();
    }
}
