import processing.core.PVector;

import java.util.ArrayList;

//Chunk Map as the player would see it.
//Starting chunk = between 59 and 60

//   0,  1,  2,  3,  4,  5,  6,  7,
//   8,  9, 10, 11, 12, 13, 14, 15,
//  16, 17, 18, 19, 20, 21, 22, 23,
//  24, 25, 26, 27, 28, 29, 30, 31,
//  32, 33, 34, 35, 36, 37, 38, 39,
//  40, 41, 42, 43, 44, 45, 46, 47,
//  48, 49, 50, 51, 52, 53, 54, 55,
//  56, 57, 58, 59, 60, 61, 62, 63

//Biomes
//snow 0,  1,  2,  3, 8,  9, 10, 11, 16, 17, 18, 19;
//desert  4,  5,  6,  7, 12, 13, 14, 15, 20, 21, 22, 23;
//autumn 24, 25, 26, 27, 32, 33, 34, 35, 40, 41, 42, 43;
//mountain 28, 29, 30, 31, 36, 37, 38, 39,44, 45, 46, 47;
//grass 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63;

//Dimensions
//Full Mega Texture 15360 x 15360
//Bottom 16 chunks 15360 x 3840
//Each other quadrant 7680 x 5760

//Graphic Design Stuff
//Layer 4 - Zig Zag (base-value - 24)
//Layer 3 - Dotted Grid  (base-value - 16)
//Layer 2 - Trellis (base-value - 8)
//Layer 1 - Solid Colour base-value


/**
 * Chunk Class.
 * Because the game is an open world game, only objects that need
 * to be displayed should be shown and processed.
 * The chunks allow for easy management of this ensuring only the current
 * chunk that the player is in and the surrounding chunks are loaded.
 */
public class Chunk {

    private int ID;
    private Utility util;
    private PVector topLeft;
    private PVector topRight;
    private PVector bottomLeft;
    private PVector bottomRight;

    private ArrayList<Chunk> neighbours; //Any of the bordering chunks

    private ArrayList<GameCage> cages; //Stores any cage within the chunk.
    private ArrayList<GameWorldObject> worldObjects; //Stored the background objects such as trees, rocks ect.
    private ArrayList<GameUFO> ufo;
    private ArrayList<GamePickup> pickups;
    private String biomeType;

    public Chunk(int ID, PVector topLeft, PVector topRight, PVector bottomLeft, PVector bottomRight, Utility util) {

        this.ID = ID;
        this.topLeft = topLeft;
        this.topRight = topRight;
        this.bottomLeft = bottomLeft;
        this.bottomRight = bottomRight;
        this.util = util;

        this.neighbours = new ArrayList<>();
        this.cages = new ArrayList<>();
        this.worldObjects = new ArrayList<>();
        this.ufo = new ArrayList<>();
        this.pickups = new ArrayList<>();
        this.biomeType = getBiomeType();

    }

    /**
     * Calculates what biome the chunk is based on its ID.
     */
    private String getBiomeType() {


        String result = "";

        int[] snow = {0, 1, 2, 3, 8, 9, 10, 11, 16, 17, 18, 19};
        int[] desert = {4, 5, 6, 7, 12, 13, 14, 15, 20, 21, 22, 23};
        int[] autumn = {24, 25, 26, 27, 32, 33, 34, 35, 40, 41, 42, 43};
        int[] mountain = {28, 29, 30, 31, 36, 37, 38, 39, 44, 45, 46, 47};
        int[] grass = {48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63};

        for (int biomeID : snow) {
            if (this.getID() == biomeID) {
                result = "snow";
                break;
            }
        }

        for (int biomeID : desert) {
            if (this.getID() == biomeID) {
                result = "desert";
                break;
            }
        }

        for (int biomeID : autumn) {
            if (this.getID() == biomeID) {
                result = "autumn";
                break;
            }
        }

        for (int biomeID : mountain) {
            if (this.getID() == biomeID) {
                result = "mountain";
                break;
            }
        }

        for (int biomeID : grass) {
            if (this.getID() == biomeID) {
                result = "grass";
                break;
            }
        }

        return result;


    }

    public int getID() {

        return this.ID;

    }

    /**
     * Useful for getting coordinates within a this biome.
     *
     * @return An Arraylist of the coordinates at the corners of the biomes.
     */
    public ArrayList<PVector> getBounds() {

        ArrayList<PVector> bounds = new ArrayList<>();
        bounds.add(this.topLeft);
        bounds.add(this.topRight);
        bounds.add(this.bottomLeft);
        bounds.add(this.bottomRight);
        return bounds;

    }

    /**
     * Calcuates the exact center point of the chunk.
     *
     * @return a PVector of the chunk center.
     */
    public PVector chunkCenter() {

        float centerX = this.topLeft.x + (this.topRight.x - this.topLeft.x) / 2;
        float centerY = this.topLeft.y + (this.bottomLeft.y - this.topLeft.y) / 2;

        return new PVector(centerX, centerY);

    }

    public void addNeighbour(Chunk chunk) {

        this.neighbours.add(chunk);

    }

    public ArrayList<Chunk> getNeighbours() {

        return this.neighbours;

    }

    public void addCage(GameCage cage) {

        this.cages.add(cage);

    }

    public ArrayList<GameCage> getCages() {

        return this.cages;

    }

    public void rebindCages(ArrayList<GameCage> newCages) {

        this.cages = newCages;

    }

    public void addUFO(GameUFO ufo) {

        this.ufo.add(ufo);

    }

    public ArrayList<GameUFO> getUFOs() {

        return this.ufo;

    }

    public void addPickup(GamePickup pickup) {

        this.pickups.add(pickup);

    }

    public ArrayList<GamePickup> getPickups() {

        return this.pickups;

    }


    public void rebindPickups(ArrayList<GamePickup> newPickups) {

        this.pickups = newPickups;

    }

    public void addWorldObject() {

        String[] items;

        switch (this.biomeType) {

            case "snow":
                items = new String[]{"bush-snow", "rock-normal", "tree-snow"};
                break;
            case "desert":
                items = new String[]{"rock-normal", "rock-desert", "cactus-desert"};
                break;
            case "autumn":
                items = new String[]{"bush-autumn", "rock-normal", "tree-autumn"};
                break;
            case "mountain":
                items = new String[]{"rock-normal", "rock-mountain"};
                break;
            default:
                items = new String[]{"bush-grass", "rock-normal", "tree-grass"};
                break;
        }

        String[] skins = {"00", "01", "02", "03", "04", "05"};

        this.worldObjects.add(new GameWorldObject(getRandomPosition(), items[this.util.randomInt(0, items.length - 1)], skins[this.util.randomInt(0, skins.length - 1)], this.util));

    }

    private PVector getRandomPosition() {

        boolean isAcceptable = false;
        PVector randomPosition = new PVector();
        while (!isAcceptable) {

            float randomX = this.util.processing.random(this.topLeft.x + 64, this.topRight.x - 64);
            float randomY = this.util.processing.random(this.topLeft.y + 64, this.bottomLeft.y - 64);
            randomPosition.x = randomX;
            randomPosition.y = randomY;

            if (noObjectCollision(randomPosition))
                isAcceptable = true;

        }
        return randomPosition;

    }

    private boolean noObjectCollision(PVector object) {

        boolean result = true;
        for (GameCage cage : this.cages) {
            if (areColliding(object, cage.getPosition(), cage.getDimensions())) {
                result = false;
                break;

            }
        }

        for (GameWorldObject worldObject : this.worldObjects) {
            if (areColliding(object, worldObject.getPosition(), worldObject.getDimensions())) {
                result = false;
                break;

            }
        }
        return result;
    }

    private boolean areColliding(PVector mainObject, PVector comparatorObjectPosition, float[] comparatorObjectDimensions) {

        return mainObject.x > comparatorObjectPosition.x - (comparatorObjectDimensions[0]) && mainObject.x < comparatorObjectPosition.x + (comparatorObjectDimensions[0])
                && mainObject.y > comparatorObjectPosition.y - (comparatorObjectDimensions[1]) && mainObject.y < comparatorObjectPosition.y + (comparatorObjectDimensions[1]);

    }

    public ArrayList<GameWorldObject> getWorldObjects() {

        return this.worldObjects;

    }

    public void rebindWorldObjects(ArrayList<GameWorldObject> newWorldObjects) {

        this.worldObjects = newWorldObjects;

    }

    public void integrateToWorld(float[] amountChanged) {

        this.topLeft.x += amountChanged[0];
        this.topLeft.y += amountChanged[1];

        this.topRight.x += amountChanged[0];
        this.topRight.y += amountChanged[1];

        this.bottomLeft.x += amountChanged[0];
        this.bottomLeft.y += amountChanged[1];

        this.bottomRight.x += amountChanged[0];
        this.bottomRight.y += amountChanged[1];

    }

    public void debugDraw() {

        this.util.get().rectMode(this.util.get().CORNER);
        this.util.get().fill(255, 255, 255, 0);
        this.util.get().stroke(0, 75, 100, 255);
        this.util.get().strokeWeight(3);
        this.util.get().quad(this.topLeft.x, this.topLeft.y, this.topRight.x, this.topRight.y, this.bottomRight.x, this.bottomRight.y, this.bottomLeft.x, this.bottomLeft.y);
        this.util.get().strokeWeight(0);

    }

}
