import processing.core.PVector;

/**
 * GameWorldObject Class.
 * Represents superfluous background items such as trees, rocks, cactus ect.
 */
public class GameWorldObject {

    private final PVector position;
    private Utility UTIL;
    private Animations sprites;

    public GameWorldObject(PVector position, String item, String skin, Utility util) {

        this.position = position;
        this.UTIL = util;

        this.sprites = new Animations("world/" + item, 16, this.UTIL);
        this.sprites.change(item + "" + skin, -1);

    }

    public PVector getPosition() {

        return this.position;

    }

    public float[] getDimensions() {

        return this.sprites.getDimensions();

    }

    public void integrateToWorld(float[] amountChanged) {

        this.position.x += amountChanged[0];
        this.position.y += amountChanged[1];

    }

    public void draw() {
        this.UTIL.get().imageMode(this.UTIL.get().CENTER);
        this.sprites.draw(new PVector(this.position.x, this.position.y));

    }


}
