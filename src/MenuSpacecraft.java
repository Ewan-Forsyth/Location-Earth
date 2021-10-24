import processing.core.PVector;

/**
 * MenuSpacecraft Class
 * This class hold the information about a Spacecraft that is moving from left to right on the menu screen.
 * The sprites variable is an instance of the Animations class which is the image shown of the spacecraft.
 */
class MenuSpacecraft {

    private final PVector position;
    private final Animations sprites;
    private boolean active;
    int particleSpawnCount;
    private final Utility util;

    public MenuSpacecraft(PVector position, Utility util) {

        this.util = util;
        this.position = position;
        this.sprites = new Animations("menu/flying-spacecraft", 16, this.util);

        String[] possibleSkins = {"menu", "menu-alt", "menu-red"}; //All the possible UFO skins. One will be chosen at random.
        this.sprites.change(possibleSkins[this.util.randomInt(0, possibleSkins.length - 1)], 6);

        this.active = true;

        this.particleSpawnCount = 0; //This will be incremented to tell the sequence class when to spawn a particle

    }

    public boolean isActive() {

        return this.active;

    }

    public PVector getPosition() {

        return this.position;

    }

    public void draw() {

        this.sprites.draw(this.position);
        this.position.x += (float) (this.util.get().width / 384);

        if (this.position.x > this.util.get().width + (float) (this.util.get().width / 6))
            this.active = false;

        this.particleSpawnCount++;

    }


}
