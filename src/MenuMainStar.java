/**
 * MenuMainStar Class
 * This class represents a star that is moving from right to left on the main menu.
 * The stars are all managed in the MenuMainStarField class.
 */
class MenuMainStar {

    private float x;
    private final float y;
    private final float size;
    private final float randomSpeedOffset; //Gives a little variance to the star speed to make the scene look less uniform.
    private boolean destroyObject; //To store if the star should be destroyed.
    private final int fillColour;
    private final Utility util;
    boolean starMoving;

    public MenuMainStar(float x, float y, float size, float randomSpeedOffset, Utility util) {

        this.util = util;

        this.starMoving = true;
        this.x = x;
        this.y = y;
        this.size = size;
        this.randomSpeedOffset = randomSpeedOffset;

        this.destroyObject = false;

        this.fillColour = 255;

    }

    //To tell the MainMenu class if the star is no longer on the screen.
    public boolean needsDestroyed() {

        return this.destroyObject;

    }

    public void draw() {

        this.util.get().noStroke();
        this.util.get().fill(this.fillColour);
        this.util.get().rect(this.x, this.y, this.size, this.size);

        int STAR_SPEED_SCALAR = 1;
        if (this.starMoving)
            this.x -= this.size / STAR_SPEED_SCALAR + this.randomSpeedOffset; //Moves the star to the left.

        if (this.x < -1)
            this.destroyObject = true; //Lets the program know the star is off screen and that it can be removed from the ArrayList.

    }


}
