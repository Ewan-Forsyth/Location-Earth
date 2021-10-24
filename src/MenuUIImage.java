import processing.core.PVector;

/**
 * MenuUIImage Class
 * This class represents an image element on a menu.
 * If an image needs to be displayed in an individual MenuScreen, an instance of this will be crated.
 * This is not the class for the moving menu background objects.
 */
class MenuUIImage {

    private final String ID;
    private final PVector position;
    private final Animations imageAnimations;
    private boolean tintMode;
    private Utility util;

    public MenuUIImage(String ID, PVector position, String animation, int size, String skin, Utility util) {

        this.ID = ID;
        this.position = position;
        this.imageAnimations = new Animations(animation, size, util);
        this.imageAnimations.change(skin, -1);
        this.tintMode = false;
        this.util = util;
    }

    public String getID() {

        return this.ID;

    }

    public void sprite(String spriteName, int frameRate, boolean loop) {

        this.imageAnimations.change(spriteName, frameRate, loop);

    }

    public void setTintMode(boolean tintMode) {

        this.tintMode = tintMode;

    }

    public void draw() {

        if (this.tintMode)
            this.util.get().tint(255, 100);


        this.imageAnimations.draw(this.position);

        if (this.tintMode)
            this.util.get().noTint();

    }

}
