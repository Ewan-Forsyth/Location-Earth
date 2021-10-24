import processing.core.PVector;

/**
 * GameCage Class.
 * This hold information about the actual cage shown in the game that holds an alien.
 * This also handles the health and damage done to the cage to break it.
 */
public class GameCage {

    private final PVector position;

    private float[] dimensions;

    private Utility UTIL;

    private Animations cageBack;
    private Animations cageFront;
    private Animations character;

    private boolean isActive;

    private int health;

    private String characterName;

    private int hitCounter;

    public GameCage(PVector position, Utility util, String characterName) {

        this.position = position;
        this.UTIL = util;

        this.characterName = characterName;

        this.cageBack = new Animations("world/cage", 8, this.UTIL);
        this.cageBack.change("cage_back", -1);
        this.character = new Animations("menu/character-select", 48, this.UTIL);
        this.character.change(this.characterName, 8);
        this.cageFront = new Animations("world/cage", 8, this.UTIL);
        this.cageFront.change("cage_front", -1);

        this.dimensions = this.cageBack.getDimensions();
        this.health = 100;
        this.isActive = true;
        this.hitCounter = 0;

    }

    public PVector getPosition() {

        return this.position;

    }

    public float[] getDimensions() {

        return this.dimensions;

    }

    public boolean isActive() {

        return this.isActive;

    }

    public void setActive(boolean isActive) {

        this.isActive = isActive;

    }

    public String getCharacterName() {

        return this.characterName;

    }

    public void hit(int damage) {

        this.hitCounter = 15;
        decreaseHealth(damage);

        if (this.getHealth() <= 0)
            setActive(false);

    }

    public int getHealth() {

        return this.health;

    }

    private void decreaseHealth(int amount) {

        this.health = this.health - amount;

    }

    public void integrateToWorld(float[] amountChanged) {

        this.position.x += amountChanged[0];
        this.position.y += amountChanged[1];

    }

    public void draw() {

        this.UTIL.get().imageMode(this.UTIL.get().CENTER);

        if (this.hitCounter > 0)
            this.UTIL.get().tint(255, 0, 0, 255);
        this.cageBack.drawStatic(new PVector(this.position.x, this.position.y)); //Draws the background of the cage.
        this.UTIL.get().noTint();

        this.character.drawStatic(new PVector(this.position.x - (this.cageBack.getDimensions()[0] / 16), this.position.y + (this.cageBack.getDimensions()[1] / 4))); //The character inside the cage is drawn here

        if (this.hitCounter > 0)
            this.UTIL.get().tint(255, 0, 0, 255);
        this.cageFront.drawStatic(new PVector(this.position.x, this.position.y)); //Draws the bars of the cage.
        this.UTIL.get().noTint();

        if (this.hitCounter > 0)
            this.hitCounter--;

    }

}
