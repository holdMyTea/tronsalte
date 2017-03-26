package com.translate.forsenboyz.rise42.translate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.translate.forsenboyz.rise42.translate.ListUtils.*;

public class SearchActivity extends AppCompatActivity {

    public static final String TAG = "14tag88";

    private final String keyAPI = "dict.1.1.20170312T203056Z.dc8ccfab6c0d2ae9." +
            "2697a4c9bf2ba829fdf43137dcaf1c970b1a7afd";

    RequestQueue requestQueue;
    DatabaseHandler databaseHandler;

    private EditText editSearch;
    private Button buttonSearch;
    private ListView listResults;

    private MenuItem menuOk;
    private MenuItem menuCancel;

    private String query;
    private Set<Integer> results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        requestQueue = RequestSingleton.getInstance(SearchActivity.this).getQueue();
        databaseHandler = DatabaseHandler.getInstance(SearchActivity.this);

        results = new HashSet<>(12);

        editSearch = (EditText) findViewById(R.id.editSearch);
        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        listResults = (ListView) findViewById(R.id.listResults);

        Log.d(TAG, "onCreate: Created");

        buttonSearch.setOnClickListener(
                makeSearchButtonClickListener()
        );

        listResults.setOnItemClickListener(
                makeResultListClickListener()
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search, menu);
        menuOk = menu.findItem(R.id.menu_search_ok);
        menuCancel = menu.findItem(R.id.menu_search_cancel);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_search_ok:
                if (!results.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, "OK: q: "+query+" results:"
                                +StringArrayToString(getChosenResults(listResults,results)));
                        Log.d(TAG, "onOptionsItemSelected: putting this into");
                        databaseHandler.insert(query, getChosenResults(listResults,results));
                        Log.d(TAG, "onOptionsItemSelected: getting back");
                        Log.d(TAG, "onOptionsItemSelected: got this:"
                                +databaseHandler.get(query));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onOptionsItemSelected: "+e);
                    }
                }
                break;

            case R.id.menu_search_cancel:
                results.clear();
                listResults.invalidateViews();
                Toast.makeText(SearchActivity.this, "cancel", Toast.LENGTH_SHORT).show();
                break;
        }

        return false;
    }

    private void showMenuButtons() {
        menuOk.setVisible(true);
        menuCancel.setVisible(true);
    }

    private void hideMenuButtons() {
        menuOk.setVisible(false);
        menuCancel.setVisible(false);
    }

    private View.OnClickListener makeSearchButtonClickListener() {
        return new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: ");
                String input = editSearch.getText().toString();

                if (input.isEmpty()) {
                    Toast.makeText(SearchActivity.this, "C'mon", Toast.LENGTH_SHORT).show();
                    return;
                }

                results.clear();

                query = input.replaceAll(" ", "_");

                StringRequest request = new StringRequest(
                        Request.Method.GET,
                        "https://dictionary.yandex.net/api/v1/dicservice.json/lookup"
                                + "?key=" + keyAPI
                                + "&lang=" + "en-ru"
                                + "&text=" + query,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {

                                Log.d(TAG, "onResponse: response");
                                Log.d(TAG, "answer: " + response);

                                JsonParser parser = new JsonParser();

                                try {

                                    Map<String, String> map;
                                    List<Map<String, String>> list = new ArrayList<>();

                                    JsonArray container = parser.parse(response).getAsJsonObject()
                                            .get("def").getAsJsonArray().get(0).getAsJsonObject()
                                            .get("tr").getAsJsonArray();

                                    for (JsonElement meaning : container) {
                                        JsonObject current = meaning.getAsJsonObject();

                                        map = new HashMap<>();
                                        map.put("type", "translation");
                                        map.put("value", current.get("text").getAsString());
                                        list.add(map);

                                        if (current.has("syn")) {
                                            JsonArray synonyms = current.get("syn").getAsJsonArray();
                                            Log.d(TAG, "syns: " + synonyms.toString());
                                            for (JsonElement element : synonyms) {
                                                Log.d(TAG, "syn:" + element.getAsJsonObject().get("text").toString());
                                                map = new HashMap<>();
                                                map.put("type", "synonym");
                                                map.put(
                                                        "value",
                                                        element.getAsJsonObject().get("text").getAsString()
                                                );
                                                list.add(map);
                                            }
                                        }
                                    }

                                    listResults.setAdapter(
                                            new SimpleAdapter(
                                                    SearchActivity.this,
                                                    list,
                                                    R.layout.list_item_search,
                                                    new String[]{"type", "value"},
                                                    new int[]{R.id.textItemSearchType, R.id.textItemSearchValue}
                                            ){
                                                @Override
                                                public View getView(int position, View convertView, ViewGroup parent) {
                                                    View view;
                                                    if(results.contains(position)){
                                                        view = getLayoutInflater().inflate(R.layout.list_item_selected, parent, false);
                                                    } else {
                                                        view = getLayoutInflater().inflate(R.layout.list_item_search, parent, false);
                                                    }
                                                    return super.getView(position, view, parent);
                                                }
                                            }
                                    );

                                } catch (Exception e) {
                                    Log.d(TAG, "dropped: " + e);
                                    e.printStackTrace();
                                }
                            }
                        },

                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d(TAG, "onErrorResponse: pity");
                            }
                        }
                );

                requestQueue.add(request);
            }
        };
    }

    private AdapterView.OnItemClickListener makeResultListClickListener() {
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: id: "+id+" pos: "+position);
                if (results.contains(position)) {
                    results.remove(position);

                    Log.d(TAG, "onItemClick: results after:"+results);
                    if (results.isEmpty()) {
                        hideMenuButtons();
                    }

                } else {
                    results.add(position);

                    Log.d(TAG, "onItemClick: results after:"+results);
                    if (results.size() == 1) {
                        showMenuButtons();
                    }
                }
                listResults.invalidateViews();
            }
        };
    }

}
