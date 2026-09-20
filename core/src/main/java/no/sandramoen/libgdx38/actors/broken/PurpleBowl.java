package no.sandramoen.libgdx38.actors.broken;

import no.sandramoen.libgdx38.actors.particles.broken.EffectPurpleBowl;
import no.sandramoen.libgdx38.actors.particles.broken.EffectTeapot;

public class PurpleBowl extends Broken{

    public PurpleBowl() {
        num_pieces = 6;
        image_path += "Purple Bowl";
        particleActor = new EffectPurpleBowl();
    }
}
