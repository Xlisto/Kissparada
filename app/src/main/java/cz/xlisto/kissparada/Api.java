package cz.xlisto.kissparada;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;
import java.util.ArrayList;

import static cz.xlisto.kissparada.Constans.J;

/**
 * Přístup k internetové stránce kiss.cz
 * Created Xlisto by 08.12.2020
 **/
public class Api {
    private final String TAG = getClass().getName() + ", ";
    private LoadAsyncTask loadAsyncTask;
    private Handler handler;
    private Context context;


    /**
     * Stáhne obsah stránky
     * V handleru vrátí seznam aktuálních skladeb
     *
     * @param handler
     */
    public void load(Handler handler, String url, Context context) {
        this.handler = handler;
        this.context = context;
        loadAsyncTask = new LoadAsyncTask();
        loadAsyncTask.execute(url);
    }


    /**
     * Stáhne obsah stránky
     */
    private class LoadAsyncTask extends AsyncTask<String, Void, KissparadaContainer> {

        @Override
        protected KissparadaContainer doInBackground(String... args) {
            KissparadaContainer kissparadaContainer = null;
            try {
                kissparadaContainer = connect(args[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return kissparadaContainer;
        }

        @Override
        protected void onPostExecute(KissparadaContainer kissparadaContainer) {
            Message message = new Message();
            message.obj = kissparadaContainer;
            message.what = 0;
            handler.sendMessage(message);
        }

        /**
         * Stáhne obsah stránky a vybere 15 tagů s jednotlivými songy
         *
         * @param wwwAdress adresa stránky: https://www.kiss.cz/kissparada/
         * @return Kontejner s Arraylist s jednotlivými songy a výsledek operace
         * @throws IOException
         */
        private KissparadaContainer connect(String wwwAdress) throws IOException {
            ArrayList<SongContainer> songContainers = new ArrayList<>();

            Document document = Jsoup.connect(wwwAdress).get();
            Elements elements = document.getElementsByClass("event clear-fix");

//kontrola uzavřeného hlasování
            if(document.text().contains("Hlasování do tohoto kola")) {
                return new KissparadaContainer(songContainers, context.getResources().getString(R.string.voted_closed));
            }
//kontrola žádného načtení dat
            if(elements.size()<=0) {
                return new KissparadaContainer(songContainers, context.getResources().getString(R.string.no_data));
            }
            for (int i = 0; i < elements.size(); i++) {
                Element element = elements.get(i);
                SongContainer songContainer = new SongContainer(element);
                songContainers.add(songContainer);
            }
            return new KissparadaContainer(songContainers, context.getResources().getString(R.string.ok));
        }
    }
}
