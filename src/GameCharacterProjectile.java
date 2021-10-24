import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;

/**
 * GameCharacterProjectile Class.
 * Handles the projectiles that can be thrown or fired from guns.
 * This contains all the stats and attributes as well as information as to whom fired the shot.
 * Both explosives and bullets are handled in this class.
 */
class GameCharacterProjectile {

    private final PVector position;

    private final String weaponType;
    private final int lifeSpan;
    private final int weaponDamage;

    private final String characterFaction;
    private final GameCharacter characterObject;

    private int projectileAge;

    private boolean active;

    private final Animations sprites;

    private final float speedX;
    private final float speedY;

    private int explosiveCountDown;

    private final int blastRadius;

    private int angle;

    private final Utility UTIL;

    private float explosionRadius;

    private int explosionFadeCount;

    private int explosionAlphaValue;

    private int[] startingExplosionColour;

    public GameCharacterProjectile(PVector position, int angle, String ammoType, String characterFaction, boolean primaryFire, GameCharacter characterObject, Utility util) {

        this.UTIL = util;

        JSONObject projectileData = this.UTIL.loadJSONFile("projectiles.json").getJSONObject(ammoType);

        this.position = position;

        this.weaponDamage = projectileData.getInt("damage");
        this.characterFaction = characterFaction;
        this.weaponType = projectileData.getString("weapon-type");
        this.characterObject = characterObject;
        this.active = true;

        int coreLifeSpan = projectileData.getInt("ammo-lifespan");
        if (primaryFire)
            this.lifeSpan = coreLifeSpan;
        else
            this.lifeSpan = coreLifeSpan / 2;

        if (this.weaponType.equals("gun"))
            this.blastRadius = -1;
        else
            this.blastRadius = projectileData.getInt("blast-radius-scalar");

        this.projectileAge = 0;

        this.sprites = new Animations("projectiles", 56, this.UTIL);
        this.sprites.change(ammoType, -1);

        float baseSpeed = projectileData.getInt("speed") + util.get().width / 1280f;

        this.angle = angle;

        this.speedX = baseSpeed * PApplet.cos(PApplet.radians(this.angle));
        this.speedY = baseSpeed * PApplet.sin(PApplet.radians(this.angle));
        this.position.x = this.position.x + this.UTIL.get().width / 40f * PApplet.cos(PApplet.radians(this.angle));
        this.position.y = this.position.y + this.UTIL.get().width / 40f * PApplet.sin(PApplet.radians(this.angle));

        this.explosiveCountDown = 120;
        this.explosionRadius = 0;
        this.explosionFadeCount = 100;
        this.explosionAlphaValue = 100;

        this.startingExplosionColour = new int[]{0, 100, 182}; //Energy explosion colour

        if (ammoType.equals("frag"))
            this.startingExplosionColour = new int[]{189, 76, 0}; //Frag explosion colour

    }

    public boolean isActive() {

        return this.active;

    }

    public void setActive(boolean isActive) {

        this.active = isActive;

    }

    public void integrateToWorld(float[] amountChanged) {

        this.position.x += amountChanged[0];
        this.position.y += amountChanged[1];

    }

    public PVector position() {

        return this.position;

    }

    public float[] getSize() {

        return this.sprites.getDimensions();

    }

    public String getFaction() {

        return this.characterFaction;

    }

    public int getDamage() {

        return this.weaponDamage;

    }

    public String getWeaponType() {

        return this.weaponType;

    }

    public GameCharacter getOwnerObject() {

        return this.characterObject;

    }

    public boolean isExploding() {

        return this.explosiveCountDown == 1;

    }

    public int getBlastRadius() {

        return this.blastRadius;

    }

    public void drawDebugOutline() {

        this.UTIL.get().fill(200, 0, 0, 100);
        this.UTIL.get().stroke(255);
        this.UTIL.get().circle(this.position.x, this.position.y, ((float) (this.UTIL.get().width / this.blastRadius)) * 2);

    }

    public void integrate(boolean gamePaused) {

        if (this.projectileAge <= this.lifeSpan) {

            if (!gamePaused) {
                this.position.x = this.position.x + speedX;
                this.position.y = this.position.y + speedY;
            }

            //If the projectile is from a gun, it will be rotated.
            if (this.weaponType.equals("gun")) {

                this.UTIL.get().pushMatrix();
                this.UTIL.get().translate(this.position.x, this.position.y);
                this.UTIL.get().rotate(PApplet.radians(this.angle + 90));
                this.UTIL.get().translate(0, 0);
                sprites.draw(new PVector(0, 0));
                this.UTIL.get().popMatrix();

            } else { //If the item is an explosive.

                sprites.draw(this.position);

            }

            if (!gamePaused)
                this.projectileAge++;

        } else {

            switch (this.weaponType) {

                case "gun":
                    this.active = false;
                    break;
                case "explosive":

                    if (this.explosiveCountDown <= 8)
                        drawExplosionOutline(gamePaused);

                    if (this.explosiveCountDown > 0)
                        sprites.draw(this.position);

                    if (!gamePaused) {
                        if (this.explosiveCountDown < 0)
                            fadeExplosion();
                        else
                            this.explosiveCountDown--;
                    }
                    break;

            }

        }
    }

    private void drawExplosionOutline(boolean gamePaused) {

        this.UTIL.get().fill(this.startingExplosionColour[0], this.startingExplosionColour[1], this.startingExplosionColour[2], this.explosionAlphaValue);
        this.UTIL.get().stroke(0, 20);
        this.UTIL.get().circle(this.position.x, this.position.y, this.explosionRadius);

        if (!gamePaused) {
            this.explosionRadius += 96;
            if (this.explosionRadius >= (float) (this.UTIL.get().width / this.blastRadius) * 2)
                this.explosionRadius = (float) (this.UTIL.get().width / this.blastRadius) * 2;
        }
    }

    private void fadeExplosion() {

        this.explosionFadeCount -= 4;
        this.explosionAlphaValue = this.explosionFadeCount;
        if (this.explosionFadeCount <= 0)
            this.active = false;

    }


}
