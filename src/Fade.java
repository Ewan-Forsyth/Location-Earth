import processing.core.PApplet;

/**
 * Fade Class.
 * Fade the screen to or from black.
 * Usually used when loading into or out of a game or menu.
 */
public class Fade {

    private Utility util;
    private int fadeOutAlpha; //How much the fade is over the content.
    private int fadeOutSpeed;
    private int fadeOutLength;
    private boolean fadeActive;

    private boolean fadeOut;

    public Fade(Utility util, int speed, int length, boolean fadeOut) {

        this.util = util;

        this.fadeOut = fadeOut;
        if (this.fadeOut)
            this.fadeOutAlpha = 0;
        else
            this.fadeOutAlpha = 255;

        this.fadeOutSpeed = speed;
        this.fadeOutLength = length;

        this.fadeActive = true;

    }

    public boolean isFullyFaded() {

        boolean result = false;

        if (this.fadeOut && this.fadeOutAlpha >= this.fadeOutLength)
            result = true;
        else if (!this.fadeOut && this.fadeOutAlpha <= 0)
            result = true;

        return result;

    }

    public void progressFade() {

        if (this.fadeActive) {

            this.util.get().rectMode(PApplet.CORNER);
            this.util.get().fill(0, 0, 0, Math.min(this.fadeOutAlpha, 255));
            this.util.get().noStroke();
            this.util.get().rect(0, 0, this.util.get().width, this.util.get().height);

            if (this.fadeOut)
                this.fadeOutAlpha += this.fadeOutSpeed;
            else
                this.fadeOutAlpha -= this.fadeOutSpeed;
        }

    }

}
