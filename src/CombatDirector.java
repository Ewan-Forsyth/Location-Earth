import java.util.ArrayList;
import java.util.HashMap;

/**
 * CombatDirector Class.
 * This is called every few second to decide if an enemy NPC should spawn
 * and if so, how many should spawn.
 */
public class CombatDirector {

    private Utility util;

    public CombatDirector(Utility util) {

        this.util = util;

    }

    public int numberOfEnemiesToSpawn(HashMap<String, GameCharacter> characters) {

        int result = 0;

        GameCharacter player = characters.get("player");

        if (player.getHealth() > 20) { //This only runs if the players health is above 20

            ArrayList<GameCharacter> enemies = new ArrayList<>();

            for (String i : characters.keySet()) {

                if (characters.get(i).getFaction().equals("agent"))
                    enemies.add((characters.get(i)));

            }

            if (enemies.size() < 48) { //Caps the maximum enemy NPCs to 48 so as not to overload the memory.

                int enemiesNearPlayer = 0;
                for (GameCharacter enemy : enemies) {

                    if (this.util.findDistance(new float[]{player.position().x, player.position().y}, new float[]{enemy.position().x, enemy.position().y}) < this.util.get().width)
                        enemiesNearPlayer++;

                }

                if (enemiesNearPlayer < 2) {

                    result = 1;

                }

            }

        }

        return result;
    }

}
