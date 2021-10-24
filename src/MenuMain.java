import processing.core.PApplet;
import processing.core.PVector;
import processing.data.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * MenuMain Class
 * This is the core main menu object that sits at the top level of the process for the game.
 * i.e Main(Class) > MainMenu.
 * A menu consists of menu MenuScreens which contain the elements you can see and interact with.
 * All the layouts for the screens and the action inputs are handled in this class.
 */
class MenuMain {

    private final HashMap<String, MenuScreen> screens;
    private String currentScreen;

    private ArrayList<MenuEarth> earths;
    private int earthSpawnMax;
    private int earthSpawnCounter;

    private final MenuMainStarfield starfield;
    private final MenuSpacecraftSequence spacecraftSequence;

    private final HashMap<String, String[]> options;

    private boolean fadeOutActive; //To control if the screen is fading out.
    private String fadeOutAction;


    private boolean active;

    private Input input;
    private final Audio audio;

    private final Utility util;

    private Fade fadeout;

    private String selectedCharacter;

    private ArrayList<String> keybindingIDs;
    private boolean changeKeyBindMode;
    private String keybindingBeingModified;

    public MenuMain(Audio audio, Input input, Utility util) {

        this.util = util;

        this.input = input;
        this.audio = audio;

        this.selectedCharacter = "";

        this.earths = new ArrayList<>();
        this.earthSpawnMax = this.util.randomInt(600, 1200);
        this.earthSpawnCounter = 0;

        this.options = new HashMap<>();

        this.starfield = new MenuMainStarfield(this.util);
        this.spacecraftSequence = new MenuSpacecraftSequence(this.util);

        this.screens = new HashMap<>();

        this.screens.put("main", new MenuScreen());
        this.screens.put("new-game", new MenuScreen());
        this.screens.put("load-game", new MenuScreen());
        this.screens.put("options", new MenuScreen());

        JSONObject colours = this.util.loadJSONFile("colours.json"); //Gets all the colours defined in colours.json to pass to the different pages.
        addMainElements(colours);
        addNewGameElements(colours);

        addLoadGameElements(colours);

        this.currentScreen = "main"; //Ensures the main screen is the first screen shown


        this.fadeOutActive = false;
        this.fadeOutAction = "quit";

        this.fadeout = new Fade(this.util, 3, 300, true);

        this.audio.loadAudioCache(new String[]{"main-menu"});
        this.audio.play("theme");

        this.active = true;

        this.util.get().cursor();


        this.keybindingIDs = new ArrayList<>();


    }

    public boolean isActive() {

        return this.active;

    }

    private void addMainElements(JSONObject colours) {

        //ID, text, alignmentX, alignmentY, position, size, backgroundColour, stroke
        this.screens.get("main").addTextElement(new MenuUIText("title-text", "Location: Earth", PApplet.LEFT, PApplet.TOP, new PVector(0, (float) this.util.get().width / 96), this.util.get().width / 16, this.util.convertJSONArray(colours.getJSONArray("primary")), 0, this.util));
        this.screens.get("main").addTextElement(new MenuUIText("licence", "200028234 - 2021", PApplet.LEFT, PApplet.BOTTOM, new PVector(0, this.util.get().height - (float) (this.util.get().height / 128)), this.util.get().width / 48, this.util.convertJSONArray(colours.getJSONArray("primary")), 0, this.util));

        String[][] mainMenuButtons;
        boolean currentGameExists = false;

        float startYValue;

        if (currentGameExists) {

            mainMenuButtons = new String[][]{{"continue", "Continue Game"}, {"load-game", "Load Game"}, {"new-game", "New Game"}, {"options", "Keybind Options"}, {"quit-game", "Quit Game"}};
            startYValue = (float) (this.util.get().height / 3.5); //This stores how fare from the top the top button should be.

        } else {

            mainMenuButtons = new String[][]{{"new-game", "New Game"}, {"options", "Keybind Options"}, {"quit-game", "Quit Game"}};
            startYValue = (float) (this.util.get().height / 3); //This stores how fare from the top the top button should be.

        }

        float LayerValue = (float) (this.util.get().height / 9); //This stores the distance between the buttons.

        //Loops through all the items in mainMenuButtons and places a button relating to each one.
        for (int i = 0; i < mainMenuButtons.length; i++)
            this.screens.get("main").addButtonElement(new MenuUIButton(mainMenuButtons[i][0], mainMenuButtons[i][1], new PVector(this.util.get().width, startYValue + LayerValue * i), (float) (this.util.get().width / 1.5), (float) (this.util.get().height / 16), this.util.get().width / 48, "right-quad", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

    }

    private void addNewGameElements(JSONObject colours) {
        this.screens.get("new-game").addTextElement(new MenuUIText("new-game-title", "New Game", PApplet.LEFT, PApplet.TOP, new PVector(0, (float) this.util.get().width / 96), this.util.get().width / 16, this.util.convertJSONArray(colours.getJSONArray("primary")), 0, this.util));
        this.screens.get("new-game").addButtonElement(new MenuUIButton("new-game-start", "Play", new PVector(this.util.get().width - (float) (this.util.get().width / 12), this.util.get().height - (float) (this.util.get().height / 16)), (float) (this.util.get().width / 8), (float) (this.util.get().height / 16), this.util.get().width / 48, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));
        this.screens.get("new-game").addButtonElement(new MenuUIButton("new-game-back", "Back", new PVector((float) (this.util.get().width / 12), this.util.get().height - (float) (this.util.get().height / 16)), (float) (this.util.get().width / 8), (float) (this.util.get().height / 16), this.util.get().width / 48, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

        this.screens.get("new-game").addBackgroundElement(new MenuUIBackground("select-character-information", new PVector(this.util.get().width / 2f, this.util.get().height / 2f), this.util.get().width / 2.6f, this.util.get().height / 1.5f, PApplet.CENTER, this.util.convertJSONArray(colours.getJSONArray("option-background-alt")), 255, this.util.get().width / 640, this.util));

        String[] characters = {"Captain\nZedekiah", "Science Officer\nShiphrah", "Pilot\nEglon", "Security Officer\nJael"};
        int randomCharacterIndex = this.util.randomInt(0, 3);
        addOptionBox(new PVector((float) (this.util.get().width / 2), (float) (this.util.get().height / 1.265)), "select-character", colours, characters, randomCharacterIndex);
        this.screens.get("new-game").addTextElement(new MenuUIText("character-description", characterDescriptions().get(characters[randomCharacterIndex]), PApplet.LEFT, PApplet.TOP, new PVector((float) (this.util.get().width / 2) - (float) (this.util.get().width / 6), (float) (this.util.get().height / 2)), this.util.get().width / 64, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
        setSelectedCharacter(characters[randomCharacterIndex]);
        this.screens.get("new-game").addImageElement(new MenuUIImage("character-select-preview", new PVector((float) (this.util.get().width / 2), (float) (this.util.get().height / 3)), "menu/character-select", 16, "zedekiah", this.util));
        setCharacterPreviewImage(characters[randomCharacterIndex]);
    }

    private void addOptionBox(PVector position, String globalID, JSONObject colours, String[] options, int optionIndex) {

        this.options.put(globalID, options);
        this.screens.get("new-game").addButtonElement(new MenuUIButton(globalID + "-back", "<", new PVector(position.x - (float) (this.util.get().width / 5.54), position.y), (float) (this.util.get().width / 48), (float) (this.util.get().height / 12), this.util.get().width / 32, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));
        this.screens.get("new-game").addBackgroundElement(new MenuUIBackground(globalID + "-background", position, (float) (this.util.get().width / 2.6), (float) (this.util.get().height / 12), PApplet.CENTER, this.util.convertJSONArray(colours.getJSONArray("option-background")), -1, 0, this.util));
        this.screens.get("new-game").addTextElement(new MenuUIText(globalID + "-text", options[optionIndex], PApplet.CENTER, PApplet.CENTER, new PVector(position.x, position.y - (float) (this.util.get().width / 256)), this.util.get().width / 48, this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), 0, this.util));
        this.screens.get("new-game").addButtonElement(new MenuUIButton(globalID + "-forward", ">", new PVector(position.x + (float) (this.util.get().width / 5.54), position.y), (float) (this.util.get().width / 48), (float) (this.util.get().height / 12), this.util.get().width / 32, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

    }

    private void addLoadGameElements(JSONObject colours) {
        this.screens.get("load-game").addTextElement(new MenuUIText("load-game-title", "Load Game", PApplet.LEFT, PApplet.TOP, new PVector(0, (float) this.util.get().width / 96), this.util.get().width / 16, this.util.convertJSONArray(colours.getJSONArray("primary")), 0, this.util));
        this.screens.get("load-game").addButtonElement(new MenuUIButton("load-game-back", "Back", new PVector((float) (this.util.get().width / 12), this.util.get().height - (float) (this.util.get().height / 16)), (float) (this.util.get().width / 8), (float) (this.util.get().height / 16), this.util.get().width / 48, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));
    }


    private void addOptionsElements() {

        this.screens.put("options", new MenuScreen());

        JSONObject colours = this.util.loadJSONFile("colours.json");

        this.screens.get("options").addTextElement(new MenuUIText("options-title", "Options", PApplet.LEFT, PApplet.TOP, new PVector(0, (float) this.util.get().width / 96), this.util.get().width / 16, this.util.convertJSONArray(colours.getJSONArray("primary")), 0, this.util));
        this.screens.get("options").addButtonElement(new MenuUIButton("options-back", "Back", new PVector((float) (this.util.get().width / 12), this.util.get().height - (float) (this.util.get().height / 16)), (float) (this.util.get().width / 8), (float) (this.util.get().height / 16), this.util.get().width / 48, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

        JSONObject keyBindings = util.loadJSONFile("keybindings/keybindings.json"); //Calls the function to read the keybindings in from keybindings.json.
        JSONObject keycodeReference = util.loadJSONFile("keybindings/keycodes_reference.json");

        float[] XAlignment = {this.util.get().width / 2f - this.util.get().width / 3f, this.util.get().width / 2f, this.util.get().width / 2f + this.util.get().width / 3f};
        int XAlignmentIndex = 0;
        int XAlignmentHalf = keyBindings.size() / 3;
        int count = 0;
        int yMultiplayer = 0;

        this.keybindingIDs = new ArrayList<>();

        for (Object key : keyBindings.keys()) {

            String id = key + "-option";
            String text = key.toString() + ": " + keycodeReference.getString(String.valueOf(keyBindings.getInt(key.toString())));

            this.keybindingIDs.add(id); //Adds the ids to an array list so it can be retrieved later.
            this.screens.get("options").addButtonElement(new MenuUIButton(id, text, new PVector(XAlignment[XAlignmentIndex], this.util.get().height / 6f + (this.util.get().height / 16f * yMultiplayer)), this.util.get().width / 3.5f, this.util.get().height / 24f, this.util.get().width / 64, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

            count++;
            yMultiplayer++;

            if (count == XAlignmentHalf || count == XAlignmentHalf * 2) {

                XAlignmentIndex++;
                yMultiplayer = 0;

            }

        }

        this.screens.get("options").addButtonElement(new MenuUIButton("options-reset-bindings", "Reset", new PVector(this.util.get().width - this.util.get().width / 12f, this.util.get().height - this.util.get().height / 16f), (float) (this.util.get().width / 8), (float) (this.util.get().height / 16), this.util.get().width / 48, "standard", this.util.convertJSONArray(colours.getJSONArray("primary")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadow")), this.util.convertJSONArray(colours.getJSONArray("uiButtonHover")), this.util.convertJSONArray(colours.getJSONArray("uiButtonText")), this.util.convertJSONArray(colours.getJSONArray("uiButtonShadowActive")), this.util));

    }

    public void update() {

        ArrayList<MenuEarth> activeEarths = new ArrayList<>();
        for (MenuEarth earth : this.earths) {
            earth.update();
            if (earth.isActive())
                activeEarths.add(earth);
        }
        this.earths = activeEarths;


        this.earthSpawnCounter++;
        if (this.earthSpawnCounter == this.earthSpawnMax) {
            this.earths.add(new MenuEarth(new PVector(this.util.get().width + (float) (this.util.get().width / 4), this.util.randomInt(this.util.get().width / 16, this.util.get().height - this.util.get().width / 16)), this.util));
            this.earthSpawnCounter = 0;
            this.earthSpawnMax = this.util.randomInt(1800, 2400);
        }

        this.starfield.update(); //Updates and draws the background star objects.

        this.spacecraftSequence.update();

        this.screens.get(this.currentScreen).draw(); //Draws the objects for the current screen.

        if (this.changeKeyBindMode) {

            if (this.util.get().keyPressed) {

                int newKeyCode = this.input.getRawKey();
                JSONObject keyBindings = util.loadJSONFile("keybindings/keybindings.json");
                keyBindings.setInt(this.keybindingBeingModified.substring(0, this.keybindingBeingModified.length() - 7), newKeyCode);
                this.util.get().saveJSONObject(keyBindings, "src/data/keybindings/keybindings.json"); //Rewrites the key binds file with the newly modified keybindings.
                this.changeKeyBindMode = false;
                updateKeyBindList(keyBindings);
                this.input.updateKeyBindHashMaps(keyBindings);

            }

        }

        if (this.input.fullLeftClick()) { //If the left mouse button is clicked

            String buttonClicked = this.screens.get(this.currentScreen).onClick(); //Gets the id of the object the mouse is on, if any.

            if (buttonClicked != null)
                events(buttonClicked); //Calls the function to run the event relating to that ID.

        }

        if (this.input.getLastKeyUp("tab") || this.input.getLastKeyUp("actionDown"))
            this.screens.get(this.currentScreen).changeActiveButton(true); //Calls the method to make the next button active.

        if (this.input.getLastKeyUp("actionUp"))
            this.screens.get(this.currentScreen).changeActiveButton(false); //Calls the method to make the next button active.


        if (this.input.getLastKeyUp("select")) {

            events(this.screens.get(this.currentScreen).getActiveButtonID());
        }

        if (this.fadeOutActive) {

            this.fadeout.progressFade();
            if (this.fadeout.isFullyFaded())
                endFadeOut(this.fadeOutAction);

        }


    }

    //This contains all of the action listeners for the main menu.
    //Any button event gets called here.
    public void events(String buttonID) {

        if (!this.fadeOutActive && !this.keybindingIDs.contains(buttonID)) {
            switch (buttonID) {
                case "new-game":
                case "load-game":
                    this.currentScreen = buttonID;
                    this.screens.get(this.currentScreen).resetActiveButton();
                    break;
                case "options":
                    addOptionsElements();
                    this.currentScreen = buttonID;
                    this.screens.get(this.currentScreen).resetActiveButton();
                    break;
                case "quit-game":
                    this.audio.fadeAllSounds(4000);
                    beginFadeOut("quit");
                    break;
                case "new-game-back":
                case "load-game-back":
                case "options-back":
                    this.changeKeyBindMode = false;
                    this.keybindingBeingModified = "";
                    backToMain();
                    break;
                case "select-character-back":
                    String nextOption = calculateNextOption("select-character", false);
                    changeCharacterDescription(nextOption);
                    setSelectedCharacter(nextOption);
                    this.screens.get(this.currentScreen).setText("select-character-text", nextOption);
                    break;
                case "select-character-forward":
                    String nextOptionForward = calculateNextOption("select-character", true);
                    changeCharacterDescription(nextOptionForward);
                    setSelectedCharacter(nextOptionForward);
                    this.screens.get(this.currentScreen).setText("select-character-text", nextOptionForward);
                    break;
                case "new-game-start":
                    this.audio.fadeAllSounds(4000);
                    beginFadeOut("new-game");
                    break;
                case "options-reset-bindings":
                    JSONObject defaultKeyBindings = util.loadJSONFile("keybindings/keybindings_default.json");
                    this.util.get().saveJSONObject(defaultKeyBindings, "src/data/keybindings/keybindings.json");
                    updateKeyBindList();
                    this.changeKeyBindMode = false;
                    this.keybindingBeingModified = "";
                    break;
            }


        } else {

            if (this.changeKeyBindMode)
                updateKeyBindList();

            this.screens.get("options").setButtonText(buttonID, "Select Key");
            this.keybindingBeingModified = buttonID;
            this.changeKeyBindMode = true;

        }

    }

    private void updateKeyBindList(JSONObject newKeyBinds) {

        this.keybindingBeingModified = "";
        populateKeyBindOptions(newKeyBinds);

    }

    private void populateKeyBindOptions(JSONObject newKeyBinds) {
        JSONObject keycodeReference = util.loadJSONFile("keybindings/keycodes_reference.json");

        for (String id : this.keybindingIDs) {

            String realID = id.substring(0, id.length() - 7);
            String text = realID + ": " + keycodeReference.getString(String.valueOf(newKeyBinds.getInt(realID)));
            this.screens.get("options").setButtonText(id, text);

        }
    }

    private void updateKeyBindList() {

        this.keybindingBeingModified = "";

        JSONObject keyBindings = util.loadJSONFile("keybindings/keybindings.json"); //Calls the function to read the keybindings in from keybindings.json.
        populateKeyBindOptions(keyBindings);

    }

    private String calculateNextOption(String globalID, boolean forward) {

        String result = "";
        String currentText = this.screens.get(this.currentScreen).getText(globalID + "-text");

        if (!currentText.equals("")) {

            String[] options = this.options.get(globalID);
            for (int i = 0; i < options.length; i++) {

                if (options[i].equals(currentText)) {
                    int newIndex;

                    if (forward) {

                        if (i == options.length - 1)
                            newIndex = 0;
                        else
                            newIndex = i + 1;

                    } else {

                        if (i == 0)
                            newIndex = options.length - 1;
                        else
                            newIndex = i - 1;

                    }
                    result = options[newIndex];
                    break;
                }

            }

        }

        return result;

    }

    private void setSelectedCharacter(String characterTitle) {
        String formatted = "";
        switch (characterTitle) {
            case "Captain\nZedekiah":
                formatted = "zedekiah";
                break;
            case "Science Officer\nShiphrah":
                formatted = "shiphrah";
                break;
            case "Pilot\nEglon":
                formatted = "eglon";
                break;
            case "Security Officer\nJael":
                formatted = "jael";
                break;
        }
        this.selectedCharacter = formatted;

    }

    public String getSelectedCharacter() {

        return this.selectedCharacter;

    }

    private HashMap<String, String> characterDescriptions() {
        HashMap<String, String> descriptions = new HashMap<>();
        descriptions.put("Captain\nZedekiah", "The brave captain on the research\nmission. He has many years of\nexperience in leading exploration\nmissions to different planets.");
        descriptions.put("Science Officer\nShiphrah", "The smartest member of the\nresearch mission. She is collecting\ndata from the human race to help\nthe aliens better understand how\nto initiate peaceful contact.");
        descriptions.put("Pilot\nEglon", "The pilot of the spacecraft. He is a\ntechnical expert and can use his\nknowledge of hacking to help in\nmany tight situations.");
        descriptions.put("Security Officer\nJael", "The trained combatant of the\nmission. Her job is to protect the\nother members from any danger\nthat they may come upon.");
        return descriptions;

    }

    private void changeCharacterDescription(String name) {

        this.screens.get(this.currentScreen).setText("character-description", characterDescriptions().get(name));
        setCharacterPreviewImage(name);
    }

    private void setCharacterPreviewImage(String name) {

        switch (name) {
            case "Captain\nZedekiah":
                this.screens.get("new-game").setImageElement("character-select-preview", "zedekiah");
                break;
            case "Science Officer\nShiphrah":
                this.screens.get("new-game").setImageElement("character-select-preview", "shiphrah");
                break;
            case "Pilot\nEglon":
                this.screens.get("new-game").setImageElement("character-select-preview", "eglon");
                break;
            case "Security Officer\nJael":
                this.screens.get("new-game").setImageElement("character-select-preview", "jael");
                break;
        }

    }

    private void beginFadeOut(String action) {

        this.fadeOutAction = action;
        this.fadeOutActive = true;


    }

    private void endFadeOut(String action) {

        switch (action) {
            case "quit":
                this.util.get().exit();
                break;
            case "new-game":
                this.active = false;
                break;

        }

    }

    //Called if the player is navigating back to the main screen.
    private void backToMain() {

        this.currentScreen = "main";
        this.screens.get(this.currentScreen).resetActiveButton();

    }

}
