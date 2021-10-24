import processing.core.PVector;

/**
 * MenuUIBackground Class
 * This class represents a background element on a menu.
 * This consists of a single rectangle to be used as a background of sorts.
 */
class MenuUIBackground {

    private final String ID;
    private final PVector position;
    private final int rectMode;
    private final float backgroundWidth;
    private final float backgroundHeight;
    private final int stroke;
    private int[] backgroundColour;
    private final int strokeWeight;
    private final Utility util;

    public MenuUIBackground(String ID, PVector position, float backgroundWidth, float backgroundHeight, int rectMode, int[] backgroundColour, int stroke, int strokeWeight, Utility util) {

        this.ID = ID;
        this.position = position;
        this.rectMode = rectMode;
        this.backgroundWidth = backgroundWidth;
        this.backgroundHeight = backgroundHeight;
        this.backgroundColour = backgroundColour;
        this.stroke = stroke;
        this.strokeWeight = strokeWeight;

        this.util = util;

    }

    public void setBackgroundColour(int[] colour) {

        this.backgroundColour = colour;

    }

    public String getID() {

        return this.ID;

    }

    public void draw() {

        this.util.get().strokeWeight(this.strokeWeight);
        if (this.stroke == -1)
            this.util.get().noStroke();
        else
            this.util.get().stroke(this.stroke);

        this.util.get().fill(this.backgroundColour[0], this.backgroundColour[1], this.backgroundColour[2], this.backgroundColour[3]);

        this.util.get().rectMode(this.rectMode);
        this.util.get().rect(this.position.x, this.position.y, this.backgroundWidth, this.backgroundHeight);

    }

}
