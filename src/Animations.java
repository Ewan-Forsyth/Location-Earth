import processing.core.PImage;
import processing.core.PVector;
import processing.data.JSONArray;
import processing.data.JSONObject;

import java.util.HashMap;

/**
 * Animations Class.
 * Contains all the logic for displaying sprites and animating them.
 * An instance of this call can be created in any object that requires a sprite image to be shown.
 * A full description of the logic can be found below outlining the system in more detail.
 */
class Animations {

    /*
     *  The animation system works as follows:
     *  A "Animations" class is attached to any world object that requires a sprite placed over it.
     *  An ID is passed in which corresponds to a directory in assets/sprites/.
     *  There MUST be a animations.json file inside it which contains objects which relate to animations for that object.
     *  The animation objects have 2 fields.
     *  1. An array of all the files that are to be included. They are to be the file path from the current directory that the animations file is in.
     *  2. Whether or not the animations are to loop normally or oscillate.
     */

    //Key = name of sprite belonging to this object.
    //Value = Array of PImage's to be iterated over to make it look like the sprite is animated.
    private final HashMap<String, PImage[]> animations;

    //This stores whether the animation should go back through the sprites when it gets to the last one, or jump back to the start.
    private final HashMap<String, Boolean> oscillateStates;

    private String currentAnimation; //The name id of the animation belonging to this object.
    private int spriteIndex; //The array index of the sprite animation that is currently shown. This will be updated when necessary.

    private int frameRate; //How fast should the object sprite animate. (in ticks)
    private int currentFame; //Current tick/frame to measure the passage of time between the animations changing.

    private float scalar; //For calculating how big the sprite should be according to the screen size.

    private boolean oscillateUp; //Keeps track of the current oscillate direction. Only used if the oscillate mode is set to true on an animation.

    private boolean loop; //Keeps track of if an animation is to loop or play only one.

    private float spriteWidth;
    private float spriteHeight;

    private final Utility util;

    public Animations(String ID, int scale, Utility util) {

        this.util = util;

        this.animations = new HashMap<>();
        this.oscillateStates = new HashMap<>();

        JSONObject animationData = util.loadJSONFile("assets/animations/" + ID + "/animations.json"); //Gets the animations.json data for the requested object.

        String firstKey = ""; //To store a default sprite to negate null pointers.
        for (Object key : animationData.keys()) {

            String keyID = key.toString(); //Stores the key as a string so it can be used easily.

            if (firstKey.equals(""))
                firstKey = keyID; //If this is the first sprite in the data, it will be the default on shown.

            JSONObject animation = animationData.getJSONObject(keyID); //A single animation object that is being looped through.
            JSONArray animationFrames = animation.getJSONArray("frames"); //The array of files paths to each image.
            PImage[] newAnimationSet = new PImage[animationFrames.size()]; //Array for storing the PIMages as an animation set.

            //Creates the images whilst adding them to the animation set.
            for (int i = 0; i < animationFrames.size(); i++)
                newAnimationSet[i] = util.get().loadImage("assets/animations/" + ID + "/" + animationFrames.getString(i));

            this.animations.put(keyID, newAnimationSet); //Puts the animation into the hashmap which is used to switch between animations.
            this.oscillateStates.put(keyID, animation.getBoolean("oscillate")); //Stores the oscillate mode in a hash map with the animation name as the key as it corresponds to the animation set.
        }

        this.spriteWidth = 0;
        this.spriteHeight = 0;

        switchAnimation(firstKey, 300); //Changes to the first animation using the private inner method to do this.
        this.loop = true;

        this.scalar = (float) ((util.get().width / this.animations.get(currentAnimation)[spriteIndex].width) / scale); //Calculates how big the sprite should be so it is of the same proportions on each screen.

        if (this.scalar < 1.0f)
            this.scalar = 1.0f;

    }

    public float[] getDimensions() {

        float[] dimensions = new float[2];
        dimensions[0] = this.spriteWidth;
        dimensions[1] = this.spriteHeight;

        return dimensions;

    }

    private void setDimensions(float objectWidth, float objectHeight) {

        this.spriteWidth = objectWidth;
        this.spriteHeight = objectHeight;

    }

    /*
     *  To be called from the outside when it is time for the sprite and animation to be changed.
     */
    public void change(String animationName, int frameRate) {

        if (this.currentAnimation != animationName) {

            switchAnimation(animationName, frameRate);
            this.loop = true;

        }

    }

    /*
     *  Overridden method of the one above but provides an option for the animation to loop or play once.
     *  Because it is presumed that most animation will be looped, the method was written once without this parameter for ease.
     */
    public void change(String spriteSet, int frameRate, boolean loop) {

        if (this.currentAnimation != spriteSet) {

            switchAnimation(spriteSet, frameRate);
            this.loop = loop;

        }

    }

    /*
     *  Inner private method for changing the sprite and resetting the values so there are no null pointers.
     */
    private void switchAnimation(String spriteSet, int frameRate) {

        this.currentAnimation = spriteSet;
        this.spriteIndex = 0;
        this.currentFame = 0;
        this.frameRate = frameRate;
        this.oscillateUp = true;

        PImage[] animationSet = this.animations.get(currentAnimation);
        setDimensions(animationSet[0].width * this.scalar, animationSet[0].height * this.scalar);

    }

    //Called from outside the class to draw the sprites at the point of the coordinates passed in.
    public void draw(PVector position) {

        drawObject(position);

        //If it is -1, it means it is completely static or idle without animation.
        //Saves it from running through a cycle when it does not need to.
        if (this.frameRate != -1)
            currentFame++;

        //When the frameRate of the animation is reached.
        if (currentFame == frameRate) {

            progressAnimation();
            currentFame = 0;

        }

    }

    /**
     * Draws the item but does not move it on to the next frame.
     */
    public void drawStatic(PVector position) {

        drawObject(position);

    }

    private void drawObject(PVector position) {
        PImage[] animationSet = this.animations.get(currentAnimation); //Stores the animation set to a var so the get only needs to happen once.

        try {

            this.util.get().image(animationSet[spriteIndex], position.x, position.y, animationSet[spriteIndex].width * this.scalar, animationSet[spriteIndex].height * this.scalar);

        } catch (Exception e) {

            this.util.get().image(animationSet[spriteIndex], position.x, position.y, animationSet[spriteIndex].width * this.scalar, animationSet[spriteIndex].height * this.scalar);


        }
    }

    //Moves the current animation on to the next frame
    private void progressAnimation() {

        if (this.loop) { //If the animation is looping in any way.

            if (!this.oscillateStates.get(this.currentAnimation))
                normalLoop();
            else
                oscillateLoop();

        } else {

            singleAnimation();

        }

    }

    //For animations that go back to the start immediately after the last frame.
    private void normalLoop() {

        if (this.spriteIndex == this.animations.get(this.currentAnimation).length - 1)
            this.spriteIndex = 0;
        else
            this.spriteIndex++;

    }

    //For animations that animate back through the frames when it reaches the end.
    private void oscillateLoop() {
        if (this.spriteIndex == this.animations.get(currentAnimation).length - 1)
            this.oscillateUp = false;
        else if (this.spriteIndex == 0)
            this.oscillateUp = true;

        if (this.oscillateUp)
            this.spriteIndex++;
        else
            this.spriteIndex--;

    }

    //If an animation is to be played once, and once only.
    private void singleAnimation() {

        if (this.spriteIndex == this.animations.get(this.currentAnimation).length - 1)
            this.frameRate = -1;

        while (this.spriteIndex < this.animations.get(this.currentAnimation).length - 1)
            this.spriteIndex++;

    }

}
