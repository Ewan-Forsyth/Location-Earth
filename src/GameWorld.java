import processing.core.PImage;
import processing.core.PVector;

/**
 * GameWorld Class.
 * This stores values as to the position of the world in accordance with the player.
 * The only way in which this is represented is through the background mega texture.
 * The mega texture is displayed through this class.
 */
public class GameWorld {

    private Utility UTIL;
    private float worldX;
    private float worldY;
    private float worldWidth;
    private float worldHeight;

    private PImage backgroundTerrain;

    private PVector amountChanged; //Stores the direction and amount that the world has moved in a single frame.

    public GameWorld(Utility util) {

        this.UTIL = util;

        this.backgroundTerrain = this.UTIL.get().loadImage("assets/animations/world/mega_background.png");


        this.worldX = this.UTIL.get().width / 2.0f;
        this.worldY = this.UTIL.get().height - this.backgroundTerrain.height / 2.01f;

        this.worldWidth = (float) (this.backgroundTerrain.width);
        this.worldHeight = (float) (this.backgroundTerrain.height);

        this.amountChanged = new PVector(0, 0);

    }

    public PVector getWorldZero() {

        return new PVector(this.worldX - (this.worldWidth / 2), this.worldY - (this.worldHeight / 2));

    }

    public float[] getAmountChanged() {

        float x = this.amountChanged.x;
        float y = this.amountChanged.y;

        this.amountChanged.x = 0;
        this.amountChanged.y = 0;

        return new float[]{x, y};

    }

    public float getWorldX() {

        return this.worldX;
    }

    public float getWorldY() {

        return this.worldY;
    }

    public float getWorldWidth() {

        return this.worldWidth;
    }

    public float getWorldHeight() {

        return this.worldHeight;
    }

    public void integrateToWorld(float[] amountChanged) {

        this.worldX += amountChanged[0];
        this.worldY += amountChanged[1];

    }

    public void draw() {

        this.UTIL.get().image(this.backgroundTerrain, this.worldX, this.worldY);

    }

    //Moves the entire world. This could be through a set sequence, or usually, the player moving.
    public void move(String direction, float speed) {

        switch (direction) {
            case "up":
                this.amountChanged.y = -speed;
                break;
            case "down":
                this.amountChanged.y = speed;
                break;
            case "left":
                this.amountChanged.x = -speed;
                break;
            case "right":
                this.amountChanged.x = speed;
                break;
        }

    }

}
