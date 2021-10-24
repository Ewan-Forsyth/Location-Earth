import processing.core.PVector;

import java.util.ArrayList;

/**
 * MenuSpacecraftSequence Class
 * This class manages the synchronisation between the spacecraft on the main menu and the particles coming from it.
 * This includes the spawning and deleting of objects.
 */
class MenuSpacecraftSequence {

    final private int MIN_PARTICLE_Y_OFFSET = 128;
    final private int MAX_PARTICLE_Y_OFFSET = 512;

    private ArrayList<MenuSpacecraft> crafts; //Stores any spacecrafts on the screen.
    private ArrayList<MenuSpacecraftParticle> craftParticles; //Stores the particles from the spacecraft.
    private int craftSpawnCounter; //To keep track of how long it has been since a spacecraft has spawned.
    private int randomCraftSpawnTime; //The time (in ticks) it should take for a spacecraft to spawn.
    private final Utility util;

    public MenuSpacecraftSequence(Utility util) {

        this.util = util;
        this.craftParticles = new ArrayList<>();
        this.crafts = new ArrayList<>();

        this.craftSpawnCounter = 0;
        this.randomCraftSpawnTime = this.util.randomInt(300, 600);

    }

    private void addCraft() {

        this.crafts.add(new MenuSpacecraft(new PVector((float) (-this.util.get().width / 8), this.util.randomInt(this.util.get().height / 32, this.util.get().height - this.util.get().height / 32)), this.util));

    }

    private void addParticle(float x, float y) {

        float finalY;
        int sideOfZero = this.util.randomInt(0, 1);
        if (sideOfZero == 0)
            finalY = y + (float) (this.util.get().width / this.util.randomInt(MIN_PARTICLE_Y_OFFSET, MAX_PARTICLE_Y_OFFSET)); //Set the particle to spawn below the spacecraft.
        else
            finalY = y - (float) (this.util.get().width / this.util.randomInt(MIN_PARTICLE_Y_OFFSET, MAX_PARTICLE_Y_OFFSET)); //Set the particle to spawn a bit above the spacecraft.

        this.craftParticles.add(new MenuSpacecraftParticle(new PVector(x, finalY), this.util)); //Adds the new particle to the arraylist.

    }

    public void update() {

        for (MenuSpacecraftParticle particle : this.craftParticles) {

            particle.draw();

            if (!particle.isActive()) {

                ArrayList<MenuSpacecraftParticle> tempParticles = new ArrayList<>();
                for (MenuSpacecraftParticle particles : this.craftParticles)
                    if (particles.isActive())
                        tempParticles.add(particles);
                this.craftParticles = tempParticles;

            }

        }

        this.craftSpawnCounter++;
        if (this.craftSpawnCounter == this.randomCraftSpawnTime) {

            addCraft();
            this.craftSpawnCounter = 0;
            this.randomCraftSpawnTime = this.util.randomInt(1200, 1800);

        }

        for (MenuSpacecraft craft : this.crafts) {

            craft.draw();

            addParticle(craft.getPosition().x, craft.getPosition().y); //Spawns a new particle where the spacecraft is.

            if (!craft.isActive()) { //If the spacecraft is now offscreen.

                ArrayList<MenuSpacecraft> tempCrafts = new ArrayList<>(); //Creates a temp arraylist to rebind to the main one.
                for (MenuSpacecraft testCrafts : this.crafts)
                    if (testCrafts.isActive())
                        tempCrafts.add(testCrafts);
                this.crafts = tempCrafts; //Rebinds the arraylist with all the currently active crafts to the main arraylist.

            }

        }


    }

}
