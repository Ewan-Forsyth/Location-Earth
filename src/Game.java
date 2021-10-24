import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 * Game Class.
 * Contains all the logic for setting up and running the game.
 * This contains the update function which is called every tick.
 */
class Game {

    private boolean active;

    private final Input input;

    private final HashMap<String, Boolean> gameState; //To store which stage the game is at. e.g. Active, Game over menu ect.

    private HashMap<String, MenuScreen> uiScreens;

    private HashMap<String, GameCharacter> characters;

    private boolean keyboardArmRotate;
    private final float[] armRotateMouseCoords;
    private final Animations crosshair;

    private ArrayList<GameCharacterProjectile> projectiles;

    private final ArrayList<String> currentWeaponNames;

    private boolean debugMode;
    private boolean debugDrawMode;
    private int frameCountChange;

    private final Utility util;

    private GameWorld currentWorld;

    private HashMap<String, Boolean> worldLocks; //To store if the world should move or not depending on the axis in the HashMap.

    private ArrayList<String> friendlyNPCs;

    private ArrayList<Chunk> chunks;
    private Chunk currentChunk;
    private int chunkUpdateCounter;

    private int numberOfAliensToFind;

    private String activateAction;

    private boolean gameWon;
    private int gameWonSequenceCounter;

    private Fade fadeout;
    private Fade gameOverFadeout;
    private boolean gameOverFadeoutActive;

    private Fade fadeIn;

    private int gameEndCounter;

    private GameStatistics statistics;

    private CombatDirector combatDirector;
    private int combatDirectorCounter;

    private ArrayList<String> weaponsToUnlock;
    private int weaponUnlockMessageCounter;
    private boolean weaponUnlockMode;

    private boolean gamePaused;

    private Fade quitGameFadeOut;
    private boolean quitGameFadeOutActive;
    private String quitGameAction;

    private final String[] dynamicHUDElements;

    private boolean finalBattleActive;

    public Game(Input input, Utility util, String playerCharacter) {

        this.combatDirector = new CombatDirector(util);
        this.combatDirectorCounter = 0;

        this.input = input;
        this.util = util;

        this.active = true;

        this.statistics = new GameStatistics();

        this.gameState = new HashMap<>();
        this.gameState.put("active", true);
        this.gameState.put("game-over", false);

        this.activateAction = "";

        this.currentWorld = new GameWorld(util);
        createChunks();

        this.chunkUpdateCounter = 0;
        this.currentChunk = this.getChunk(59); //Binds the starting chunk for the player to spawn in.

        this.characters = new HashMap<>();
        createPlayer(playerCharacter);
        processFriendlyNPCs(playerCharacter);

        ArrayList<Integer> randomObjectChunks = generateRandomChunkPlacement(); //Gets an ArrayList of 4 ints of chunks that are spread out across the world.

        placeWorldObjects();
        createCages(randomObjectChunks);
        addUFO(randomObjectChunks.get(3));

        this.projectiles = new ArrayList<>();
        currentWeaponNames = new ArrayList<>();

        this.debugMode = false;
        this.debugDrawMode = false;
        this.frameCountChange = 0;

        this.worldLocks = new HashMap<>();
        this.worldLocks.put("x", false);
        this.worldLocks.put("y", false);

        this.gameWon = false;
        this.gameWonSequenceCounter = 0;
        this.fadeout = new Fade(this.util, 3, 300, true);
        this.gameOverFadeout = new Fade(this.util, 3, 300, true);

        this.gameOverFadeoutActive = false;
        this.gameEndCounter = 0;
        this.fadeIn = new Fade(this.util, 2, 600, false);

        this.keyboardArmRotate = false;
        this.armRotateMouseCoords = new float[2];
        this.crosshair = new Animations("hud", 48, this.util);
        this.crosshair.change("crosshair-blue", -1);
        createHud();


        this.weaponsToUnlock = new ArrayList<>();
        this.weaponsToUnlock.add("desert-eagle");
        this.weaponsToUnlock.add("mp5");
        if (this.util.randomInt(0, 1) == 0)
            this.weaponsToUnlock.add("invisibility");
        else
            this.weaponsToUnlock.add("shape-shift");


        this.weaponUnlockMessageCounter = 0;
        this.weaponUnlockMode = false;

        this.gamePaused = false;

        this.quitGameFadeOut = new Fade(this.util, 4, 300, true);
        this.quitGameFadeOutActive = false;
        this.quitGameAction = "";

        this.dynamicHUDElements = new String[]{"health", "ammo", "weapon-unlocked"};

        this.finalBattleActive = false;

    }

    public boolean isActive() {
        return this.active;
    }

    private void createPlayer(String chosenCharacter) {

        this.characters.put("player", new GameCharacter(new PVector(this.util.get().width / 2f, this.util.get().height / 2f), "aliens/" + chosenCharacter, "alien-blaster", 64, "alien", true, this.util));
        this.characters.get("player").addWeapon("alien-mine", new GameCharacterWeapon("alien-mine", 10, this.util));

    }

    private void processFriendlyNPCs(String playerCharacter) {
        String[] characterNames = {"zedekiah", "shiphrah", "eglon", "jael"};
        this.friendlyNPCs = new ArrayList<>();

        for (String characterName : characterNames) {
            if (!characterName.equals(playerCharacter))
                this.friendlyNPCs.add(characterName);
        }

        this.numberOfAliensToFind = 3;
    }

    private void createChunks() {

        this.chunks = new ArrayList<>();
        int[] chunkValues = {0, 1920, 3840, 5760, 7680, 9600, 11520, 13440};
        int ID = 0;
        for (int chunkValue : chunkValues) {
            for (int value : chunkValues) {
                this.chunks.add(new Chunk(ID
                        , new PVector(this.currentWorld.getWorldZero().x + value, this.currentWorld.getWorldZero().y + chunkValue),
                        new PVector(this.currentWorld.getWorldZero().x + value + 1920, this.currentWorld.getWorldZero().y + chunkValue),
                        new PVector(this.currentWorld.getWorldZero().x + value, this.currentWorld.getWorldZero().y + chunkValue + 1920),
                        new PVector(this.currentWorld.getWorldZero().x + value + 1920, this.currentWorld.getWorldZero().y + chunkValue + 1920),
                        this.util));
                ID++;
            }
        }

        for (Chunk chunkMain : chunks) {

            for (Chunk chunkInner : chunks) {

                if (chunkMain.getID() != chunkInner.getID()) {

                    if (this.util.findDistance(new float[]{chunkMain.getBounds().get(0).x, chunkMain.getBounds().get(0).y}, new float[]{chunkInner.getBounds().get(0).x, chunkInner.getBounds().get(0).y}) <= 2715.30)
                        chunkMain.addNeighbour(chunkInner);

                }

            }

        }

    }

    public ArrayList<Integer> generateRandomChunkPlacement() {

        int[] chunkZoneZero = {0, 1, 2, 8, 9, 10, 16, 17, 18};
        int[] chunkZoneOne = {5, 6, 7, 13, 14, 15, 21, 22, 23};
        int[] chunkZoneTwo = {32, 33, 34, 40, 41, 42};
        int[] chunkZoneThree = {37, 38, 39, 45, 46, 47};

        ArrayList<Integer> randomPositions = new ArrayList<>();

        randomPositions.add(chunkZoneZero[this.util.randomInt(0, chunkZoneZero.length - 1)]);
        randomPositions.add(chunkZoneOne[this.util.randomInt(0, chunkZoneOne.length - 1)]);
        randomPositions.add(chunkZoneTwo[this.util.randomInt(0, chunkZoneTwo.length - 1)]);
        randomPositions.add(chunkZoneThree[this.util.randomInt(0, chunkZoneThree.length - 1)]);
        Collections.shuffle(randomPositions); //Randomises the list to make the map less predictable.
        return randomPositions;

    }

    private void createCages(ArrayList<Integer> randomObjectChunks) {

        int[] chunks = {randomObjectChunks.get(0), randomObjectChunks.get(1), randomObjectChunks.get(2)};
        int count = 0;
        for (String friendlyNPC : this.friendlyNPCs) {
            PVector centerCoords = getChunk(chunks[count]).chunkCenter();
            this.getChunk(chunks[count]).addCage(new GameCage(new PVector(centerCoords.x, centerCoords.y), this.util, friendlyNPC));
            count++;
        }

    }

    private Chunk getChunk(int ID) {

        Chunk result = this.chunks.get(0);

        for (Chunk chunk : this.chunks) {
            if (chunk.getID() == ID) {
                result = chunk;
                break;
            }
        }

        return result;

    }

    private void placeWorldObjects() {

        for (Chunk chunk : this.chunks) {
            int amountOfObjects = this.util.randomInt(24, 48);
            for (int i = 0; i <= amountOfObjects; i++)
                chunk.addWorldObject();
        }

    }

    private void addUFO(int chunk) {
        getChunk(chunk).addUFO(new GameUFO(new PVector(getChunk(chunk).chunkCenter().x, getChunk(chunk).chunkCenter().y), this.util));
    }

    private void createHud() {

        this.uiScreens = new HashMap<>();
        this.uiScreens.put("hud", new MenuScreen());
        this.uiScreens.put("debug-mode", new MenuScreen());
        this.uiScreens.put("game-over", new MenuScreen());
        this.uiScreens.put("new-weapon", new MenuScreen());
        this.uiScreens.put("pause", new MenuScreen());

        JSONObject colours = this.util.loadJSONFile("colours.json");

        this.uiScreens.get("hud").addImageElement(new MenuUIImage("ammo-image", new PVector(this.util.get().width / 44f, this.util.get().height - this.util.get().height / 8f), "hud", 24, "ammo-energy-normal", this.util));

        Integer[] ammoValues = this.characters.get("player").getAmmo();
        this.uiScreens.get("hud").addTextElement(new MenuUIText("ammo", ammoValues[0].toString() + "/" + ammoValues[1].toString(), PApplet.LEFT, PApplet.CENTER, new PVector((float) (this.util.get().width / 22), this.util.get().height - (float) (this.util.get().height / 8)), this.util.get().width / 32, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));

        this.uiScreens.get("hud").addImageElement(new MenuUIImage("health-image", new PVector((float) (this.util.get().width / 44), this.util.get().height - (float) (this.util.get().height / 24)), "hud", 24, "health-normal", this.util));
        this.uiScreens.get("hud").addTextElement(new MenuUIText("health", this.characters.get("player").getHealth().toString(), PApplet.LEFT, PApplet.CENTER, new PVector((float) (this.util.get().width / 22), this.util.get().height - (float) (this.util.get().height / 24)), this.util.get().width / 32, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
        updateWeaponSlots();

        this.uiScreens.get("debug-mode").addTextElement(new MenuUIText("fps-counter", "FPS: " + PApplet.round(this.util.get().frameRate), PApplet.RIGHT, PApplet.TOP, new PVector(this.util.get().width - this.util.get().width / 192f, this.util.get().height / 96f), this.util.get().width / 64, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
        this.uiScreens.get("debug-mode").addTextElement(new MenuUIText("player-position", "x: y:", PApplet.RIGHT, PApplet.TOP, new PVector(this.util.get().width - this.util.get().width / 192f, this.util.get().height / 24f), this.util.get().width / 64, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
        this.uiScreens.get("debug-mode").addTextElement(new MenuUIText("chunk", "Chunk:", PApplet.RIGHT, PApplet.TOP, new PVector(this.util.get().width - this.util.get().width / 192f, this.util.get().height / 14f), this.util.get().width / 64, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));

        int count = 0;
        for (String friendlyNPC : this.friendlyNPCs) {

            this.uiScreens.get("hud").addImageElement(new MenuUIImage(friendlyNPC + "-icon", new PVector(this.util.get().width / 44f + (this.util.get().width / 32f * count), this.util.get().height - this.util.get().height / 4.8f), "menu/character-select", 48, friendlyNPC, util));
            this.uiScreens.get("hud").setImageElementTint(friendlyNPC + "-icon", true);
            count++;

        }

        this.uiScreens.get("new-weapon").addTextElement(new MenuUIText("weapon-unlocked", " has been unlocked!", PApplet.CENTER, PApplet.CENTER, new PVector(this.util.get().width / 2f, this.util.get().height / 7f), this.util.get().width / 48, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));

        createPauseScreen(colours);

    }

    private void createPauseScreen(JSONObject colours) {

        this.uiScreens.get("pause").addBackgroundElement(new MenuUIBackground("pause-background-tint", new PVector(this.util.get().width / 2f, this.util.get().height / 2f), this.util.get().width + 2, this.util.get().height, PApplet.CENTER, this.util.convertJSONArray(colours.getJSONArray("pause-tint")), 0, 0, this.util));

        this.uiScreens.get("pause").addTextElement(new MenuUIText("title-text", "Pause", PApplet.LEFT, PApplet.TOP, new PVector(0, this.util.get().width / 96f), this.util.get().width / 16, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
        this.uiScreens.get("pause").addTextElement(new MenuUIText("licence", "200028234 - 2021", PApplet.LEFT, PApplet.BOTTOM, new PVector(0, this.util.get().height - this.util.get().height / 128f), this.util.get().width / 48, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));

        String[][] pauseMenuButtons = new String[][]{{"resume-game", "Resume Game"}, {"quit-to-menu", "Quit To Menu"}, {"quit-game-application", "Quit To Life"}};
        float startYValue = this.util.get().height / 3f;
        float LayerValue = this.util.get().height / 9f;

        for (int i = 0; i < pauseMenuButtons.length; i++)
            this.uiScreens.get("pause").addButtonElement(new MenuUIButton(pauseMenuButtons[i][0], pauseMenuButtons[i][1], new PVector(0, startYValue + LayerValue * i), this.util.get().width / 1.5f, this.util.get().height / 16f, this.util.get().width / 48, "left-quad", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

    }

    private void createGameOverScreen(boolean positive) {

        JSONObject colours = this.util.loadJSONFile("colours.json");

        this.uiScreens.put("game-over", new MenuScreen());

        String title;
        String message;

        String titleTextColour;

        if (positive) {

            title = "Congratulations!";
            message = "Mission Success!";
            titleTextColour = "uiButtonText";

        } else {

            title = "Game Over";
            message = "Mission Failed!";
            titleTextColour = "negative";

        }


        this.uiScreens.get("game-over").addTextElement(new MenuUIText("game-over-title", title, PApplet.LEFT, PApplet.TOP, new PVector(0, this.util.get().height / 64f), this.util.get().width / 24, this.util.convertJSONArray(colours.getJSONArray("primary")), 0, this.util));
        this.uiScreens.get("game-over").addTextElement(new MenuUIText("game-over-message", message, PApplet.CENTER, PApplet.CENTER, new PVector(this.util.get().width / 2f, this.util.get().height / 6f), this.util.get().width / 24, this.util.convertJSONArray(colours.getJSONArray(titleTextColour)), 0, this.util));

        int count = 0;
        int halfway = 1;
        int positionScaler = 0;
        int XAlignmentIndex = 0;

        float[] xAlignment = {this.util.get().width / 2f - this.util.get().width / 4f, this.util.get().width / 2f + this.util.get().width / 4f};

        for (String statisticName : this.statistics.getStatistics().keySet()) {

            if (count == halfway + 1) {
                positionScaler = 0;
                XAlignmentIndex++;
            }

            this.uiScreens.get("game-over").addTextElement(new MenuUIText("stat-" + count, statisticName + ": " + this.statistics.getStatistics().get(statisticName), PApplet.CENTER, PApplet.CENTER, new PVector(xAlignment[XAlignmentIndex], this.util.get().height / 3f + (this.util.get().height / 16f * positionScaler)), this.util.get().width / 48, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
            count++;
            positionScaler++;

        }

        this.uiScreens.get("game-over").addButtonElement(new MenuUIButton("go-to-menu", "Main Menu", new PVector(this.util.get().width - this.util.get().width / 12f, this.util.get().height - this.util.get().height / 16f), (float) (this.util.get().width / 8), (float) (this.util.get().height / 16), this.util.get().width / 48, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

    }

    private void updateWeaponSlots() {

        for (String weaponSlotReference : this.currentWeaponNames) {

            this.uiScreens.get("hud").removeElement(weaponSlotReference + "-background");
            this.uiScreens.get("hud").removeElement(weaponSlotReference + "-image");
            this.uiScreens.get("hud").removeElement(weaponSlotReference + "-text");

        }
        this.currentWeaponNames.clear();

        JSONObject colours = this.util.loadJSONFile("colours.json");
        String[] playerWeapons = this.characters.get("player").getWeapons();
        float heightOffset = (float) this.util.get().height / 18;
        for (int i = playerWeapons.length - 1; i > -1; i--) {

            int[] backgroundColour;
            if (this.characters.get("player").getCurrentWeapon().equals(playerWeapons[i]))
                backgroundColour = this.util.convertJSONArray(colours.getJSONArray("primary-transparent-00"));
            else
                backgroundColour = this.util.convertJSONArray(colours.getJSONArray("primary-transparent-01"));

            this.uiScreens.get("hud").addBackgroundElement(new MenuUIBackground("weapon-slot-" + i + "-background", new PVector(this.util.get().width - (float) (this.util.get().width / 24), (this.util.get().height - heightOffset)), (float) (this.util.get().width / 12), (float) (this.util.get().height / 9), PApplet.CENTER, backgroundColour, 255, this.util.get().width / 512, this.util));
            this.uiScreens.get("hud").addImageElement(new MenuUIImage("weapon-slot-" + i + "-image", new PVector(this.util.get().width - (float) (this.util.get().width / 32), (this.util.get().height - heightOffset)), "hud", 24, "weapon-" + playerWeapons[i], this.util));
            this.uiScreens.get("hud").addTextElement(new MenuUIText("weapon-slot-" + i + "-text", (i + 1) + ".", PApplet.RIGHT, PApplet.CENTER, new PVector(this.util.get().width - (float) (this.util.get().width / 18), (this.util.get().height - heightOffset)), this.util.get().width / 64, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));

            this.currentWeaponNames.add("weapon-slot-" + ((playerWeapons.length - 1) - i));
            heightOffset += (float) (this.util.get().height / 9);

        }

    }

    private void updateWeaponSlotColours() {

        GameCharacter player = this.characters.get("player");
        JSONObject colours = this.util.loadJSONFile("colours.json");
        int count = 0;

        for (String weapon : this.currentWeaponNames) {

            int[] backgroundColour;
            if (player.getCurrentWeapon().equals(player.getWeapons()[count]))
                backgroundColour = this.util.convertJSONArray(colours.getJSONArray("primary-transparent-00"));
            else
                backgroundColour = this.util.convertJSONArray(colours.getJSONArray("primary-transparent-01"));

            this.uiScreens.get("hud").setBackgroundColour(weapon + "-background", backgroundColour);
            count++;
        }
        changeAmmoText();
    }

    private boolean isOutOfBounds(PVector position) {

        boolean result = true;

        float xMinimum = this.currentWorld.getWorldZero().x;
        float yMinimum = this.currentWorld.getWorldZero().y;

        float xMaximum = this.currentWorld.getWorldZero().x + this.currentWorld.getWorldWidth();
        float yMaximum = this.currentWorld.getWorldZero().y + this.currentWorld.getWorldHeight();

        if (position.x > xMinimum && position.x < xMaximum &&
                position.y > yMinimum && position.y < yMaximum)
            result = false;

        return result;

    }

    public void createAgent() {

        String name = String.valueOf(System.currentTimeMillis());

        String[] agents = {"agent00", "agent01", "agent02"};
        String[] skins = {"a", "b", "c"};
        String[] weapons;

        switch (this.numberOfAliensToFind) {
            case 3:
                weapons = new String[]{"glock"};
                break;
            case 2:
                weapons = new String[]{"glock", "desert-eagle"};
                break;
            case 1:
                weapons = new String[]{"glock", "desert-eagle", "mp5"};
                break;
            default:
                weapons = new String[]{"glock", "desert-eagle", "mp5", "rpg"};
                break;

        }

        PVector spawnPosition = new PVector(this.characters.get("player").position().x, this.characters.get("player").position().y);

        PVector playerPosition = this.characters.get("player").getWorldPosition();
        PVector playerPreviousPosition = this.characters.get("player").getPreviousWorldPosition();

        int angleToSpawn = -1;
        if (this.finalBattleActive || playerPosition.x == playerPreviousPosition.x && playerPosition.y == playerPreviousPosition.y)
            angleToSpawn = this.util.randomInt(-179, 179);
        else if (playerPosition.x > playerPreviousPosition.x && playerPosition.y == playerPreviousPosition.y) //Player is moving right
            angleToSpawn = 0;
        else if (playerPosition.x < playerPreviousPosition.x && playerPosition.y == playerPreviousPosition.y) //Player is moving left
            angleToSpawn = 180;
        else if (playerPosition.x == playerPreviousPosition.x && playerPosition.y > playerPreviousPosition.y) //Player is moving down
            angleToSpawn = 90;
        else if (playerPosition.x == playerPreviousPosition.x && playerPosition.y < playerPreviousPosition.y) //Player is moving up
            angleToSpawn = -90;
        else if (playerPosition.x < playerPreviousPosition.x && playerPosition.y < playerPreviousPosition.y) //Player is moving up and to the left
            angleToSpawn = -135;
        else if (playerPosition.x > playerPreviousPosition.x && playerPosition.y < playerPreviousPosition.y) //Player is moving up and to the right
            angleToSpawn = -45;
        else if (playerPosition.x < playerPreviousPosition.x && playerPosition.y > playerPreviousPosition.y) //Player is moving down and to the left
            angleToSpawn = 135;
        else if (playerPosition.x > playerPreviousPosition.x && playerPosition.y > playerPreviousPosition.y) //Player is moving down and to the right
            angleToSpawn = 45;

        spawnPosition.x = spawnPosition.x + this.util.get().width * PApplet.cos(PApplet.radians(angleToSpawn));
        spawnPosition.y = spawnPosition.y + this.util.get().width * PApplet.sin(PApplet.radians(angleToSpawn));

        if (isOutOfBounds(spawnPosition)) {
            spawnPosition.x = this.characters.get("player").position().x + this.util.get().width * PApplet.cos(PApplet.radians(-angleToSpawn));
            spawnPosition.y = this.characters.get("player").position().y + this.util.get().width * PApplet.sin(PApplet.radians(-angleToSpawn));

        }

        this.characters.put(name, new GameCharacter(spawnPosition, "agents/" + agents[util.randomInt(0, agents.length - 1)] + "/" + skins[util.randomInt(0, skins.length - 1)], weapons[util.randomInt(0, weapons.length - 1)], 999, "agent", false, this.util));

        if (this.util.randomInt(0, 2) == 0) {
            int fragCountChance = this.util.randomInt(1, 5);
            this.characters.get(name).addWeapon("frag", new GameCharacterWeapon("frag", fragCountChance, this.util));
        }

    }

    private String getCharacterDirection(GameCharacter character) {

        String result = "";

        PVector characterPosition = character.getWorldPosition();
        PVector characterPreviousPosition = character.getPreviousWorldPosition();

        if (characterPosition.x > characterPreviousPosition.x && characterPosition.y == characterPreviousPosition.y) //Character is moving right
            result = "right";

        else if (characterPosition.x < characterPreviousPosition.x && characterPosition.y == characterPreviousPosition.y) //Character is moving left
            result = "left";

        else if (characterPosition.x == characterPreviousPosition.x && characterPosition.y > characterPreviousPosition.y) //Character is moving down
            result = "down";

        else if (characterPosition.x == characterPreviousPosition.x && characterPosition.y < characterPreviousPosition.y) //Character is moving up
            result = "up";

        return result;
    }

    public void update() {

        if (this.gameState.get("active")) {

            if (this.input.getLastKeyUp("pause"))
                this.gamePaused = !this.gamePaused;

            if (!this.gamePaused) {

                this.util.get().noCursor();

            } else {

                this.util.get().cursor();
                pauseMenuControl();
            }

            if (!this.gameWon && !this.gamePaused)
                playerControl();

            if (!this.gamePaused) {
                this.combatDirectorCounter++;
                if (this.combatDirectorCounter == 600) {

                    int numberToSpawn = this.combatDirector.numberOfEnemiesToSpawn(this.characters);
                    for (int i = 0; i < numberToSpawn; i++)
                        createAgent();


                    this.combatDirectorCounter = 0;
                }
            }

            GameCharacter player = this.characters.get("player");

            //==========================================================================================================
            //Chunk information

            float[] amountWorldChanged = this.currentWorld.getAmountChanged(); //Gets the value that any dynamic object should move due to the player moving throughout the world.
            this.currentWorld.integrateToWorld(amountWorldChanged);
            this.currentWorld.draw();

            if (!this.gamePaused) {
                this.chunkUpdateCounter++;
                if (this.chunkUpdateCounter == 180) {
                    this.currentChunk = this.getChunk(currentChunk(this.characters.get("player")));
                    this.chunkUpdateCounter = 0;
                }
            }

            //==========================================================================================================
            //Chunk debug draw
            for (Chunk chunk : this.chunks) {
                chunk.integrateToWorld(amountWorldChanged);
                if (this.debugDrawMode)
                    chunk.debugDraw();
            }

            //==========================================================================================================
            //World Objects
            for (Chunk chunk : this.chunks) {

                for (GameWorldObject worldObject : chunk.getWorldObjects()) {

                    worldObject.integrateToWorld(amountWorldChanged);

                    if (chunkIsLoaded(chunk.getID())) {
                        worldObject.draw();

                    }
                }
            }

            //==========================================================================================================
            //Drawing Projectiles
            ArrayList<GameCharacterProjectile> activeProjectiles = new ArrayList<>();
            for (GameCharacterProjectile projectile : this.projectiles) {

                projectile.integrateToWorld(amountWorldChanged);

                if (this.debugDrawMode && projectile.getWeaponType().equals("explosive")) //If debug mode is active and it is an explosive
                    projectile.drawDebugOutline(); //Draw an outline of the maximum blast radius.


                projectile.integrate(this.gamePaused);

                if (projectile.isActive())
                    activeProjectiles.add(projectile);
            }

            this.projectiles = activeProjectiles;

            //==========================================================================================================
            //Drawing Characters
            HashMap<String, GameCharacter> aliveCharacters = new HashMap<>();
            for (String keyName : this.characters.keySet()) {

                GameCharacter character = this.characters.get(keyName);

                if (character.getFaction().equals("agent") || character.getFaction().equals("alien") && !this.gameWon) {

                    if (!keyName.equals("player")) {
                        character.integrateToWorld(amountWorldChanged);
                        if (this.util.findDistance(new float[]{player.position().x, player.position().y}, new float[]{character.position().x, character.position().y}) <= this.util.get().width && character.getFaction().equals("agent") || character.getFaction().equals("alien")) {

                            if (!this.gamePaused)
                                character.aiManager(getCharactersInRange(character), player.position(), isAtEdge(character, getCharacterDirection(character)));
                            character.update();

                        }

                    } else { //If the looping character is the player

                        character.update();
                        character.setWorldPosition(this.currentWorld.getWorldZero());

                    }

                }
                if (character.hasFired())
                    this.projectiles.add(character.getFiredShot());

                if (!this.gamePaused) {

                    for (GameCharacterProjectile projectile : this.projectiles) {

                        float playerProjectileDistance = this.util.findDistance(new float[]{character.position().x, character.position().y}, new float[]{projectile.position().x, projectile.position().y});

                        if (playerProjectileDistance < (float) (this.util.get().width / 4) && character != projectile.getOwnerObject() && projectile.getWeaponType().equals("gun")) {

                            if (areColliding(character.position().x, character.position().y, character.getSize()[0], character.getSize()[1], projectile.position().x, projectile.position().y, projectile.getSize()[0], projectile.getSize()[1])) {

                                if (!projectile.getFaction().equals(character.getFaction())) {

                                    if (character.getFaction().equals("agent") || character.getFaction().equals("alien") && character.isPlayer())
                                        character.changeHealth(-projectile.getDamage(), projectile.position().x < character.position().x);

                                    if (character.isPlayer())
                                        this.statistics.increaseStatistic("Damage Taken", projectile.getDamage());

                                    if (projectile.getOwnerObject().isPlayer() && character.getFaction().equals("agent"))
                                        this.statistics.increaseStatistic("Damage Dealt", projectile.getDamage());

                                    projectile.setActive(false);
                                    if (character.isDying() && !character.isItemDispensed() && !character.isPlayer()) {
                                        spawnKillItem(character);
                                    }

                                    if (projectile.getOwnerObject().isPlayer())
                                        this.statistics.increaseStatistic("Agents Killed", 1);


                                }

                            }

                        } else if (projectile.isExploding() && playerProjectileDistance < (float) (this.util.get().width / projectile.getBlastRadius())) {

                            if (!projectile.getFaction().equals(character.getFaction())) {
                                int projectileDamage = -(projectile.getDamage() + 100 / (int) playerProjectileDistance); //Calculates the damage the explosive the grenade should do based on the distance and the base damage

                                if (character.getFaction().equals("agent") || character.getFaction().equals("alien") && character.isPlayer())
                                    character.changeHealth(projectileDamage, projectile.position().x < character.position().x);

                                if (character.isPlayer())
                                    this.statistics.increaseStatistic("Damage Taken", projectileDamage);

                                if (projectile.getOwnerObject().isPlayer() && character.getFaction().equals("agent"))
                                    this.statistics.increaseStatistic("Damage Dealt", projectileDamage);

                                if (character.isDying() && !character.isItemDispensed())
                                    spawnKillItem(character);

                                if (projectile.getOwnerObject().isPlayer())
                                    this.statistics.increaseStatistic("Agents Killed", 1);
                            }

                        }


                    }


                    if (!this.characters.get(keyName).isDead())
                        aliveCharacters.put(keyName, this.characters.get(keyName));

                    //Testing the player collisions of Pickups
                    for (GamePickup pickup : getChunk(currentChunk(this.characters.get("player"))).getPickups()) {

                        if (areColliding(character.position().x, character.position().y, character.getSize()[0], character.getSize()[1], pickup.position().x, pickup.position().y, pickup.getSize()[0], pickup.getSize()[1])) {

                            if (pickup.getPickupType().equals("health") && character.isPlayer()) {

                                character.changeHealth(pickup.getAmount(), false);
                                pickup.setActive(false);

                            } else if (character.weaponExists(pickup.getPickupType())) {

                                character.addAmmo(pickup.getPickupType(), pickup.getAmount());
                                pickup.setActive(false);

                            }


                        }

                    }

                }

            }

            if (!this.gamePaused) {

                if (player.isDead()) {
                    createGameOverScreen(false);
                    changeGameState("game-over");
                } else {
                    this.characters = aliveCharacters;
                }

                if (player.healthChanged())
                    this.uiScreens.get("hud").setText("health", player.getHealth().toString());

                if (player.ammoChanged())
                    changeAmmoText();

                if (player.weaponChanged())
                    updateWeaponSlotColours();

                if (player.weaponAdded())
                    updateWeaponSlots();

            }

            //==========================================================================================================
            //Drawing Cages.

            for (Chunk chunk : this.chunks) {
                ArrayList<GameCage> activeCages = new ArrayList<>();
                for (GameCage cage : chunk.getCages()) {
                    cage.integrateToWorld(amountWorldChanged);
                    if (chunkIsLoaded(chunk.getID())) {
                        cage.draw();
                        for (GameCharacterProjectile projectile : this.projectiles) {
                            if (projectile.getWeaponType().equals("explosive")) {
                                if (projectile.isExploding() &&
                                        (this.util.findDistance(new float[]{cage.getPosition().x, cage.getPosition().y}, new float[]{projectile.position().x, projectile.position().y}) - (cage.getDimensions()[0] / 2)) < (float) (this.util.get().width / projectile.getBlastRadius())) {
                                    projectile.setActive(false);
                                    cage.hit(projectile.getDamage());
                                }
                            } else if (areColliding(cage.getPosition().x, cage.getPosition().y, cage.getDimensions()[0], cage.getDimensions()[1], projectile.position().x, projectile.position().y, projectile.getSize()[0], projectile.getSize()[1])) {
                                if (projectile.getWeaponType().equals("gun")) {
                                    projectile.setActive(false);
                                    cage.hit(projectile.getDamage());
                                }
                            }
                        }

                    }

                    if (cage.isActive()) {

                        activeCages.add(cage);

                    } else {

                        rescueCharacter(cage);


                    }
                    chunk.rebindCages(activeCages);
                }
            }

            //==========================================================================================================
            //Drawing Pickups

            for (Chunk chunk : this.chunks) {

                if (!chunkIsLoaded(chunk.getID()))
                    chunk.getPickups().clear(); //Clears the items from chunks which are not loaded.

                ArrayList<GamePickup> activePickups = new ArrayList<>();
                for (GamePickup pickup : chunk.getPickups()) {
                    pickup.integrateToWorld(amountWorldChanged);

                    if (chunkIsLoaded(chunk.getID()))
                        pickup.draw();

                    if (pickup.isActive())
                        activePickups.add(pickup);
                }
                chunk.rebindPickups(activePickups);
            }


            //==========================================================================================================
            //Drawing UFO
            for (Chunk chunk : this.chunks) {
                for (GameUFO ufo : chunk.getUFOs()) {
                    ufo.integrateToWorld(amountWorldChanged);
                    if (chunkIsLoaded(chunk.getID())) {
                        ufo.draw();

                        int[] ufoTextColour = {255, 255, 255};
                        if (chunkIsSnowBiome(chunk.getID()))
                            ufoTextColour = new int[]{0, 0, 0};

                        if (ufo.isCharging())
                            ufo.drawText("Power: " + ufo.getPowerLevel() + "%", ufoTextColour);

                        if (this.util.findDistance(new float[]{player.position().x, player.position().y}, new float[]{ufo.position().x, ufo.position().y}) < this.util.get().width / 6f && !this.gameWon) {

                            if (this.numberOfAliensToFind > 0) {
                                ufo.drawText("You cannot fly this without the whole crew...", ufoTextColour);
                            } else if (!ufo.isCharging() && ufo.getPowerLevel() == 0) {
                                ufo.drawText("Begin Charging? [E]", ufoTextColour);
                                this.activateAction = "charge-ufo";
                            } else if (ufo.getPowerLevel() == 100) {
                                ufo.drawText("Enter Spacecraft and Take-off? [E]", ufoTextColour);
                                this.activateAction = "enter-ufo";
                            }

                        } else if (ufo.getPowerLevel() == 100 && !this.gameWon) {
                            ufo.drawText("Ready for take-off...", ufoTextColour);
                        }

                    }

                }

            }

            //==========================================================================================================
            //Drawing HUD

            if (!this.gamePaused) {


                if (chunkIsSnowBiome(this.currentChunk(player))) {

                    for (String element : this.dynamicHUDElements)
                        this.uiScreens.get("hud").setTextColour(element, new int[]{0, 0, 0, 255});

                } else {

                    for (String element : this.dynamicHUDElements)
                        this.uiScreens.get("hud").setTextColour(element, new int[]{255, 255, 255, 255});

                }
                this.uiScreens.get("hud").draw();

            } else {

                this.uiScreens.get("pause").draw();

            }

            if (this.debugMode) {

                this.frameCountChange++;

                if (this.frameCountChange >= 60) {
                    this.uiScreens.get("debug-mode").setText("fps-counter", "FPS: " + PApplet.round(this.util.get().frameRate));
                    this.frameCountChange = 0;
                }

                this.uiScreens.get("debug-mode").setText("player-position", "x:" + Math.round(this.characters.get("player").position().x - this.currentWorld.getWorldZero().x) + " y: " + Math.round(this.characters.get("player").position().y - this.currentWorld.getWorldZero().y));
                this.uiScreens.get("debug-mode").setText("chunk", "Chunk: " + this.currentChunk(this.characters.get("player")));
                this.uiScreens.get("debug-mode").draw();

            }

            if (!this.gamePaused) {

                if (this.weaponUnlockMode) {
                    this.uiScreens.get("new-weapon").draw();
                    this.weaponUnlockMessageCounter++;
                    if (this.weaponUnlockMessageCounter == 480) {
                        this.weaponUnlockMode = false;
                        this.weaponUnlockMessageCounter = 0;
                    }
                }

                if (!this.keyboardArmRotate) {

                    float gunOffsetX = 0.0f;
                    float gunOffsetY = 0.0f;
                    switch (player.getDirection()) {
                        case "up":
                            gunOffsetX = (float) (-player.getSize()[0] / 2.6);
                            gunOffsetY = -player.getSize()[1] / 24;
                            break;
                        case "down":
                            gunOffsetX = (float) (player.getSize()[0] / 2.6);
                            gunOffsetY = -player.getSize()[1] / 24;
                            break;
                        case "left":
                            gunOffsetX = player.getSize()[0] / 32;
                            gunOffsetY = -player.getSize()[1] / 8;
                            break;
                        case "right":
                            gunOffsetX = -player.getSize()[0] / 32;
                            gunOffsetY = -player.getSize()[1] / 8;
                            break;
                    }

                    this.crosshair.draw(new PVector(this.util.get().mouseX + gunOffsetX, this.util.get().mouseY + gunOffsetY));

                }

            }

            //======================================================================================================
            //Fade out screen.

            if (this.gameWon) {
                this.gameWonSequenceCounter++;
                if (this.gameWonSequenceCounter > 120) {
                    this.fadeout.progressFade();
                    if (this.fadeout.isFullyFaded()) {
                        createGameOverScreen(true);
                        changeGameState("game-over");
                    }
                }
            }

            if (!this.fadeIn.isFullyFaded())
                this.fadeIn.progressFade();

            if (this.quitGameFadeOutActive) {

                this.quitGameFadeOut.progressFade();

                if (this.quitGameFadeOut.isFullyFaded()) {
                    if (this.quitGameAction.equals("menu"))
                        this.active = false;
                    else
                        this.util.get().exit();
                }

            }

        } else if (this.gameState.get("game-over")) {

            this.util.get().cursor();
            this.uiScreens.get("game-over").draw();

            if (this.input.fullLeftClick()) { //If the left mouse button is clicked

                String buttonClicked = this.uiScreens.get("game-over").onClick(); //Gets the id of the object the mouse is on, if any.

                if (buttonClicked != null)
                    events(buttonClicked); //Calls the function to run the event relating to that ID.

            }

            if (this.input.getLastKeyUp("select"))
                events(this.uiScreens.get("game-over").getActiveButtonID());

            if (this.gameOverFadeoutActive) {
                this.gameOverFadeout.progressFade();
                if (this.gameOverFadeout.isFullyFaded()) {
                    this.gameEndCounter++;
                    if (this.gameEndCounter == 120)
                        this.active = false;

                }
            }

        }
    }

    private void events(String buttonID) {

        switch (buttonID) {
            case "go-to-menu":
                this.gameOverFadeoutActive = true;
                break;
            case "resume-game":
                this.gamePaused = false;
                break;
            case "quit-to-menu":
                this.quitGameFadeOutActive = true;
                this.quitGameAction = "menu";
                break;
            case "quit-game-application":
                this.quitGameFadeOutActive = true;
                this.quitGameAction = "life";
                break;
        }

    }

    private void playerControl() {

        GameCharacter playerObject = this.characters.get("player");

        //Both the widthPadding and heightPadding are required so that the edge main
        // background graphic does not go too far over showing the black canvas behind.
        int widthPadding = this.util.get().width / 1024;
        int heightPadding = this.util.get().height / 1024;

        //This is the code that checks if the player at the furthest point of the world on the X axis.
        if (this.currentWorld.getWorldX() - (this.currentWorld.getWorldWidth() / 2) > -widthPadding || this.currentWorld.getWorldX() + (this.currentWorld.getWorldWidth() / 2) < this.util.get().width + widthPadding) {
            if (!this.worldLocks.get("x")) {
                setWorldLock("x", true);
            } else if (playerObject.position().x == this.util.get().width / 2f || playerObject.position().x == this.util.get().width / 2f) {
                setWorldLock("x", false);
            }
        }

        //This is the code that checks if the player at the furthest point of the world on the Y axis.
        if (this.currentWorld.getWorldY() - (this.currentWorld.getWorldHeight() / 2) > -heightPadding || this.currentWorld.getWorldY() + (this.currentWorld.getWorldHeight() / 2) < this.util.get().height + heightPadding) {
            if (!this.worldLocks.get("y")) {
                setWorldLock("y", true);
            } else if (playerObject.position().y == this.util.get().height / 2f || playerObject.position().y == this.util.get().height / 2f) {
                setWorldLock("y", false);
            }
        }

        boolean[] keyStates = {input.key("moveUp"), input.key("moveLeft"), input.key("moveDown"), input.key("moveRight")};

        int directionCount = 0;
        for (boolean isPressed : keyStates) {
            if (isPressed)
                directionCount++;
        }

        if (directionCount > 0 && noConflictingKeys(keyStates) && !this.characters.get("player").isDying()) {

            if (input.key("moveUp")) {
                if (this.worldLocks.get("y")) {
                    if (!isAtEdge(this.characters.get("player"), "up"))
                        playerObject.move("up");
                } else {
                    playerObject.startMoving();
                    this.currentWorld.move("down", playerObject.getSpeed());
                }

            }

            if (input.key("moveLeft")) {
                if (this.worldLocks.get("x")) {
                    if (!isAtEdge(this.characters.get("player"), "left"))
                        playerObject.move("left");
                } else {
                    playerObject.startMoving();
                    this.currentWorld.move("right", playerObject.getSpeed());
                }
            }

            if (input.key("moveDown")) {
                if (this.worldLocks.get("y")) {
                    if (!isAtEdge(this.characters.get("player"), "down"))
                        playerObject.move("down");
                } else {
                    playerObject.startMoving();
                    this.currentWorld.move("up", playerObject.getSpeed());
                }
            }

            if (input.key("moveRight")) {
                if (this.worldLocks.get("x")) {
                    if (!isAtEdge(this.characters.get("player"), "right"))
                        playerObject.move("right");
                } else {
                    playerObject.startMoving();
                    this.currentWorld.move("left", playerObject.getSpeed());
                }
            }

        } else {

            playerObject.move("idle");

        }


        if (!this.keyboardArmRotate) {
            if (input.key("rotateLeft") || input.key("rotateRight"))
                this.keyboardArmRotate = true;

            this.armRotateMouseCoords[0] = this.util.get().mouseX;
            this.armRotateMouseCoords[1] = this.util.get().mouseY;
        } else {

            if (this.util.findDistance(this.armRotateMouseCoords, new float[]{this.util.get().mouseX, this.util.get().mouseY}) > 50)
                this.keyboardArmRotate = false;

        }

        int angle = playerObject.getAngle();
        if (this.keyboardArmRotate) {
            if (input.key("rotateLeft"))
                angle = playerObject.getAngle() - 4;
            else if (input.key("rotateRight"))
                angle = playerObject.getAngle() + 4;

            if (angle > 180)
                angle = -180;
            else if (angle < -180)
                angle = 180;

        } else {

            angle = this.util.findAngle(new float[]{this.util.get().mouseX, this.util.get().mouseY}, new float[]{playerObject.position().x, playerObject.position().y});

        }

        playerObject.setAngle(angle);

        if (input.key("fire") || input.key("left"))
            playerObject.fire(true);

        if (input.key("right"))
            if (playerObject.getCurrentWeaponObject().hasAlternativeFire())
                playerObject.fire(false);

        if (input.key("weaponSlot1") || input.key("weaponSlot1Alt"))
            setPlayerWeapon(0, false);
        else if (input.key("weaponSlot2") || input.key("weaponSlot2Alt"))
            setPlayerWeapon(1, false);
        if (input.key("weaponSlot3") || input.key("weaponSlot3Alt"))
            setPlayerWeapon(2, false);
        else if (input.key("weaponSlot4") || input.key("weaponSlot4Alt"))
            setPlayerWeapon(3, false);
        if (input.key("weaponSlot5") || input.key("weaponSlot5Alt"))
            setPlayerWeapon(4, false);
        else if (input.key("weaponSlot6") || input.key("weaponSlot6Alt"))
            setPlayerWeapon(5, false);
        if (input.key("weaponSlot7") || input.key("weaponSlot7Alt"))
            setPlayerWeapon(6, false);
        else if (input.key("weaponSlot8") || input.key("weaponSlot8Alt"))
            setPlayerWeapon(7, false);
        else if (input.key("weaponSlot9") || input.key("weaponSlot9Alt"))
            setPlayerWeapon(8, false);

        if (input.getLastKeyUp("developerConsole"))
            this.debugMode = !this.debugMode;

        if (input.getLastKeyUp("debugDrawMode"))
            this.debugDrawMode = !this.debugDrawMode;

        if (input.mouseWheelUp())
            setPlayerWeapon(playerObject.getCurrentWeaponIndex() - 1, true);

        if (input.mouseWheelDown())
            setPlayerWeapon(playerObject.getCurrentWeaponIndex() + 1, true);

        if (input.getLastKeyUp("activate")) {

            if (!this.activateAction.equals("")) {

                switch (this.activateAction) {
                    case "enter-ufo":
                        this.gameWon = true;
                        break;
                    case "charge-ufo":
                        for (Chunk chunk : this.chunks) {
                            if (chunk.getUFOs().size() > 0) {
                                chunk.getUFOs().get(0).initialiseChargeMode();
                                createFinalShowdown();

                            }
                        }
                        break;

                }

            }

        }

    }

    private void createFinalShowdown() {

        for (GameCharacter character : this.characters.values())
            if (this.util.findDistance(new float[]{character.position().x, character.position().y}, new float[]{this.characters.get("player").position().x, this.characters.get("player").position().y}) > this.util.get().width)
                character.setActive(false);

        for (int i = 0; i < 6; i++)
            createAgent();

        this.finalBattleActive = true;

    }

    /**
     * This function checks if two keys are pressed that would cause problems for world integration.
     * For instance, returns false if the left and right direction keys are pressed. (Or up and down)
     *
     * @param keyStates The states of the direction move keys.
     * @return Whether two illegal keys are pressed. True if not, false if yes.
     */
    private boolean noConflictingKeys(boolean[] keyStates) {

        boolean result = true;
        if (keyStates[0] && keyStates[2] || keyStates[1] && keyStates[3])
            result = false;

        return result;
    }

    private void setWorldLock(String direction, Boolean lockStatus) {
        if (direction.equals("x") || direction.equals("y"))
            this.worldLocks.put(direction, lockStatus);
    }

    private boolean isAtEdge(GameCharacter character, String direction) {

        boolean result = false;

        switch (direction) {

            case "up":
                result = character.position().y < 0 + character.getSize()[1] / 2;
                break;
            case "down":
                result = character.position().y > this.util.get().height - character.getSize()[1] / 2;
                break;
            case "left":
                result = character.position().x < 0 + character.getSize()[0] / 2;
                break;
            case "right":
                result = character.position().x > this.util.get().width - character.getSize()[0] / 2;
                break;

        }

        return result;

    }

    private void setPlayerWeapon(int index, boolean mouseWheel) {

        int weaponIndex;
        if (index > this.characters.get("player").getWeapons().length - 1 && mouseWheel)
            weaponIndex = 0;
        else if (index < 0 && mouseWheel)
            weaponIndex = this.characters.get("player").getWeapons().length - 1;
        else
            weaponIndex = index;

        if (weaponIndex < this.characters.get("player").getWeapons().length && !this.characters.get("player").getCurrentWeapon().equals(this.characters.get("player").getWeapons()[weaponIndex])) //Checks to make sure the player has more than 1 weapon.
            this.characters.get("player").setCurrentWeapon(weaponIndex);
    }

    public boolean areColliding(float objectOneX, float objectOneY, float objectOneWidth, float objectOneHeight, float objectTwoX, float objectTwoY, float objectTwoWidth, float objectTwoHeight) {

        return objectTwoX + objectTwoWidth / 2 > objectOneX - objectOneWidth / 2 &&
                objectTwoX - objectTwoWidth / 2 < objectOneX + objectOneWidth / 2 &&
                objectTwoY + objectTwoHeight / 2 > objectOneY - objectOneHeight / 2 &&
                objectTwoY - objectTwoHeight / 2 < objectOneY + objectOneHeight / 2;
    }

    private void changeAmmoText() {

        Integer[] ammoValues = this.characters.get("player").getAmmo();
        if (ammoValues[1] == 999999999)
            this.uiScreens.get("hud").setText("ammo", ammoValues[0].toString());
        else
            this.uiScreens.get("hud").setText("ammo", ammoValues[0].toString() + "/" + ammoValues[1].toString());

    }

    public void changeGameState(String newState) {
        for (String i : this.gameState.keySet()) {
            if (this.gameState.get(i))
                this.gameState.put(i, false);
            else if (!this.gameState.get(i) && i.equals(newState))
                this.gameState.put(i, true);
        }

    }

    private ArrayList<GameCharacter> getCharactersInRange(GameCharacter currentCharacter) {

        ArrayList<GameCharacter> charactersInRange = new ArrayList<>();

        if (this.finalBattleActive) {

            for (GameCharacter character : this.characters.values())
                if (character.position() != currentCharacter.position())
                    charactersInRange.add(character);

        } else {

            for (GameCharacter character : this.characters.values())
                if (character.position() != currentCharacter.position() && //Ensures the characters does not register itself.
                        this.util.findDistance(new float[]{currentCharacter.position().x, currentCharacter.position().y}, new float[]{character.position().x, character.position().y}) < this.util.get().width / 2f) //Checks the character is in range.
                    charactersInRange.add(character);


        }

        return charactersInRange;

    }

    private int currentChunk(GameCharacter character) {

        int result = this.currentChunk.getID();

        for (Chunk chunk : this.chunks) {

            if (character.position().x > chunk.getBounds().get(0).x && character.position().x < chunk.getBounds().get(1).x) {

                if (character.position().y > chunk.getBounds().get(0).y && character.position().y < chunk.getBounds().get(2).y) {

                    result = chunk.getID();
                    break;

                }

            }

        }

        return result;

    }


    private int[] getLoadedChunks() {

        ArrayList<Integer> loadedChunks = new ArrayList<>();

        loadedChunks.add(this.currentChunk.getID());

        for (Chunk chunk : this.currentChunk.getNeighbours())
            loadedChunks.add(chunk.getID());

        int[] finalChunks = new int[loadedChunks.size()];

        for (int i = 0; i < loadedChunks.size(); i++)
            finalChunks[i] = loadedChunks.get(i);

        return finalChunks;

    }

    private boolean chunkIsLoaded(int chunkID) {

        boolean result = false;

        for (int loadedChunk : getLoadedChunks()) {
            if (loadedChunk == chunkID) {
                result = true;
                break;
            }
        }
        return result;
    }

    private void spawnKillItem(GameCharacter deadNPC) {


        deadNPC.setItemDispensed(true);
        GameCharacter player = this.characters.get("player");
        int[] healthAmounts = {20, 40, 60};
        int healthDispensed = 0;

        if (player.getHealth() < 40)
            healthDispensed = healthAmounts[this.util.randomInt(0, healthAmounts.length - 1)];
        else if (player.getHealth() < 80 && player.getHealth() >= 60)
            healthDispensed = healthAmounts[this.util.randomInt(0, 1)];
        else if (player.getHealth() < 60 && player.getHealth() >= 40)
            healthDispensed = healthAmounts[this.util.randomInt(0, 2)];


        if (healthDispensed != 0)
            this.chunks.get(currentChunk(deadNPC)).addPickup(new GamePickup(new PVector(deadNPC.position().x, deadNPC.position().y), "health", healthDispensed, this.util));
        else {

            String weapon = player.getWeapons()[this.util.randomInt(0, player.getWeapons().length - 1)];

            if (weapon.equals("alien-mine") && this.numberOfAliensToFind == 3 || player.weaponPrimaryOutOfAmmo())
                weapon = "alien-blaster";

            int ammoAmount = 10;
            switch (weapon) {
                case "alien-blaster":
                    ammoAmount = 16;
                    break;
                case "desert-eagle":
                    ammoAmount = 8;
                    break;
                case "invisibility":
                case "shape-shift":
                    ammoAmount = 300;
                    break;
                case "mp5":
                    ammoAmount = 128;
                    break;
                case "rpg":
                    ammoAmount = 7;
                    break;
            }

            this.chunks.get(currentChunk(deadNPC)).addPickup(new GamePickup(new PVector(deadNPC.position().x, deadNPC.position().y), weapon, ammoAmount, this.util));

        }

    }


    private void rescueCharacter(GameCage cage) {

        this.characters.put(cage.getCharacterName(), new GameCharacter(new PVector(cage.getPosition().x, cage.getPosition().y), "aliens/" + cage.getCharacterName(), "alien-blaster", 32, "alien", false, this.util));
        this.uiScreens.get("hud").setImageElementTint(cage.getCharacterName() + "-icon", false); //Sets the character icon at the bottom left to be un-tinted
        this.numberOfAliensToFind--;
        this.statistics.increaseStatistic("Aliens Rescued", 1);

        if (this.numberOfAliensToFind == 0)
            this.weaponsToUnlock.add("rpg");

        Collections.shuffle(this.weaponsToUnlock);

        if (this.weaponsToUnlock.size() > 0) {

            String weapon = this.weaponsToUnlock.remove(0);
            int ammoCount = 1;

            switch (weapon) {
                case "desert-eagle":
                    ammoCount = 16;
                    break;
                case "invisibility":
                case "shape-shift":
                    ammoCount = 600;
                    break;
                case "mp5":
                    ammoCount = 256;
                    break;
                case "rpg":
                    ammoCount = 7;
                    break;
            }

            this.characters.get("player").addWeapon(weapon, new GameCharacterWeapon(weapon, ammoCount, this.util));


            String processedWeaponName = weapon.replace("-", " ");

            this.uiScreens.get("new-weapon").setText("weapon-unlocked", "\"" + processedWeaponName.replace("-", " ").substring(0, 1).toUpperCase() + processedWeaponName.substring(1) + "\" has been unlocked!");
            this.weaponUnlockMode = true;

        }


    }

    private void pauseMenuControl() {

        if (this.input.fullLeftClick()) { //If the left mouse button is clicked

            String buttonClicked = this.uiScreens.get("pause").onClick(); //Gets the id of the object the mouse is on, if any.

            if (buttonClicked != null)
                events(buttonClicked); //Calls the function to run the event relating to that ID.

        }


        if (this.input.getLastKeyUp("tab") || this.input.getLastKeyUp("actionDown"))
            this.uiScreens.get("pause").changeActiveButton(true);

        if (this.input.getLastKeyUp("actionUp"))
            this.uiScreens.get("pause").changeActiveButton(false);

        if (this.input.getLastKeyUp("select"))
            events(this.uiScreens.get("pause").getActiveButtonID());

    }

    private boolean chunkIsSnowBiome(int chunkNumber) {

        boolean result = false;
        int[] snow = {0, 1, 2, 3, 4, 8, 9, 10, 11, 12, 16, 17, 18, 19, 20};

        for (int i : snow) {
            if (i == chunkNumber) {
                result = true;
                break;
            }

        }

        return result;

    }

}
