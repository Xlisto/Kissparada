package cz.xlisto.kissparada;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import static cz.xlisto.kissparada.Constans.URL_KISSPARADA;

/**
 * Přístup ke SharedPreferences nastavení
 * Created Xlisto by 24.01.2021
 **/
public class Shp {
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPreferences;
    private String TYPE = "TYPE";
    public final static String NOTICE_VOTE = "NOTICE_VOTE";
    public final static String NOTICE_KISSPARADA = "NOTICE_KISSPARADA";
    public final static String NOTICE_REPRIZA = "NOTICE_REPRIZA";


    public Shp(Context context) {
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        editor = sharedPreferences.edit();
    }


    /**
     * Uloží url Kissparády | url na Retroparádu
     */
    public void setType(String url) {
        editor.putString(TYPE, url);
        editor.commit();
    }


    /**
     * Vrací url Kissparády
     *
     * @return url na Kissparádu | url na Retroparádu
     */
    public String getType() {
        return sharedPreferences.getString(TYPE, URL_KISSPARADA);
    }


    /**
     * Uloží hodnoty pro povolení/zakázaní zobrazovaní upozornění
     * @param b true/false
     * @param type NOTICE_VOTE|NOTICE_KISSPARADA|NOTICE_REPRIZA
     */
    public void setAllowsNotice(boolean b, String type) {
        editor.putBoolean(type, b);
        editor.commit();
    }


    /**
     * vrátí hodnoty pro povolení/zakázání zobrazovaná upozornění
     * @param type
     * @return
     */
    public boolean getAllowsNotice(String type) {
        return sharedPreferences.getBoolean(type, true);
    }



}
