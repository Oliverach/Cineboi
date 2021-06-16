package ch.bbcag.cineboi;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import org.json.JSONException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import ch.bbcag.cineboi.databinding.ActivityMainBinding;
import ch.bbcag.cineboi.helper.ImageListAdapter;
import ch.bbcag.cineboi.helper.TMDB_Parser;
import ch.bbcag.cineboi.model.Film;


public class DiscoverFragment extends Fragment{
    private static final String API_URL = "https://api.themoviedb.org/3/discover/movie?api_key=fa11728f6e81c5f05fb42f521fb71283&";
    private static final String API_URL_GENRE = "https://api.themoviedb.org/3/genre/movie/list?api_key=fa11728f6e81c5f05fb42f521fb71283";
    private static final String API_URL_COUNTRIES = "https://api.themoviedb.org/3/watch/providers/regions?api_key=fa11728f6e81c5f05fb42f521fb71283";
    private static final String API_ADDITION_GENRE = "&with_genres=";
    private static final String API_ADDITION_COUNTRY = "&region=";
    private static final String API_URL_SEARCH = "https://api.themoviedb.org/3/search/movie?api_key=fa11728f6e81c5f05fb42f521fb71283";
    private static final String API_ADDITION_SEARCH = "&query=";
    private String api_query = "sort_by=popularity.desc";
    private BottomSheetDialog bottomSheetDialog;
    private LinearLayout bottomsheetcontainer;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getFilmPosters(API_URL + api_query);
        bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(R.layout.bottom_sheet_persistent);
        bottomsheetcontainer = bottomSheetDialog.findViewById(R.id.bottom_sheet);
        getActivity().setTitle("Discover");

        View v = inflater.inflate(R.layout.fragment_discover, container, false);
        Button btnCountry = v.findViewById(R.id.country_filter);
        Button btnGenre = v.findViewById(R.id.genre_filter);
        Button btnYear = v.findViewById(R.id.release_filter);
        btnCountry.setOnClickListener(this::filterCountries);
        btnGenre.setOnClickListener(this::filterGenres);
        btnYear.setOnClickListener(this::filterRelease);
        SearchView simpleSearchView = getActivity().findViewById(R.id.searchView);
        simpleSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                api_query = API_ADDITION_SEARCH + simpleSearchView.getQuery();
                getFilmPosters(API_URL_SEARCH + api_query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return v;
    }
    private void getFilmPosters(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        ArrayList<Film> films = TMDB_Parser.createFilmFromJsonString(response);
                        GridView gridView = getActivity().findViewById(R.id.gridview);
                        ImageListAdapter filmAdapter = new ImageListAdapter(getActivity(), films);
                        gridView.setAdapter(filmAdapter);
                        AdapterView.OnItemClickListener mListClickedHandler = (parent, v, position, id) -> {
                            Intent intent = new Intent(getActivity().getApplicationContext(), DetailActivity.class);
                            Film selected = (Film) parent.getItemAtPosition(position);
                            intent.putExtra("FilmId", selected.getId());
                            intent.putExtra("Filmname", selected.getName());
                            startActivity(intent);
                        };
                        gridView.setOnItemClickListener(mListClickedHandler);
                    } catch (JSONException e) {
                        generateAlertDialog();
                        e.printStackTrace();
                    }
                }, error -> generateAlertDialog());
        queue.add(stringRequest);
    }
    private void generateAlertDialog() {
        AlertDialog.Builder dialogBuilder;
        dialogBuilder = new AlertDialog.Builder(getActivity());
        dialogBuilder.setPositiveButton("Ok", (dialog, id) -> {
            getActivity().finish();
        });
        dialogBuilder.setMessage("Die Filme konnten nicht geladen werden. Versuche es später nochmals.").setTitle("Fehler");
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


    public void filterGenres(View view) {
        bottomsheetcontainer.removeAllViews();
        getGenres(API_URL_GENRE);
        bottomSheetDialog.show();
    }

    public void filterCountries(View view) {
        bottomsheetcontainer.removeAllViews();
        getCountries(API_URL_COUNTRIES);
        bottomSheetDialog.show();

    }

    public void filterRelease(View view) {
        setApi_query("year=2021");
        getFilmPosters(API_URL + api_query);
    }


    public void filterReset(View view) {
        setApi_query("sort_by=popularity.desc");
        getFilmPosters(API_URL + api_query);

        Button btn = (Button) getActivity().findViewById(R.id.genre_filter);
        btn.setText(R.string.button_genre);

        Button btn2 = (Button) getActivity().findViewById(R.id.country_filter);
        btn2.setText(R.string.button_countries);

        Button resetbtn = (Button) getActivity().findViewById(R.id.reset_button);
        resetbtn.setVisibility(View.INVISIBLE);
    }

    public String getApi_query() {
        return api_query;
    }

    public void setApi_query(String api_query) {
        this.api_query = api_query;
    }


    private void getGenres(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        HashMap<String, String> genres = TMDB_Parser.getFilmGenresFromJsonString(response);
                        generateView(genres, API_ADDITION_GENRE, R.id.genre_filter);

                    } catch (JSONException e) {
                        generateAlertDialog();
                        e.printStackTrace();
                    }
                }, error -> generateAlertDialog());
        queue.add(stringRequest);
    }

    private void getCountries(String url)
    {
        RequestQueue queue = Volley.newRequestQueue(getActivity().getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        HashMap<String, String> countries = TMDB_Parser.getFilmCountriesFromJsonString(response);
                        generateView(countries, API_ADDITION_COUNTRY, R.id.country_filter);

                    } catch (JSONException e) {
                        generateAlertDialog();
                        e.printStackTrace();
                    }
                }, error -> generateAlertDialog());
        queue.add(stringRequest);
    }

    private void generateView(HashMap<String, String> map, String searchItem, int idButton) {
        Iterator it = map.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            LinearLayout linearLayout  = new LinearLayout(getActivity());
            linearLayout.setPaddingRelative(8,8,8,8);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(getActivity());
            textView.setText((String) pair.getValue());
            textView.setPaddingRelative(8,8,8,8);
            textView.setTextSize(25);
            linearLayout.addView(textView);

            linearLayout.setOnClickListener(v -> {
                setApi_query(getApi_query() + searchItem + pair.getKey());
                getFilmPosters(API_URL + api_query);
                bottomSheetDialog.hide();
                Button btn = (Button) getActivity().findViewById(idButton);
                btn.setText(pair.getValue().toString());
                Button resetbtn = (Button) getActivity().findViewById(R.id.reset_button);
                resetbtn.setVisibility(View.VISIBLE);
            });
            bottomsheetcontainer.addView(linearLayout);
        }
    }
}