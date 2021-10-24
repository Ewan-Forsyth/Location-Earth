import processing.core.PVector;

/**
 * MenuEarth Class
 * This class represents the earth that moves from right to left on the menu screen.
 * There is an Animation variable named sprites which contains the actual image of the earth.
 * The rest of the class is fairly simple and self explanatory.
 */
class MenuEarth {

    private final PVector position;
    private final Animations sprites;
    private boolean active; //Stores whether or not the earth is on screen.
    private final Utility util;

    public MenuEarth(PVector position, Utility util) {

        this.util = util;

        this.position = position;
        this.active = true;
        this.sprites = new Animations("menu/earth", 16, this.util);
        this.sprites.change("menu", -1, false);

    }

    public boolean isActive() {

        return this.active;

    }

    public void update() {

        this.sprites.draw(position);
        this.position.x -= 1;

        if (this.position.x < (float) (-this.util.get().width / 16))
            this.active = false;

    }

}
