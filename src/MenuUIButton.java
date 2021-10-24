import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;

/**
 * MenuUIButton Class
 * This class represents a button element on a menu.
 * Any menu button is created here.
 * There are 3 button types. left-quad, right-quad and standard.
 */
class MenuUIButton {

    private int QUAD_SLOPE_OFFSET; //Used to create the slope effect on the main menu buttons.
    private int HOVER_EXTENSION_SPEED; //How fast should the button widen if hovered or made active.
    private final int HOVER_EXTENSION_LENGTH = 12; //The length of the object, except smaller means the button will get longer.

    private String ID;
    private String text;
    private PVector position;

    private float currentWidth;
    private float currentHeight;

    private float buttonWidth;
    private float buttonHeight;

    private int textSize;
    private int[] backgroundColour;
    private int[] shadowColour;
    private int[] hoverColour;
    private int[] textColour;
    private int[] shadowActive;
    private String buttonMode;
    private PFont font;

    private Utility util;

    private boolean hoverMode;
    private boolean activeMode;

    public MenuUIButton(String ID, String text, PVector position, float buttonWidth, float buttonHeight, int textSize, String buttonMode, int[] backgroundColour, int[] shadowColour, int[] hoverColour, int[] textColour, int[] shadowActive, Utility util) {

        QUAD_SLOPE_OFFSET = util.get().width / 32;
        HOVER_EXTENSION_SPEED = util.get().width / 213;

        this.ID = ID;
        this.text = text;
        this.position = position;

        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.currentWidth = this.buttonWidth;
        this.currentHeight = this.buttonHeight;

        this.textSize = textSize;
        this.buttonMode = buttonMode;

        this.backgroundColour = backgroundColour;
        this.shadowColour = shadowColour;
        this.hoverColour = hoverColour;
        this.textColour = textColour;
        this.shadowActive = shadowActive;

        this.font = util.get().createFont("fonts/8-bit-operator/8bitOperatorPlus8-Bold.ttf", this.textSize);

        this.hoverMode = false;
        this.activeMode = false;
        this.util = util;

    }

    public boolean isHovered() {

        return this.hoverMode;

    }

    public void setActive(boolean isActive) {

        this.activeMode = isActive;

    }

    public String getID() {

        return this.ID;

    }

    public void draw() {

        drawShadow(); //Draws the shadow first so it will be behind the main button.
        drawCoreButton(); //Draws the actual button.
        drawText(); //Places the text on the button.
        collisionDetection(); //Checks to see if the mouse if over the button.

        if (this.buttonMode.equals("left-quad") || this.buttonMode.equals("right-quad"))
            hoverMorph(); //Make the button wider.
    }

    private void collisionDetection() {

        switch (this.buttonMode) {
            case "standard":
                this.hoverMode = this.util.get().mouseX > this.position.x - this.currentWidth / 2 && this.util.get().mouseX < this.position.x + this.currentWidth / 2 &&
                        this.util.get().mouseY > this.position.y - this.currentHeight / 2 && this.util.get().mouseY < this.position.y + this.currentHeight / 2;
                break;
            case "left-quad":
                this.hoverMode = this.util.get().mouseX > this.position.x && this.util.get().mouseX < this.position.x + this.currentWidth &&
                        this.util.get().mouseY > this.position.y && this.util.get().mouseY < this.position.y + this.currentHeight;
                break;
            case "right-quad":
                this.hoverMode = this.util.get().mouseX < this.position.x && this.util.get().mouseX > this.position.x - this.currentWidth &&
                        this.util.get().mouseY > this.position.y && this.util.get().mouseY < this.position.y + this.currentHeight;
                break;
        }

    }

    private void hoverMorph() {

        if (this.hoverMode || this.activeMode) {

            if (this.currentWidth < this.buttonWidth + this.buttonWidth / HOVER_EXTENSION_LENGTH)
                this.currentWidth += HOVER_EXTENSION_SPEED;

        } else {

            if (this.currentWidth > this.buttonWidth)
                this.currentWidth -= HOVER_EXTENSION_SPEED; //Returns the button to normal size.

        }

    }

    private void drawCoreButton() {

        this.util.get().noStroke();

        if (!this.hoverMode)
            this.util.get().fill(backgroundColour[0], backgroundColour[1], backgroundColour[2], backgroundColour[3]);
        else
            this.util.get().fill(hoverColour[0], hoverColour[1], hoverColour[2], hoverColour[3]);

        switch (this.buttonMode) {
            case "standard":
                this.util.get().rectMode(PApplet.CENTER);
                this.util.get().rect(this.position.x, this.position.y, this.currentWidth, this.currentHeight);
                break;
            case "left-quad":
                this.util.get().quad(
                        this.position.x, this.position.y,
                        this.position.x + this.currentWidth, this.position.y,
                        this.position.x + this.currentWidth - QUAD_SLOPE_OFFSET, this.position.y + this.currentHeight,
                        this.position.x, this.position.y + this.currentHeight);
                break;
            case "right-quad":
                this.util.get().quad(
                        this.position.x - this.currentWidth, this.position.y,
                        this.position.x, this.position.y,
                        this.position.x, this.position.y + this.currentHeight,
                        this.position.x - this.currentWidth + QUAD_SLOPE_OFFSET, this.position.y + this.currentHeight);
                break;
        }

    }

    private void drawShadow() {

        this.util.get().noStroke();

        if (!this.activeMode)
            this.util.get().fill(shadowColour[0], shadowColour[1], shadowColour[2], shadowColour[3]);
        else
            this.util.get().fill(shadowActive[0], shadowActive[1], shadowActive[2], shadowActive[3]);

        switch (this.buttonMode) {
            case "standard":
                this.util.get().rectMode(PApplet.CENTER);
                this.util.get().rect(this.position.x + currentWidth / 1024, this.position.y - currentWidth / 1024, this.currentWidth + (float) (this.util.get().width / 320), this.currentHeight + (float) (this.util.get().width / 320));
                break;
            case "right-quad":
                this.util.get().quad(
                        this.position.x - this.currentWidth - this.currentWidth / 96, this.position.y - this.currentHeight / 12,
                        this.position.x, this.position.y - this.currentHeight / 12,
                        this.position.x, this.position.y + this.currentHeight + this.currentHeight / 12,
                        this.position.x - this.currentWidth + QUAD_SLOPE_OFFSET - this.currentWidth / 384, this.position.y + this.currentHeight + this.currentHeight / 12);
                break;
            case "left-quad":
                this.util.get().quad(
                        this.position.x, this.position.y - this.currentHeight / 12,
                        this.position.x + this.currentWidth + this.currentWidth / 96, this.position.y - this.currentHeight / 12,
                        this.position.x + this.currentWidth - QUAD_SLOPE_OFFSET + this.currentWidth / 384, this.position.y + this.currentHeight + this.currentHeight / 12,
                        this.position.x, this.position.y + this.currentHeight + this.currentHeight / 12);
                break;
        }

    }

    public void setText(String text) {

        this.text = text;

    }

    private void drawText() {

        this.util.get().noStroke();
        this.util.get().fill(textColour[0], textColour[1], textColour[2], textColour[3]);
        this.util.get().textFont(this.font);
        switch (this.buttonMode) {
            case "standard":
                this.util.get().textAlign(PApplet.CENTER, PApplet.CENTER);
                this.util.get().text(this.text, this.position.x, this.position.y - currentHeight / 32);
                break;
            case "left-quad":
                this.util.get().textAlign(PApplet.RIGHT, PApplet.CENTER);
                this.util.get().text(this.text, this.position.x + this.currentWidth - QUAD_SLOPE_OFFSET, this.position.y - currentHeight / 256 + this.currentHeight / 2);
                break;
            case "right-quad":
                this.util.get().textAlign(PApplet.LEFT, PApplet.CENTER);
                this.util.get().text(this.text, this.position.x - this.currentWidth + QUAD_SLOPE_OFFSET, this.position.y - currentHeight / 256 + this.currentHeight / 2);
                break;
        }


    }

}
