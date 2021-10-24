import processing.core.PVector;

/**
 * GamePickup Class.
 * Contains information as to items that can be picked up by the player as well as draws the pickup.
 */
public class GamePickup {

    private final PVector position;
    private String pickupType;
    private int amount;
    private boolean active;
    private Animations sprites;
    private Utility UTIL;

    public GamePickup(PVector position, String pickupType, int amount, Utility util) {

        this.UTIL = util;
        this.position = position;
        this.pickupType = pickupType;
        this.amount = amount;
        this.active = true;
        this.sprites = new Animations("pickups", 48, this.UTIL);
        this.sprites.change(pickupType, -1);

    }

    public String getPickupType() {

        return this.pickupType;

    }

    public int getAmount() {

        return this.amount;

    }

    public boolean isActive() {

        return this.active;

    }

    public void setActive(boolean isActive) {

        this.active = isActive;

    }

    public void integrateToWorld(float[] amountChanged) {

        this.position.x += amountChanged[0];
        this.position.y += amountChanged[1];

    }

    public PVector position() {

        return this.position;

    }

    public float[] getSize() {

        return this.sprites.getDimensions();

    }

    public void draw() {

        this.sprites.draw(this.position);

    }

}
