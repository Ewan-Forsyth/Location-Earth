import java.util.HashMap;

/**
 * GameStatistics Class.
 * Gathers stats based on the players performance to show at the end of the game.
 */
public class GameStatistics {

    private HashMap<String, Integer> statistics;

    public GameStatistics() {

        this.statistics = new HashMap<>();
        this.statistics.put("Agents Killed", 0);
        this.statistics.put("Aliens Rescued", 0);
        this.statistics.put("Damage Taken", 0);
        this.statistics.put("Damage Dealt", 0);

    }

    public void increaseStatistic(String statName, int value) {

        int newStat = this.statistics.get(statName) + value;
        this.statistics.put(statName, newStat);

    }

    public HashMap<String, Integer> getStatistics() {

        return this.statistics;

    }

}
