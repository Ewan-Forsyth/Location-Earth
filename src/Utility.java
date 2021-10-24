import processing.core.PApplet;
import processing.data.JSONArray;
import processing.data.JSONObject;

/**
 * Utility Class.
 * This is the general class for methods that need to be accessed globally.
 * It is passed into most everything.
 * This is where the processing stuff is accessed. It makes the rest of the code super verbose but it had to be done :(
 */
public class Utility {

    PApplet processing; //Stores the entire processing library.

    public Utility(PApplet processing) {
        this.processing = processing;

    }

    public PApplet get() {

        return this.processing;

    }

    /*
     * Global random int function taking in the minimum and maximum numbers.
     */
    public int randomInt(int min, int max) {

        return (int) this.processing.random(min, max + 1);

    }

    /*
     * Takes in the name of a JSON file in the data folder and returns the content.
     */
    public JSONObject loadJSONFile(String filePath) {

        JSONObject result = null;
        try {
            result = this.processing.loadJSONObject(filePath);
        } catch (Exception e) {
            PApplet.println(e);
        }

        return result;

    }

    //Converts a JSON Array to and int array.
    public int[] convertJSONArray(JSONArray jsonArray) {

        int[] result = new int[jsonArray.size()]; //The new array is the same size as the JSON array that is being converted
        for (int i = 0; i < jsonArray.size(); i++)
            result[i] = (int) jsonArray.get(i);

        return result;

    }

    /* Used for finding the distance between 2 objects based on their positions. */
    public float findDistance(float[] aPos, float[] bPos) {

        float a = aPos[0] - bPos[0];
        float b = aPos[1] - bPos[1];

        float distanceSquared = (a * a) + (b * b);
        return PApplet.sqrt(distanceSquared);

    }

    /* Finds the angle that two objects are at in relation to each other. */
    public int findAngle(float[] measuredObject, float[] centerObject) {

        double angle = Math.atan2(measuredObject[1] - centerObject[1], measuredObject[0] - centerObject[0]);
        return (int) Math.toDegrees(angle);

    }
}
