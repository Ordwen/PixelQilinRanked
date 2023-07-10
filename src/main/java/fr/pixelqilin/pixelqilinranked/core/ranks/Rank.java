package fr.pixelqilin.pixelqilinranked.core.ranks;

public class Rank {

    private final String name;
    private final int elo;

    /**
     * Constructor.
     *
     * @param name displayed name of the rank
     * @param elo  minimum elo of the rank
     */
    public Rank(String name, int elo) {
        this.name = name;
        this.elo = elo;
    }

    /**
     * Get the name of the rank.
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the minimum elo of the rank.
     * @return elo
     */
    public int getElo() {
        return elo;
    }
}
