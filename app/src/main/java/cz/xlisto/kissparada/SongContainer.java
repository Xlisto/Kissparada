package cz.xlisto.kissparada;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;

/**
 * Rozparsuje tag songu na jednotivé atributy a vytvoří z nich objekt
 * Created Xlisto by 08.12.2020
 *
 * Ukázka vstupních dat:
 * <div class="event clear-fix">
 *     <span class="eventTime eventKP pull-left " data-id="987"> 3 </span>
 *     <span class="eventTime eventKP eventHistory pull-left move-bellow popup" title="Minulé kolo"> 2 </span>
 *     <span class="eventEvent eventKP pull-left"> <span>Inna x Sean Paul</span><br> Up </span>
 *     <span class="eventYoutube pull-right">
 *         <a href="https://www.youtube.com/watch?v=8Fl6d_fSRNs" class="external" style="background-image:url('http://img.youtube.com/vi/8Fl6d_fSRNs/0.jpg');"><i class="fa fa-youtube-play"></i></a>
 *     </span>
 * </div>
 *
 **/
public class SongContainer {
    private String J1 = getClass().getName() + ", ";
    private Element element;
    private String dataId, interpret, song, imageLink, youtubeLink, youtubeId, position, lastPosition;


    public SongContainer() {
        this.dataId = "";
        this.interpret = "";
        this.song = "";
        this.imageLink = "";
        this.youtubeLink = "";
        this.youtubeId = "";
        this.position = "";
        this.lastPosition = "";
    }


    public SongContainer(Element element) {
        this.element = element;
        Elements dataIds = element.getElementsByAttribute("data-id");
        Elements lastPositions = element.getElementsByAttributeValueContaining("title", "Minulé kolo");
        Elements interprets = element.getElementsByTag("span");
        Elements links = element.getElementsByTag("a");
        if (dataIds != null) {
            //identifikační číslo skladby
            //Log.w(J, J1 + dataIds.first().attr("data-id"));
            dataId = dataIds.first().attr("data-id");
            position = dataIds.first().text();
            //Log.w(J, J1 + position);
        }

        if (lastPositions != null) {
            //pozice z minulého kola
            lastPosition = "";
            String pos = lastPositions.first().text();
            if (!pos.isEmpty())
                lastPosition = pos;
            //Log.w(J, J1 + lastPosition + "\n\n");
        }

        if (interprets != null) {
            //získání názevu songu a interpreta
            interpret = interprets.get(3).text();
            song = interprets.get(2).text().substring(interpret.length() + 1);
            //Log.w(J, J1 + interprets.get(2).text()+"\n\n");
            //Log.w(J, J1 + song+"\n\n");
        }
//rozparsování linku na youtube a náhledu na youtube
        if (links != null) {
            youtubeLink = links.first().attr("href");
            youtubeId = youtubeLink.replace("https://www.youtube.com/watch?v=","");
            imageLink = links.first().attr("style")
                    .replace("background-image:url('", "")
                    .replace("');", "")
                    .replace("http://", "https://");

        }
    }


    public Element getElement() {
        return element;
    }


    public String getDataId() {
        return dataId;
    }


    public String getInterpret() {
        return interpret;
    }


    public String getSong() {
        return song;
    }


    public String getImageLink() {
        return imageLink;
    }


    public String getYoutubeLink() {
        return youtubeLink;
    }


    public String getYoutubeId() {
        return youtubeId;
    }


    public String getPosition() {
        return position;
    }


    public String getLastPosition() {
        return lastPosition;
    }


    /**
     * Vyhledá patřičný song podle dataId uvedené na webu a vrátí objekt songu
     *
     * @param dataId
     * @param songContainers
     * @return
     */
    static public SongContainer searchByDataId(String dataId, ArrayList<SongContainer> songContainers) {

        for (SongContainer c : songContainers) {
            if (c.getDataId().equals(dataId))
                return c;
        }
        return new SongContainer();
    }


    /**
     * Vyhledá patřičný song podle dataId uvedené na webu a vrátí aktuální pozici v ArrayListu
     *
     * @param dataId
     * @param songContainers
     * @return
     */
    static public int positionByDataId(String dataId, ArrayList<SongContainer> songContainers) {
        for (int i = 0; i < songContainers.size(); i++) {
            if (songContainers.get(i).getDataId().equals(dataId))
                return i;
        }
        return -1;
    }


    /**
     * Vymaže jeden songcontainer podle dataId a vrátí arraylist bez tohoto smazanýho
     * @param dataId1
     * @param songContainers
     * @return
     */
    static public ArrayList<SongContainer> deleteByDataId(String dataId1, String dataId2, ArrayList<SongContainer> songContainers) {
        for (int i = songContainers.size()-1; i >= 0; i--) {
            if (songContainers.get(i).getDataId().equals(dataId1) || songContainers.get(i).getDataId().equals(dataId2)) {
                songContainers.remove(i);
                i--;
            }
        }
        return songContainers;
    }
}
