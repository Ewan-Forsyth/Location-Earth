import processing.core.PFont;

/**
 * LoadingScreen Class.
 * The loading screen is displayed when a new game is being loaded.
 * This involves the loading process text as well as the mission brief.
 */
public class LoadingScreen {

    private Utility util;
    private PFont mainFont;
    private PFont briefFont;

    private int progressionCounter;
    private int progressionIndex;

    private String[] loadingText;

    public LoadingScreen(Utility util) {

        this.util = util;
        this.mainFont = util.get().createFont("fonts/8-bit-operator/8bitOperatorPlus8-Bold.ttf", this.util.get().width / 24f);
        this.briefFont = util.get().createFont("fonts/8-bit-operator/8bitOperatorPlus8-Bold.ttf", this.util.get().width / 40f);

        this.progressionCounter = 0;
        this.progressionIndex = 0;
        this.loadingText = new String[]{"Loading", "Loading.", "Loading..", "Loading..."};
    }

    public void draw() {

        drawLoadingText();
        drawAdvice();

    }

    private void drawLoadingText() {

        this.util.get().fill(255, 255, 255, 255);
        this.util.get().textAlign(this.util.get().LEFT, this.util.get().BOTTOM);
        this.util.get().textFont(this.mainFont);
        this.util.get().text(loadingText[this.progressionIndex], this.util.get().width / 128f, this.util.get().height - this.util.get().width / 128f);

        if (this.progressionCounter >= 30) {

            if (this.progressionIndex == loadingText.length - 1)
                this.progressionIndex = 0;
            else
                this.progressionIndex++;

            this.progressionCounter = 0;
        }

        this.progressionCounter++;


    }

    private void drawAdvice() {

        this.util.get().fill(255, 255, 255, 255);
        this.util.get().textAlign(this.util.get().RIGHT, this.util.get().BOTTOM);
        this.util.get().textFont(this.briefFont);
        this.util.get().text("Mission Briefing: Rescue your\ncrew mates and then find the\nspacecraft to escape earth", this.util.get().width - this.util.get().width / 128f, this.util.get().height - this.util.get().width / 128f);


    }

}
