import processing.core.PFont;
import processing.core.PVector;

/**
 * MenuUIText Class.
 * This class represents a text element on a menu.
 * This represents any text that is to be shown on a menu.
 * The text can be retrieved and set according to what is passed in making it dynamic.
 */
class MenuUIText {

    private final String ID;
    private String text;
    private final PVector position;
    private final int alignmentX;
    private final int alignmentY;
    private final int size;
    private final int stroke;
    private int[] backgroundColour;
    private final PFont font;
    private final Utility util;

    public MenuUIText(String ID, String text, int alignmentX, int alignmentY, PVector position, int size, int[] backgroundColour, int stroke, Utility util) {

        this.util = util;
        this.ID = ID;
        this.text = text;
        this.position = position;
        this.alignmentX = alignmentX;
        this.alignmentY = alignmentY;
        this.size = size;
        this.backgroundColour = backgroundColour;
        this.stroke = stroke;
        this.font = util.get().createFont("fonts/8-bit-operator/8bitOperatorPlus8-Bold.ttf", this.size);

    }

    public String getID() {

        return this.ID;

    }

    public void setText(String text) {

        this.text = text;

    }

    public String getText() {

        return this.text;

    }

    public void setColour(int[] newColour) {

        this.backgroundColour = newColour;

    }

    public void draw() {

        this.util.get().stroke(this.stroke);
        this.util.get().fill(this.backgroundColour[0], this.backgroundColour[1], this.backgroundColour[2], this.backgroundColour[3]);
        this.util.get().textAlign(this.alignmentX, this.alignmentY);
        this.util.get().textFont(this.font);
        this.util.get().text(this.text, this.position.x, this.position.y);

    }

}
