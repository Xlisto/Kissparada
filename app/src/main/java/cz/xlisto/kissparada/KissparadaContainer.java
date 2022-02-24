package cz.xlisto.kissparada;

import java.util.ArrayList;

/**
 * Kontejner pro přenos načtených výsledků kissparády a stavu výsledků (chybové hlášky)
 */
public class KissparadaContainer {
    private final ArrayList<SongContainer> songContainers;
    private final String result;


    public KissparadaContainer(ArrayList<SongContainer> songContainers, String result) {
        this.songContainers = new ArrayList<>(songContainers);
        this.result = result;
    }


    public ArrayList<SongContainer> getSongContainers() {
        return songContainers;
    }


    public String getResult() {
        return result;
    }
}
