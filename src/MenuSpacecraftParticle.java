import processing.core.PVector;

/**
 * MenuSpacecraftParticle Class
 * This represents an individual particle that is spawned from the MenuSpacecraft moving across the screen.
 * THe alpha of it is changes in here when it reaches 0, the active variable is changed so the particle can be removed.
 */
class MenuSpacecraftParticle {

    private final PVector position;
    private final int size;
    private int alpha;
    private boolean active;
    final private int[][] FIRE_COLOURS;
    private final Utility util;

    public MenuSpacecraftParticle(PVector position, Utility util) {

        this.util = util;
        this.position = position;
        this.size = (this.util.get().width / 32) / 8; //Calculates a size in relation to the width of the screen
        this.alpha = 255;
        this.active = true;
        this.FIRE_COLOURS = new int[][]{{0, 128, 128}, {0, 139, 139}, {0, 255, 255}, {0, 255, 255}, {0, 206, 209}, {64, 224, 208}, {0, 191, 255}}; //A variety of aqua blue colours.

    }

    public boolean isActive() {

        return this.active;

    }

    public void draw() {

        int randomIndex = this.util.randomInt(0, FIRE_COLOURS.length - 1); //Chooses a new fire colour each frame to make the effect look cool.
        this.util.get().noStroke();
        this.util.get().fill(FIRE_COLOURS[randomIndex][0], FIRE_COLOURS[randomIndex][1], FIRE_COLOURS[randomIndex][2], this.alpha);
        this.util.get().square(this.position.x, this.position.y, this.size);

        this.alpha -= this.util.randomInt(6, 10); //Decreases the alpha to make the particle slowly disappear
        if (this.alpha <= 0) //When the particle is no longer visible
            this.active = false;

    }


}
