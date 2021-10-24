import processing.core.PVector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * GameCharacterBrain Class.
 * This is the core AI class for a character.
 * Each GameCharacter class contains an instance of this. (except the player)
 * All the decisions of the NPCs are made here.
 */
public class GameCharacterBrain {

    private Utility util;

    private String state;
    private HashMap<String, Boolean> directions;

    private int searchCycleCount;
    private int searchCycleLimit;

    private int angle;

    private HashMap<String, Integer> directionAngles;

    private GameCharacter characterObject;
    private GameCharacter currentTarget;

    private boolean firePrimary;
    private boolean fireSecondary;

    private String currentCombatDirection;

    private int preFireModeCount;
    private int fireModeActivateLimit;
    private boolean fireModeActive;

    private int fireModeCount;
    private int fireModeLimit;

    private boolean switchToFrag;
    private boolean switchToPrimaryWeapon;

    private int checkForCloserTargetCount;

    public GameCharacterBrain(Utility util, GameCharacter characterReference) {

        this.util = util;

        this.characterObject = characterReference;

        this.state = "search";

        this.directions = new HashMap<>();
        this.directions.put("up", false);
        this.directions.put("down", false);
        this.directions.put("left", false);
        this.directions.put("right", false);
        this.directions.put("idle", true);

        this.angle = 90;

        this.directionAngles = new HashMap<>(); //To store the angle that the arm should be for each direction.
        this.directionAngles.put("up", -90);
        this.directionAngles.put("down", 90);
        this.directionAngles.put("left", 180);
        this.directionAngles.put("right", 0);

        this.firePrimary = false;
        this.fireSecondary = false;

        this.currentCombatDirection = "";

        this.switchToPrimaryWeapon = false;
        this.switchToFrag = false;

        setCurrentTarget(null);
        resetSearchMode();
        resetFireMode();

        this.checkForCloserTargetCount = 0;

    }

    private String getRelationalDirection(int angle) {

        String direction = "";

        if (angle > -180 && angle < -90) //top left
            direction = "top_left";
        else if (angle > -90 && angle < 0) //top right
            direction = "top_right";
        else if (angle > 0 && angle < 90) //bottom right
            direction = "bottom_right";
        else if (angle > 90 && angle < 180) //bottom left
            direction = "bottom_left";
        else if (angle == 180) //left
            direction = "left";
        else if (angle == -90) //top
            direction = "top";
        else if (angle == 0) //right
            direction = "right";
        else if (angle == 90) //bottom
            direction = "bottom";

        return direction;

    }

    private void resetFireMode() {

        this.preFireModeCount = 0;
        this.fireModeActivateLimit = this.util.randomInt(60, 420);
        this.fireModeActive = false;

        this.fireModeCount = 0;
        this.fireModeLimit = this.util.randomInt(30, 60);

    }

    private void resetSearchMode() {

        this.searchCycleCount = 0;
        this.searchCycleLimit = this.util.randomInt(60, 300);
        setCurrentTarget(null);

    }

    public void think(ArrayList<GameCharacter> nearbyCharacters, PVector playerPosition) {

        //==============================================================================================================
        // Runs only for mode changes.

        if (getNonFactionCharacters(nearbyCharacters).size() == 0 && !this.state.equals("search")) {

            changeState("search");


        } else if (getNonFactionCharacters(nearbyCharacters).size() > 0 && !this.state.equals("combat")) {

            this.checkForCloserTargetCount = 0;
            changeState("combat");
            selectTarget(nearbyCharacters);

        }

        //==============================================================================================================
        // Runs every frame.

        if (this.state.equals("search")) {

            searchMode(playerPosition);

        } else if (this.state.equals("combat")) {
            combatMode();
            this.checkForCloserTargetCount++;
            if (this.checkForCloserTargetCount >= 120) {
                selectTarget(nearbyCharacters);
                this.checkForCloserTargetCount = 0;
            }
        }

    }

    private ArrayList<GameCharacter> getNonFactionCharacters(ArrayList<GameCharacter> nearbyCharacters) {

        ArrayList<GameCharacter> nonFactionCharacters = new ArrayList<>();

        for (GameCharacter character : nearbyCharacters) {

            if (!character.getFaction().equals(this.characterObject.getFaction()))
                nonFactionCharacters.add(character);

        }

        return nonFactionCharacters;

    }

    private void selectTarget(ArrayList<GameCharacter> nearbyCharacters) {

        ArrayList<GameCharacter> nonFactionCharacters = getNonFactionCharacters(nearbyCharacters);

        if (nonFactionCharacters.size() > 0) {

            GameCharacter currentClosest = nonFactionCharacters.get(0);
            GameCharacter secondClosest = nonFactionCharacters.get(0);

            for (GameCharacter character : nonFactionCharacters) {

                float distance = this.util.findDistance(new float[]{this.characterObject.position().x, this.characterObject.position().y}, new float[]{character.position().x, character.position().y});
                if (distance < this.util.findDistance(new float[]{this.characterObject.position().x, this.characterObject.position().y}, new float[]{currentClosest.position().x, currentClosest.position().y}) && !character.isDying()) {

                    secondClosest = currentClosest;
                    currentClosest = character;

                }

            }


            if (currentClosest.isPlayer() && currentClosest.getCurrentEffect().equals("shape-shift") || currentClosest.getCurrentEffect().equals("invisibility")) {

                if (secondClosest.isPlayer())
                    setCurrentTarget(null);
                else
                    setCurrentTarget(secondClosest);


            } else {

                setCurrentTarget(currentClosest);

            }


        } else {

            setCurrentTarget(null);

        }

    }

    private void searchMode(PVector playerPosition) {

        if (this.characterObject.getFaction().equals("agent")) {

            this.searchCycleCount++;
            if (this.searchCycleCount >= this.searchCycleLimit) {

                chooseRandomDirection();
                resetSearchMode();

            }

        } else {

            this.angle = this.util.findAngle(new float[]{playerPosition.x, playerPosition.y}, new float[]{this.characterObject.position().x, this.characterObject.position().y});
            String direction = getRelationalDirection(this.angle);

            float distance = this.util.findDistance(new float[]{playerPosition.x, playerPosition.y}, new float[]{this.characterObject.position().x, this.characterObject.position().y});

            if (distance > this.util.get().width / 4f) { //If the alien is too far away from the player

                switch (direction) {
                    case "top_left":
                        setDirections(new boolean[]{true, false, true, false, false}); //up down left right idle
                        break;
                    case "top_right":
                        setDirections(new boolean[]{true, false, false, true, false}); //up down left right idle
                        break;
                    case "bottom_right":
                        setDirections(new boolean[]{false, true, false, true, false}); //up down left right idle
                        break;
                    case "bottom_left":
                        setDirections(new boolean[]{false, true, true, false, false}); //up down left right idle
                        break;
                    case "left":
                        setDirections(new boolean[]{false, false, true, false, false}); //up down left right idle
                        break;
                    case "top":
                        setDirections(new boolean[]{true, false, false, false, false}); //up down left right idle
                        break;
                    case "right":
                        setDirections(new boolean[]{false, false, false, true, false}); //up down left right idle
                        break;
                    case "bottom":
                        setDirections(new boolean[]{false, true, false, false, false}); //up down left right idle
                        break;

                }

            } else if (distance < this.util.get().width / 24f) { //If the alien is too close to the player

                switch (direction) {
                    case "top_left":
                        setDirections(new boolean[]{false, true, false, true, false}); //up down left right idle
                        break;
                    case "top_right":
                        setDirections(new boolean[]{false, true, true, false, false}); //up down left right idle
                        break;
                    case "bottom_right":
                        setDirections(new boolean[]{true, false, true, false, false}); //up down left right idle
                        break;
                    case "bottom_left":
                        setDirections(new boolean[]{true, false, false, true, false}); //up down left right idle
                        break;
                    case "left":
                        setDirections(new boolean[]{false, false, false, true, false}); //up down left right idle
                        break;
                    case "top":
                        setDirections(new boolean[]{false, true, false, false, false}); //up down left right idle
                        break;
                    case "right":
                        setDirections(new boolean[]{false, false, true, false, false}); //up down left right idle
                        break;
                    case "bottom":
                        setDirections(new boolean[]{true, false, false, false, false}); //up down left right idle
                        break;

                }

            } else { //If the alien is not too far away or not too close.

                this.searchCycleCount++;
                if (this.searchCycleCount >= this.searchCycleLimit) {

                    chooseRandomDirection();
                    resetSearchMode();

                }

            }

        }

    }

    private void combatMode() {

        float distance = 0f;
        boolean targetStillExists = true;
        try {

            this.angle = this.util.findAngle(new float[]{this.currentTarget.position().x, this.currentTarget.position().y}, new float[]{this.characterObject.position().x, this.characterObject.position().y});
            distance = this.util.findDistance(new float[]{this.currentTarget.position().x, this.currentTarget.position().y}, new float[]{this.characterObject.position().x, this.characterObject.position().y});

            if (this.currentTarget.isDying())
                targetStillExists = false;

        } catch (Exception e) {

            targetStillExists = false;

        }

        if (targetStillExists) {

            if (this.fireModeActive) {

                if (this.fireModeCount == this.fireModeLimit / 2)
                    this.firePrimary = true;
                else if (this.fireModeCount >= this.fireModeLimit)
                    resetFireMode();
                this.fireModeCount++;

            } else {

                String attackMode; //Charge, retreat, circle
                if (distance < this.util.get().width / 6f)
                    attackMode = "retreat";
                else if (distance > this.util.get().width / 3f)
                    attackMode = "charge";
                else
                    attackMode = "circle";

                String direction = getRelationalDirection(this.angle);

                if (this.preFireModeCount >= this.fireModeActivateLimit) {

                    setDirections(new boolean[]{false, false, false, false, true});
                    if (this.characterObject.weaponExists("frag") && this.util.randomInt(0, 10) == 0)
                        this.switchToFrag = true;
                    else
                        this.switchToPrimaryWeapon = true;

                    this.fireModeActive = true;

                } else {

                    switch (direction) {
                        case "top_left":  //top left
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, true, false, true, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{true, false, true, false, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{true, false, false, true, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{false, true, true, false, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "top_right":  //top right
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, true, true, false, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{true, false, false, true, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{false, true, false, true, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{true, false, true, false, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "bottom_right":  //bottom right
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{true, false, true, false, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, true, false, true, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{false, true, true, false, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{true, false, false, true, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "bottom_left":  //bottom left
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{true, false, false, true, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, true, true, false, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{true, false, true, false, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{false, true, false, true, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "left":  //left
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, false, false, true, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, false, true, false, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{true, false, false, false, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{false, true, false, false, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "top":  //top
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, true, false, false, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{true, false, false, false, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{false, false, false, true, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{false, false, true, false, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "right":  //right
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, false, true, false, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, false, false, true, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{false, true, false, false, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{true, false, false, false, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                        case "bottom":  //bottom
                            switch (attackMode) {
                                case "retreat":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{true, false, false, false, false}); //up down left right idle
                                    break;
                                case "charge":
                                    resetCombatDirection();
                                    setDirections(new boolean[]{false, true, false, false, false}); //up down left right idle
                                    break;
                                case "circle":
                                    switch (this.currentCombatDirection) {
                                        case "":
                                            setRandomCombatDirection();
                                            break;
                                        case "anticlockwise":
                                            setDirections(new boolean[]{false, false, true, false, false}); //up down left right idle
                                            break;
                                        case "clockwise":
                                            setDirections(new boolean[]{false, false, false, true, false}); //up down left right idle
                                            break;
                                    }
                                    break;
                            }
                            break;
                    }

                }

                this.preFireModeCount++;

            }
        }
    }

    private void setDirections(boolean[] newDirections) {

        this.directions.put("up", newDirections[0]);
        this.directions.put("down", newDirections[1]);
        this.directions.put("left", newDirections[2]);
        this.directions.put("right", newDirections[3]);
        this.directions.put("idle", newDirections[4]);

    }

    private void setRandomCombatDirection() {

        if (this.util.randomInt(0, 1) == 0)
            this.currentCombatDirection = "anticlockwise";
        else
            this.currentCombatDirection = "clockwise";

    }

    private void resetCombatDirection() {

        this.currentCombatDirection = "";

    }

    private void chooseRandomDirection() {

        ArrayList<String> availableDirections = new ArrayList<>(); //Any direction that the character can travel will be stored here.
        for (String key : this.directions.keySet()) {
            if (this.directions.get(key)) {
                this.directions.put(key, false);
            } else {
                availableDirections.add(key);
            }
        }

        Collections.shuffle(availableDirections);

        if (availableDirections.contains("idle") && this.util.randomInt(0, 16) != 0) { //Ensures there is a higher chance that the next mode will be idle

            this.directions.put("idle", true);

        } else if (availableDirections.size() > 2) {

            if (availableDirections.get(0).equals("up") && availableDirections.get(1).equals("left") ||
                    availableDirections.get(0).equals("up") && availableDirections.get(1).equals("right") ||
                    availableDirections.get(0).equals("down") && availableDirections.get(1).equals("left") ||
                    availableDirections.get(0).equals("down") && availableDirections.get(1).equals("right")) {

                this.directions.put(availableDirections.get(0), true);
                this.directions.put(availableDirections.get(1), true);

            } else {

                this.directions.put(availableDirections.get(0), true);

            }
        }

        if (!this.directions.get("idle"))
            this.angle = directionAngles.get(availableDirections.get(0));


    }

    public int getAngle() {

        return this.angle;

    }

    public ArrayList<String> currentDirections() {

        ArrayList<String> currentDirections = new ArrayList<>();
        for (String key : this.directions.keySet())
            if (this.directions.get(key))
                currentDirections.add(key);

        return currentDirections;

    }

    private void changeState(String newState) {

        if (newState.equals("search"))
            this.resetSearchMode();
        else if (newState.equals("combat"))
            resetFireMode();

        this.state = newState;

    }

    private void setCurrentTarget(GameCharacter newTarget) {

        this.currentTarget = newTarget;

    }

    public boolean fire() {

        boolean result = this.firePrimary;
        this.firePrimary = false;
        return result;

    }

    public boolean switchToPrimaryWeapon() {

        boolean result = this.switchToPrimaryWeapon;
        this.switchToPrimaryWeapon = false;
        return result;

    }


    public boolean switchToFrag() {

        boolean result = this.switchToFrag;
        this.switchToFrag = false;
        return result;

    }

}
