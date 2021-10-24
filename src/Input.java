import processing.core.PApplet;
import processing.data.JSONObject;

import java.util.HashMap;

/**
 * Input Class
 * The class were all the input is gathered and processed.
 * This does not call or process any game information other than the input of the mouse and keyboard.
 */
class Input {

    private HashMap<Integer, String> keyCodeIDs; //Stores a key code int and the unique ID of a key defined in the keybindings.json file.
    private HashMap<String, Boolean> keyStates; //Stores the unique ID and whether or not they are pressed.
    private final HashMap<Integer, Boolean> mouseStates; //Stores the mouse key ints and a boolean as to whether or not they are pressed.

    private boolean fullLeftClick; //Stores the information about whether the left mouse button has just been released. This is important for menus.
    private boolean fullCenterClick;

    private String lastKeyUp; //Keeps track of any key that has just been released.

    private boolean mouseWheelUp;
    private boolean mouseWheelDown;

    private int rawKey;


    public Input(Utility util) {

        this.keyCodeIDs = new HashMap<>();
        this.keyStates = new HashMap<>();


        JSONObject keyBindings = util.loadJSONFile("keybindings/keybindings.json"); //Calls the function to read the keybindings in from keybindings.json.
        for (Object key : keyBindings.keys()) {

            this.keyCodeIDs.put(keyBindings.getInt(key.toString()), key.toString()); //Stores the codes and the IDs in the hashmap.
            this.keyStates.put(key.toString(), false); //Stores the IDs and the states in the hashmap.

        }


        this.mouseStates = new HashMap<>();
        //Adds the mouse buttons to the mouseStates Hashmap.
        this.mouseStates.put(PApplet.LEFT, false);
        this.mouseStates.put(PApplet.RIGHT, false);
        this.mouseStates.put(PApplet.CENTER, false);

        this.fullLeftClick = false;
        this.fullCenterClick = false;
        this.lastKeyUp = "";

        this.mouseWheelUp = false;
        this.mouseWheelDown = false;
        this.rawKey = -1;
    }

    public void updateKeyBindHashMaps(JSONObject newKeyBinds) {

        this.keyCodeIDs = new HashMap<>();
        this.keyStates = new HashMap<>();

        for (Object key : newKeyBinds.keys()) {

            this.keyCodeIDs.put(newKeyBinds.getInt(key.toString()), key.toString()); //Stores the codes and the IDs in the hashmap.
            this.keyStates.put(key.toString(), false); //Stores the IDs and the states in the hashmap.

        }


    }

    //Called from the Main class.
    public void keyDown(int keyCode) {
        this.rawKey = keyCode;
        if (keyCodeIDs.get(keyCode) != null)
            this.keyStates.put(this.keyCodeIDs.get(keyCode), true); //Sets the key to being pressed in the HashMap.

    }

    public int getRawKey() {

        int key = this.rawKey;
        this.rawKey = -1;
        return key;
    }

    //Called from the Main class.
    public void keyUp(int keyCode) {

        if (keyCodeIDs.get(keyCode) != null) {
            this.keyStates.put(this.keyCodeIDs.get(keyCode), false); //Sets the key to false in the HashMap.
            setLastKeyUp(this.keyCodeIDs.get(keyCode)); //Stores the last key up so one off button hits can carry out actions.
        }

    }

    //Called from the Main class.
    public void mouseDown(int mouseButton) {

        if (mouseButton == PApplet.LEFT)
            this.mouseStates.put(PApplet.LEFT, true);
        else if (mouseButton == PApplet.RIGHT)
            this.mouseStates.put(PApplet.RIGHT, true);
        else if (mouseButton == PApplet.CENTER)
            this.mouseStates.put(PApplet.CENTER, true);
    }

    //Called from the Main class.
    public void mouseUp(int mouseButton) {

        if (mouseButton == PApplet.LEFT) {
            this.mouseStates.put(PApplet.LEFT, false);
            this.setFullLeftClick(true); //Activates the full click mode so an action can occur on the left mouse release.
        } else if (mouseButton == PApplet.RIGHT) {
            this.mouseStates.put(PApplet.RIGHT, false);
        } else if (mouseButton == PApplet.CENTER) {
            this.mouseStates.put(PApplet.CENTER, false);
            this.setFullCenterClick(true);
        }

    }

    public void mouseWheelMoved(float value) {
        if (value < 0)
            this.mouseWheelUp = true;
        else if (value > 0)
            this.mouseWheelDown = true;

    }

    //This method can be called from anywhere that the input class resides to find out if the key passed in is pressed.
    public boolean key(String id) {

        boolean result;

        switch (id) {
            case "left":
                result = this.mouseStates.get(PApplet.LEFT);
                break;
            case "right":
                result = this.mouseStates.get(PApplet.RIGHT);
                break;
            case "center":
                result = this.mouseStates.get(PApplet.CENTER);
                break;
            default:
                result = this.keyStates.get(id);
                break;
        }

        return result;

    }

    //Tells the class that a click has occurred and that an action should take place.
    public void setFullLeftClick(boolean state) {

        this.fullLeftClick = state;

    }

    public void setFullCenterClick(boolean state) {

        this.fullCenterClick = state;

    }

    //Called from outside the class to find out if a full click has been carried out on the left mouse button.
    public boolean fullLeftClick() {

        boolean result = false;
        if (this.fullLeftClick) {
            this.fullLeftClick = false;
            result = true;
        }

        return result;

    }

    public boolean fullCenterClick() {

        boolean result = false;
        if (this.fullCenterClick) {
            this.fullCenterClick = false;
            result = true;
        }

        return result;

    }

    public void setLastKeyUp(String lastKey) {

        this.lastKeyUp = lastKey; //Stores the key that has just been released.

    }

    public boolean mouseWheelUp() {
        boolean result = false;
        if (this.mouseWheelUp) {
            this.mouseWheelUp = false;
            result = true;
        }
        return result;
    }

    public boolean mouseWheelDown() {
        boolean result = false;
        if (this.mouseWheelDown) {
            this.mouseWheelDown = false;
            result = true;
        }
        return result;
    }

    public boolean getLastKeyUp(String keyToCheck) {

        boolean result = false;
        if (keyToCheck.equals(this.lastKeyUp)) {

            result = true;
            this.lastKeyUp = ""; //Sets the last key to nothing so there is no collateral damage in any other frame that should no occur.

        }

        return result;

    }


}
