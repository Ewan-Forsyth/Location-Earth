import processing.core.PApplet;
import processing.core.PVector;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * GameCharacter Class.
 * All characters in the game are of this class, including the player.
 * The updating, drawing and AI are centrally controlled from here although
 * the GameCharacterBrain class is where the AI is processed.
 */
class GameCharacter {

    private Animations animations; //Holds the animation of the body of the character
    private Animations arm;

    private Animations shapeShiftAnimations;
    private Animations shapeShiftArm;

    private PVector position;
    private PVector worldPosition;
    private PVector previousWorldPosition;

    private final float speed;

    private String direction;
    private String movementState;

    private final float characterWidth;
    private final float characterHeight;

    private int angle;

    private final String faction;

    private int health;
    private boolean dead;
    private boolean dying;
    private int deadCounter; //Used to change the rotation and tint of the character when it dies
    private boolean healthChanged;
    private boolean ammoChanged;
    private boolean weaponChanged;
    private boolean weaponAdded;

    private final HashMap<String, GameCharacterWeapon> weapons;
    private String currentWeapon;
    private int weaponCoolDown;
    private boolean canFire;

    private final ArrayList<GameCharacterProjectile> pendingShots;

    private GameCharacterBrain brain;

    private final Utility UTIL;

    private boolean effectActive;
    private String currentEffect;

    private Animations currentSkin;
    private Animations currentArm;

    private boolean isPlayer;

    private boolean damageTaken;
    private int damageTakenCounter;

    private boolean itemDispensed;

    private boolean killedOnLeftSide; //To decide on which way a character should fall over whn dead...

    public GameCharacter(PVector position, String characterSkin, String weapon, int startingAmmo, String faction, boolean isPlayer, Utility util) {

        this.UTIL = util;

        this.position = position;
        this.worldPosition = new PVector(0, 0);
        this.previousWorldPosition = new PVector(position.x, position.y);

        this.animations = new Animations("characters/" + characterSkin, 48, this.UTIL);
        this.animations.change("idle-down", 8);

        this.characterWidth = this.animations.getDimensions()[0];
        this.characterHeight = this.animations.getDimensions()[1];

        this.weapons = new HashMap<>();
        this.currentWeapon = weapon;
        this.weapons.put(weapon, new GameCharacterWeapon(weapon, startingAmmo, this.UTIL));
        this.weaponCoolDown = 0;
        this.canFire = true;

        this.arm = new Animations("characters/" + characterSkin + "/arm", 24, this.UTIL);
        this.arm.change(this.currentWeapon + "-down", -1);
        this.angle = 90;

        this.direction = "down";
        this.movementState = "idle";

        if (faction.equals("alien"))
            this.speed = 2f + (util.get().width / 1280f);
        else
            this.speed = 1f + (util.get().width / 1280f);

        this.health = 100;
        this.healthChanged = false;
        this.dead = false;
        this.deadCounter = 0;
        this.dying = false;

        this.faction = faction;

        this.pendingShots = new ArrayList<>();

        this.effectActive = false;
        this.currentEffect = "none";
        this.shapeShiftAnimations = new Animations("characters/agents/agent00/a", 48, this.UTIL);
        this.shapeShiftAnimations.change("idle-down", 8);
        this.shapeShiftArm = new Animations("characters/agents/agent00/a/arm", 24, this.UTIL);
        this.shapeShiftArm.change(this.currentWeapon + "-down", -1);

        this.currentSkin = this.animations;
        this.currentArm = this.arm;

        this.isPlayer = isPlayer;

        if (!this.isPlayer)
            this.brain = new GameCharacterBrain(this.UTIL, this);
        else
            this.brain = null;

        this.damageTakenCounter = 0;
        this.damageTaken = false;

        this.itemDispensed = false;

        this.killedOnLeftSide = false;

    }

    //Returns the literal position of the character in the context of Processing coordinates.
    public PVector position() {

        return this.position;

    }

    public PVector getPreviousWorldPosition() {

        return this.previousWorldPosition;

    }

    public void setWorldPosition(PVector worldZero) {

        this.previousWorldPosition = new PVector(this.worldPosition.x, this.worldPosition.y);
        this.worldPosition = new PVector(this.position.x - worldZero.x, this.position.y - worldZero.y);

    }

    //Returns the world position of the character in the context of the current game level.
    public PVector getWorldPosition() {

        return this.worldPosition;

    }

    public void integrateToWorld(float[] amountChanged) {

        this.position.x += amountChanged[0];
        this.position.y += amountChanged[1];

    }

    public float[] getSize() {

        return this.currentSkin.getDimensions();

    }

    public float getSpeed() {

        return this.speed;

    }

    public void setAngle(int angle) {

        if (angle > -35 && angle < 35)
            this.turn("right");
        else if (angle > 35 && angle < 145)
            this.turn("down");
        else if (angle > 145 && angle < 181 || angle < -145 && angle > -181)
            this.turn("left");
        else if (angle < -35 && angle > -145)
            this.turn("up");

        this.angle = angle;

    }

    public int getAngle() {

        return this.angle;

    }

    public void changeHealth(int amountChanged, boolean leftSide) {

        if (this.health + amountChanged > 100) {

            this.health = 100;

        } else if (this.health + amountChanged <= 0) {

            this.dying = true;
            if (!this.isDying() && !this.isDead())
                this.killedOnLeftSide = leftSide;
            this.health = 0;

        } else {

            this.health += amountChanged;

        }

        if (amountChanged < 0)
            this.damageTaken = true;

        this.healthChanged = true;

    }

    public boolean healthChanged() {

        boolean result = this.healthChanged;
        this.healthChanged = false;

        return result;

    }

    public Integer getHealth() {

        return this.health;

    }

    public void addAmmo(String weaponName, int ammoCount) {
        if (weaponExists(weaponName))
            this.weapons.get(weaponName).addAmmo(ammoCount);

        this.ammoChanged = true;
    }

    public boolean weaponExists(String weaponName) {

        boolean result = false;
        for (String i : this.weapons.keySet()) {
            if (weaponName.equals(i)) {
                result = true;
                break;
            }
        }
        return result;
    }

    public boolean ammoChanged() {

        boolean result = this.ammoChanged;
        this.ammoChanged = false;

        return result;

    }

    public Integer[] getAmmo() {

        GameCharacterWeapon weapon = weapons.get(currentWeapon);
        return new Integer[]{weapon.getAmmoCount(), weapon.getAmmoCapacity()};

    }

    public boolean weaponPrimaryOutOfAmmo() {

        return this.weapons.get("alien-blaster").getAmmoCount() < 20;

    }

    public String getFaction() {

        return this.faction;

    }

    public String getDirection() {

        return this.direction;

    }

    public boolean isDead() {

        return this.dead;

    }

    public String[] getWeapons() {

        String[] weaponNames = new String[this.weapons.size()];
        int count = 0;
        for (String keyName : this.weapons.keySet()) {
            weaponNames[count] = keyName;
            count++;
        }
        return weaponNames;

    }

    public String getCurrentWeapon() {

        return this.currentWeapon;

    }

    public int getCurrentWeaponIndex() {
        int count = 0;
        for (String keyName : this.weapons.keySet()) {

            if (keyName.equals(this.currentWeapon))
                break;

            count++;
        }
        return count;

    }

    public GameCharacterWeapon getCurrentWeaponObject() {

        return this.weapons.get(this.currentWeapon);

    }

    public void setCurrentWeapon(int index) {

        this.currentWeapon = getWeapons()[index];
        this.weaponChanged = true;

    }

    public void addWeapon(String ID, GameCharacterWeapon weaponObject) {

        this.weapons.put(ID, weaponObject);
        this.weaponAdded = true;

    }

    public boolean weaponAdded() {

        boolean result = this.weaponAdded;
        this.weaponAdded = false;

        return result;

    }

    public boolean weaponChanged() {

        boolean result = this.weaponChanged;
        this.weaponChanged = false;

        return result;

    }

    public void move(String direction) {

        switch (direction) {
            case "up":
                this.startMoving();
                this.position.y -= this.speed;
                break;
            case "down":
                this.startMoving();
                this.position.y += this.speed;
                break;
            case "left":
                this.startMoving();
                this.position.x -= this.speed;
                break;
            case "right":
                this.startMoving();
                this.position.x += this.speed;
                break;
            case "idle":
                this.stopMoving();
                break;
        }


    }

    public void startMoving() {

        this.movementState = "move";

    }

    public void stopMoving() {

        this.movementState = "idle";

    }

    public void turn(String direction) {

        this.direction = direction;

    }

    public void setAnimation() {

        switch (this.movementState) {
            case "move":
                switch (direction) {
                    case "up":
                        this.currentSkin.change("move-up", 2);
                        this.currentArm.change(this.currentWeapon + "-up", -1);
                        break;
                    case "down":
                        this.currentSkin.change("move-down", 2);
                        this.currentArm.change(this.currentWeapon + "-down", -1);
                        break;
                    case "left":
                        this.currentSkin.change("move-left", 3);
                        this.currentArm.change(this.currentWeapon + "-left", -1);
                        break;
                    case "right":
                        this.currentSkin.change("move-right", 3);
                        this.currentArm.change(this.currentWeapon + "-right", -1);
                        break;
                }
                break;
            case "idle":
                switch (this.direction) {
                    case "up":
                        this.currentSkin.change("idle-up", 8);
                        this.currentArm.change(this.currentWeapon + "-up", -1);
                        break;
                    case "down":
                        this.currentSkin.change("idle-down", 8);
                        this.currentArm.change(this.currentWeapon + "-down", -1);
                        break;
                    case "left":
                        this.currentSkin.change("idle-left", 8);
                        this.currentArm.change(this.currentWeapon + "-left", -1);
                        break;
                    case "right":
                        this.currentSkin.change("idle-right", 8);
                        this.currentArm.change(this.currentWeapon + "-right", -1);
                        break;
                }
                break;

        }

    }

    public void update() {

        setAnimation();

        if (this.effectActive && !this.dying) {

            if ("invisibility".equals(this.currentEffect))
                this.UTIL.get().tint(255, 48);


            this.weapons.get(this.currentEffect).addAmmo(-1);
            this.ammoChanged = true;

            if (this.weapons.get(this.currentEffect).getAmmoCount() == 0) {
                this.currentSkin = this.animations;
                this.currentArm = this.arm;
                this.effectActive = false;
                this.currentEffect = "none";

            }

        }

        if (!"invisibility".equals(this.currentEffect) && !this.dying)
            drawShadow();


        if (this.damageTaken) {
            this.UTIL.get().tint(230, 0, 0, 255);  //Tints the character red if they have been hit
            this.damageTakenCounter++;
            if (this.damageTakenCounter >= 15) {
                this.damageTaken = false;
                this.damageTakenCounter = 0;
            }
        }

        switch (this.direction) {
            case "up":
                if (this.dying) {

                    dyingAnimation();
                    this.currentSkin.draw(new PVector(0, 0));

                } else {

                    updateArm(new PVector((float) (this.position.x - this.characterWidth / 2.6), this.position.y - this.characterHeight / 24));
                    this.currentSkin.draw(this.position);

                }
                break;
            case "down":
                if (this.dying) {

                    dyingAnimation();
                    this.currentSkin.draw(new PVector(0, 0));

                } else {

                    this.currentSkin.draw(this.position);
                    updateArm(new PVector((float) (this.position.x + this.characterWidth / 2.6), this.position.y - this.characterHeight / 24));

                }
                break;
            case "left":
                if (this.dying) {

                    dyingAnimation();
                    this.currentSkin.draw(new PVector(0, 0));

                } else {

                    this.currentSkin.draw(this.position);
                    updateArm(new PVector(this.position.x + this.characterWidth / 32, this.position.y - this.characterHeight / 24));

                }
                break;
            case "right":
                if (this.dying) {

                    dyingAnimation();
                    this.currentSkin.draw(new PVector(0, 0));

                } else {

                    updateArm(new PVector(this.position.x - this.characterWidth / 32, this.position.y - this.characterHeight / 24));
                    this.currentSkin.draw(this.position);

                }
                break;
        }

        if (this.dying) {
            this.UTIL.get().popMatrix();
        }

        if (!this.canFire) {
            this.weaponCoolDown++;
            if (this.weaponCoolDown >= weapons.get(currentWeapon).getFireRate()) {
                this.canFire = true;
                this.weaponCoolDown = 0;
            }
        }

        this.UTIL.get().noTint();

    }

    public boolean isDying() {

        return this.dying;

    }

    private void dyingAnimation() {

        this.animations.change("dead", -1);
        this.UTIL.get().tint(240, 0, 0, 255);
        this.UTIL.get().pushMatrix();
        this.UTIL.get().translate(this.position.x, this.position.y);
        int angle = 90;
        if (!this.killedOnLeftSide)
            angle = -90;
        this.UTIL.get().rotate(PApplet.radians(angle));
        this.UTIL.get().translate(this.characterWidth / 8, -this.characterHeight / 2);
        this.deadCounter++;
        if (this.deadCounter >= 160)
            this.dead = true;


    }

    private void drawShadow() {

        this.UTIL.get().tint(0, 50);
        this.UTIL.get().pushMatrix();
        this.UTIL.get().translate(this.position.x - this.characterWidth / 8, this.position.y + this.characterHeight / 1.5f);
        this.UTIL.get().rotate(PApplet.radians(-135));
        this.UTIL.get().translate(this.characterWidth / 2, -this.characterHeight / 3);
        this.currentSkin.drawStatic(new PVector(0, 0));
        this.UTIL.get().popMatrix();
        this.UTIL.get().noTint();
    }

    public void updateArm(PVector armPosition) {

        this.UTIL.get().pushMatrix();
        this.UTIL.get().translate(armPosition.x, armPosition.y);
        this.UTIL.get().rotate(PApplet.radians(this.angle));
        this.UTIL.get().translate((float) (this.characterWidth / 1.2), 0);
        this.currentArm.draw(new PVector(0, 0));
        this.UTIL.get().popMatrix();

    }

    public void fire(boolean primary) {

        GameCharacterWeapon weapon = weapons.get(currentWeapon);
        if (this.health > 0) {
            if (this.canFire && weapon.getAmmoCount() > 0) {
                this.pendingShots.clear();

                if (weapon.getAmmoType().equals("none")) {

                    if (!this.effectActive) {

                        this.effectActive = true;
                        this.currentEffect = currentWeapon;

                        if (currentWeapon.equals("shape-shift")) {
                            this.currentSkin = this.shapeShiftAnimations;
                            this.currentArm = this.shapeShiftArm;
                        }

                    }

                } else {

                    float gunOffsetX = this.position.x;
                    float gunOffsetY = this.position.y;

                    switch (this.direction) {
                        case "up":
                            gunOffsetX = (float) (this.position.x + (-this.characterWidth / 2.6));
                            gunOffsetY = this.position.y + (-this.characterHeight / 24);
                            break;
                        case "down":
                            gunOffsetX = (float) (this.position.x + this.characterWidth / 2.6);
                            gunOffsetY = this.position.y + (-this.characterHeight / 24);
                            break;
                        case "left":
                            gunOffsetX = this.position.x + this.characterWidth / 32;
                            gunOffsetY = this.position.y + (-this.characterHeight / 8);
                            break;
                        case "right":
                            gunOffsetX = this.position.x + (-this.characterWidth / 32);
                            gunOffsetY = this.position.y + (-this.characterHeight / 8);
                            break;
                    }

                    this.pendingShots.add(new GameCharacterProjectile(new PVector(gunOffsetX, gunOffsetY), this.angle, weapon.getAmmoType(), this.faction, primary, this, this.UTIL));

                    if (this.isPlayer) {

                        weapon.addAmmo(-1);
                        this.ammoChanged = true;
                        this.canFire = false;

                    }
                }
            }
        }
    }

    public boolean isPlayer() {

        return this.isPlayer;

    }

    public boolean hasFired() {

        return this.pendingShots.size() > 0;

    }

    public GameCharacterProjectile getFiredShot() {

        return this.pendingShots.remove(0);

    }

    public void aiManager(ArrayList<GameCharacter> nearbyCharacters, PVector playersPosition, boolean isAtEdge) {

        if (!this.dying) {

            this.brain.think(nearbyCharacters, playersPosition);

            if (!isAtEdge)
                npcMovement();

        }

    }

    private void npcMovement() {

        for (String direction : this.brain.currentDirections()) {

            this.move(direction);

        }

        this.setAngle(this.brain.getAngle());

        if (this.brain.fire())
            this.fire(true);

        if (this.brain.switchToPrimaryWeapon()) {

            for (String i : this.weapons.keySet()) {

                if (!i.equals("frag")) {
                    this.currentWeapon = i;
                    break;
                }

            }

        }

        if (this.brain.switchToFrag())
            this.currentWeapon = "frag";


    }

    public String getCurrentEffect() {

        return this.currentEffect;

    }

    public boolean isItemDispensed() {

        return this.itemDispensed;

    }

    public void setItemDispensed(boolean hasItemBeenDispensed) {

        this.itemDispensed = hasItemBeenDispensed;

    }

    public void setActive(boolean isAlive) {
        this.dead = isAlive;
    }

}
