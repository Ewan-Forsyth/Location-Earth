import ddf.minim.Minim;
import processing.core.PApplet;

import processing.event.MouseEvent;

public class Main extends PApplet {

    public static void main(String[] args) {
        PApplet.main("Main");
    }

    Audio audio;
    Input input;

    Minim minim;

    MenuMain mainMenu;
    int programState; //To keep track of which part of the game the player is at. (Main Menu, Game ect.)
    Game game;
    Utility UTIL;

    LoadingScreen loadingScreen;
    boolean newGameLoaded; //Used to deduce if the game has finished loading in another thread.
    boolean initialiseNewGame;

    public void settings() {

        if(displayWidth > 1920 && displayHeight > 1080)
            size(1920, 1080);
        else
            fullScreen();


        noSmooth();

    }

    public void setup() {

        surface.setTitle("Location: Earth");
        surface.setResizable(false);

        UTIL = new Utility(this);

        imageMode(CENTER);

        minim = new Minim(this); //New instance of minim created here to properly get the application context.
        audio = new Audio(minim, UTIL);
        input = new Input(UTIL);

        loadingScreen = new LoadingScreen(UTIL);
        newGameLoaded = false;
        initialiseNewGame = true;

        mainMenu = new MenuMain(audio, input, UTIL);
        programState = 0;

    }


    public void draw() {

        background(0);

        //Switch decides which part of the program to run and then run an internal update function from there.
        switch (programState) {
            case 0: //Main menu
                if (!mainMenu.isActive())
                    programState = 2;
                else
                    mainMenu.update();
                break;
            case 1: //Game
                if (!game.isActive()) {
                    mainMenu = new MenuMain(audio, input, UTIL);
                    initialiseNewGame = true;
                    newGameLoaded = false;
                    programState = 0;
                    game = null;
                } else {
                    game.update();
                }
                break;
            case 2: //Loading game
                if (newGameLoaded) {
                    programState = 1;
                } else {
                    loadingScreen.draw(); //Draws the loading screen
                    if (initialiseNewGame) {
                        initialiseNewGame = false;
                        thread("loadNewGame"); //Starts a new thread to load the game.
                    }
                }
                break;
        }

    }

    /*
     *  All the key and mouse listeners are handled and processed in the Input class.
     *  They are simply detected here and then sent off to have their state changed accordingly
     */
    public void mousePressed() {
        input.mouseDown(mouseButton);
    }

    public void mouseReleased() {
        input.mouseUp(mouseButton);
    }

    public void keyPressed() {

        if (key == 27) {//Prevents the Escape key from carrying out its default quit application action.
            key = 0;
            input.keyDown(27); //Sets the escape key to be down in the input class.
        } else {
            input.keyDown(keyCode);
        }

    }

    public void keyReleased() {
        input.keyUp(keyCode);
    }

    public void mouseMoved() {
    }

    public void mouseWheel(MouseEvent event) {
        input.mouseWheelMoved(event.getCount());
    }


    //Method run in a separate thread to create and build the game world.
    public void loadNewGame() {
        newGameLoaded = false;
        game = new Game(input, this.UTIL, mainMenu.getSelectedCharacter());
        newGameLoaded = true;
    }

}
