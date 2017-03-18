package com.translate.forsenboyz.rise42.translate;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashSet;
import java.util.Set;

import static com.translate.forsenboyz.rise42.translate.DatabaseHandler.*;
import static com.translate.forsenboyz.rise42.translate.MainActivity.TAG;
import static com.translate.forsenboyz.rise42.translate.ListUtils.*;

public class WordActivity extends AppCompatActivity {

    private ListView listWord;

    private MenuItem menuOk;
    private MenuItem menuCancel;

    private DatabaseHandler databaseHandler;

    private Set<Integer> results;

    private String word;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word);

        this.word = getIntent().getStringExtra(KEY_COLUMN);

        Log.d(TAG, "onCreate: word:"+word);
        Log.d(TAG, "onCreate: translations:"+getIntent().getStringExtra(VALUE_COLUMN));

        databaseHandler = DatabaseHandler.getInstance(WordActivity.this);

        results = new HashSet<>(12);

        ((TextView) findViewById(R.id.textWordTitle)).setText(word);

        listWord = (ListView) findViewById(R.id.listWord);
        listWord.setOnItemClickListener(makeResultListClickListener());
        fillList(getIntent().getStringExtra(VALUE_COLUMN));

        Log.d(TAG, "onCreate: damn array from str: "+CommaStringToListOfMaps(getIntent().getStringExtra(VALUE_COLUMN)));
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
                    Toast.makeText(WordActivity.this, "OK", Toast.LENGTH_SHORT).show();
                    try {
                        Log.d(TAG, "OK: q: " + word + " results:"
                                + StringArrayToString(getUnchosenResults(listWord, results)));
                        Log.d(TAG, "onOptionsItemSelected: updating into");
                        databaseHandler.update(word, getUnchosenResults(listWord, results));
                        Log.d(TAG, "onOptionsItemSelected: getting back");
                        Log.d(TAG, "onOptionsItemSelected: got this:"
                                + databaseHandler.get(word));
                        finish();
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d(TAG, "onOptionsItemSelected: " + e);
                    }
                }
                break;

            case R.id.menu_search_cancel:
                results.clear();
                listWord.invalidateViews();
                Toast.makeText(WordActivity.this, "cancel", Toast.LENGTH_SHORT).show();
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

    private void fillList(String translations) {
        listWord.setAdapter(
                new SimpleAdapter(
                        WordActivity.this,
                        CommaStringToListOfMaps(translations),
                        R.layout.list_item_word,
                        new String[]{VALUE_COLUMN},
                        new int[]{R.id.itemWordValue}
                ) {
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = getLayoutInflater().inflate(R.layout.list_item_word, parent, false);
                        if (!results.contains(position)) {
                            return super.getView(
                                    position,
                                    view,
                                    parent);
                        } else{
                            view.setBackgroundResource(R.color.deleted);
                            ((TextView) view.findViewById(R.id.itemWordValue)).setTextColor(Color.WHITE);
                            view.findViewById(R.id.itemWordValue).setBackgroundResource(R.color.deleted);
                            return super.getView(position, view, parent);
                        }
                    }
                }
        );
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
                listWord.invalidateViews();
            }
        };
    }
}
