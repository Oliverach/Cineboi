package ch.bbcag.cineboi.helper;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import ch.bbcag.cineboi.model.Film;

public class TMDB_Parser {

    public static ArrayList<Film> createFilmFromJsonString(String filmJsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(filmJsonString);
        JSONArray results = jsonObj.getJSONArray("results");
        ArrayList<Film> filmlist = new ArrayList<>();
        for(int i = 0; i < results.length(); i++)
        {
            Film film = new Film();
            JSONObject subObj = results.getJSONObject(i);
            setStandardValue(film, subObj);
            filmlist.add(film);
        }
        return filmlist;
    }


    public static Film getFilmDetailFromJsonString(String filmJsonString) throws JSONException {
        JSONObject jsonObj = new JSONObject(filmJsonString);
        Film film = new Film();
        setStandardValue(film, jsonObj);
        film.setOverview(jsonObj.getString("overview"));
        film.setBackdrop(jsonObj.getString("backdrop_path"));
        film.setInfo("Length:"+jsonObj.getString("runtime")+"min\nLanguage:" +jsonObj.getString("original_language"));
        return film;
    }

    public static void setStandardValue(Film film, JSONObject jsonObj) throws JSONException {
        film.setId(Integer.parseInt(jsonObj.getString("id")));
        film.setName(jsonObj.getString("original_title"));
        film.setPoster_Path(jsonObj.getString("poster_path"));
    }

    public static Map<String, String> getFilmGenresFromJsonString(String filmJsonString) throws JSONException{
        JSONObject jsonObj = new JSONObject(filmJsonString);
        JSONArray results = jsonObj.getJSONArray("genres");
        TreeMap<String, String> genres = new TreeMap<>();
        for(int i = 0; i < results.length(); i++)
        {
            JSONObject subObj = results.getJSONObject(i);
            genres.put(subObj.getString("id"), subObj.getString("name"));
        }
        Map sortedgenres = sortByValues(genres);
        return sortedgenres;
    }
    public static Map<String, String> getFilmCountriesFromJsonString(String filmJsonString) throws JSONException{
        JSONObject jsonObj = new JSONObject(filmJsonString);
        JSONArray results = jsonObj.getJSONArray("results");
        TreeMap<String, String> countries = new TreeMap<>();
        for(int i = 0; i < results.length(); i++)
        {
            JSONObject subObj = results.getJSONObject(i);
            countries.put(subObj.getString("iso_3166_1"), subObj.getString("native_name"));
        }
        Map sortedcountries = sortByValues(countries);
        return sortedcountries;
    }

    public static <K, V extends Comparable<V>> Map<K, V> sortByValues(final Map<K, V> map) {
        Comparator<K> valueComparator =
                new Comparator<K>() {
                    public int compare(K k1, K k2) {
                        int compare =
                                map.get(k1).compareTo(map.get(k2));
                        if (compare == 0)
                            return 1;
                        else
                            return compare;
                    }
                };

        Map<K, V> sortedByValues =
                new TreeMap<K, V>(valueComparator);
        sortedByValues.putAll(map);
        return sortedByValues;
    }

}
