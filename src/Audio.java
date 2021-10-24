import ddf.minim.AudioPlayer;
import ddf.minim.Minim;
import processing.data.JSONObject;

import java.util.HashMap;

/**
 * Audio Class.
 * This is the central audio engine. Which controls all sounds for the game.
 * Any sound which might be required within the current point of the game are loaded.
 * They are stored in a HashMap called currentSounds with the sound name as the key, and the actual AudioPlayer sound as the value.
 */
class Audio {

    private HashMap<String, AudioPlayer> currentSounds; //Contains all the sounds required for the current menu/level/scope,
    private final Minim minim;
    private final boolean globalMute;
    private final Utility util;

    public Audio(Minim minim, Utility util) {

        this.util = util;

        globalMute = true;
        this.minim = minim;
        this.currentSounds = new HashMap<>();

    }

    /*
     *  Takes in a string array of names of folders in the audio directory.
     *  All of the sounds in those folders will be loaded provided there is a manifest.json file as well with the information in it.
     */
    public void loadAudioCache(String[] folders) {

        for (String soundName : this.currentSounds.keySet())
            this.stop(soundName);

        this.currentSounds = null; //Unbinds the current sounds for the old list.
        this.currentSounds = new HashMap<>(); //Creates a new hashmap for the new sounds that will be loaded.

        for (String audioFolder : folders) { //Iterates through the required folder names.

            JSONObject soundManifest = this.util.loadJSONFile("assets/audio/" + audioFolder + "/manifest.json");
            for (Object key : soundManifest.keys()) { //Iterates through the many sounds in the manifest.json file.
                String keyID = key.toString();
                this.currentSounds.put(keyID, this.minim.loadFile("assets/audio/" + audioFolder + "/" + soundManifest.getString(keyID))); //Places the sound with its key inside the main hashmap.

            }

        }

    }

    public void play(String name) {

        if (this.currentSounds.get(name) != null && !this.currentSounds.get(name).isPlaying()) { //Ensures the sound exists and is not already playing.

            this.currentSounds.get(name).play();

            if (this.globalMute)
                this.currentSounds.get(name).mute(); //Mutes the sound if mute is on.

        }

    }

    public void stop(String name) {

        if (this.currentSounds.get(name) != null && this.currentSounds.get(name).isPlaying()) { //Checks the sound exists and is actually playing.
            //There is no stop method so stopping is done by pausing and then rewinding.
            this.currentSounds.get(name).pause();
            this.currentSounds.get(name).rewind();

        }

    }

    public void fadeAllSounds(int timeInMilliseconds) {

        for (String soundName : this.currentSounds.keySet()) {
            AudioPlayer soundPlayer = this.currentSounds.get(soundName);
            soundPlayer.shiftGain(soundPlayer.getGain(), -80, timeInMilliseconds);
        }
    }


}
