import processing.core.PFont;
import processing.core.PVector;

/**
 * GameUFO Class.
 * This represents the UFO that exists in the game for the player to find.
 * Note: The text shown when standing new to the UFO is drawn in the class but is not called from the draw function.
 */
public class GameUFO {

    private final PVector position;
    private Utility UTIL;
    private Animations sprites;

    private int power;
    private boolean chargeMode;
    private int powerUpCounter; //To track the ticks that should pass by before the charge iterates up to the next value
    private int powerUpLimit;

    private final PFont font;

    public GameUFO(PVector position, Utility util) {

        this.position = position;
        this.UTIL = util;

        this.sprites = new Animations("world/spaceship", 8, this.UTIL);
        this.sprites.change("ufo", -1);

        this.power = 0;
        this.chargeMode = false;
        this.powerUpCounter = 0;
        this.powerUpLimit = this.UTIL.randomInt(60, 300);

        this.font = util.get().createFont("fonts/8-bit-operator/8bitOperatorPlus8-Bold.ttf", this.UTIL.get().width / 64f);

    }

    public PVector position() {

        return this.position;

    }

    public float[] getDimensions() {

        return this.sprites.getDimensions();

    }

    public void integrateToWorld(float[] amountChanged) {

        this.position.x += amountChanged[0];
        this.position.y += amountChanged[1];

    }

    public void initialiseChargeMode() {

        this.chargeMode = true;

    }

    public boolean isCharging() {

        return this.chargeMode;

    }

    public int getPowerLevel() {

        return this.power;

    }

    public void draw() {

        this.UTIL.get().imageMode(this.UTIL.get().CENTER);
        this.sprites.draw(new PVector(this.position.x, this.position.y));

        if (this.chargeMode) {

            this.powerUpCounter++;

            if (this.powerUpCounter == this.powerUpLimit) {

                this.powerUpCounter = 0;
                this.powerUpLimit = this.UTIL.randomInt(30, 120);
                this.power++;

                if (this.power == 100)
                    this.chargeMode = false;

            }
        }

    }

    public void drawText(String text, int[] colour) {

        this.UTIL.get().fill(colour[0], colour[1], colour[2], 255);
        this.UTIL.get().textAlign(this.UTIL.get().CENTER, this.UTIL.get().CENTER);
        this.UTIL.get().textFont(this.font);
        this.UTIL.get().text(text, this.position.x, this.position.y - this.UTIL.get().width / 24f);

    }

}
