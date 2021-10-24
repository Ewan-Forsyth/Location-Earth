import java.util.ArrayList;

/**
 * MenuMainStarField Class
 * This class manages the stars in the background of the main menu.
 * This includes updating the stars and deleting the stars that are no longer on screen.
 */
class MenuMainStarfield {

    private final int MAX_STARS = 512; //Maximum number of stars on screen
    private final int DESTROY_COUNT_LIMIT = 60; //How many ticks should pass before the redundant stars should be deleted.
    private final Utility util;
    private int destroyStarsCount; //Incremented every tick
    private ArrayList<MenuMainStar> stars;

    private final float[] starSizes;

    public MenuMainStarfield(Utility util) {
        this.util = util;
        if (this.util.get().width < 1000)
            this.starSizes = new float[]{(float) (this.util.get().width / 192), (float) (this.util.get().width / 224), (float) (this.util.get().width / 256)}; //Adds star sizes that will be visible on smaller monitors.
        else
            this.starSizes = new float[]{(float) (this.util.get().width / 256), (float) (this.util.get().width / 384), (float) (this.util.get().width / 512), (float) (this.util.get().width / 640), (float) (this.util.get().width / 768)}; //Adds the default star sizes.

        this.destroyStarsCount = 0;
        stars = new ArrayList<>();

        addInitialStars();

    }

    //Adds the stars that are there when the menu is opened.
    private void addInitialStars() {

        for (int i = 0; i < this.MAX_STARS / 2; i++)
            this.stars.add(new MenuMainStar(this.util.randomInt(0, this.util.get().width), this.util.randomInt(0, this.util.get().height), getRandomStarSize(), this.util.get().random(0, 2), this.util));

    }

    public float getRandomStarSize() {

        return this.starSizes[this.util.randomInt(0, starSizes.length - 1)];

    }

    public void update() {

        for (MenuMainStar star : this.stars)
            star.draw();

        destroyStarsCount++;

        if (destroyStarsCount == DESTROY_COUNT_LIMIT) {

            destroyStars();
            destroyStarsCount = 0;

        }

        if (this.stars.size() < this.MAX_STARS)
            this.stars.add(new MenuMainStar(this.util.get().width, this.util.randomInt(0, this.util.get().height), getRandomStarSize(), this.util.get().random(0, 2), this.util));


    }

    public void destroyStars() {

        ArrayList<MenuMainStar> tempStars = new ArrayList<>(); //Temporary list to hold the items that do not need to be deleted.

        for (MenuMainStar star : this.stars)
            if (!star.needsDestroyed())
                tempStars.add(star);

        this.stars = tempStars; //Binds the temporary list to the main stars list.
    }

}
