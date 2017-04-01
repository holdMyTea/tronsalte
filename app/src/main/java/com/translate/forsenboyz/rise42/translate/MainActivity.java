package com.translate.forsenboyz.rise42.translate;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.List;
import java.util.Map;

import static com.translate.forsenboyz.rise42.translate.DatabaseHandler.KEY_COLUMN;
import static com.translate.forsenboyz.rise42.translate.DatabaseHandler.VALUE_COLUMN;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "14tag88";

    DatabaseHandler databaseHandler;

    private ListView listMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        databaseHandler = DatabaseHandler.getInstance(MainActivity.this);

        listMain = (ListView) findViewById(R.id.listMain);
        listMain.setOnItemClickListener(makeOnItemClick());

        Log.d(TAG, "onCreate: Created");
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: Start?");
        fillList();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_main_add){
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        else if(id == R.id.menu_main_backup){
            startActivity(new Intent(this, BackupActivity.class));
            return true;
        }

        return false;
    }

    private AdapterView.OnItemClickListener makeOnItemClick(){
        return new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map<String,String> data = (Map<String,String>) parent.getItemAtPosition(position);
                Log.d(TAG, "onItemClick: data:"+data);

                Intent intent = new Intent(MainActivity.this, WordActivity.class);
                intent.putExtra(KEY_COLUMN, data.get(KEY_COLUMN));
                intent.putExtra(VALUE_COLUMN, data.get(VALUE_COLUMN));
                startActivity(intent);
            }
        };
    }

    private void fillList(){
        if(databaseHandler.getCount() == 0){
            return;
        }
        List<Map<String,String>> list = databaseHandler.getAll();
        if(list == null){
            return;
        }
        listMain.setAdapter(
                new SimpleAdapter(
                        MainActivity.this,
                        databaseHandler.getAll(),
                        R.layout.list_item_main,
                        new String[]{KEY_COLUMN, DatabaseHandler.VALUE_COLUMN},
                        new int[]{R.id.textItemMainWord, R.id.textItemMainValue}
                )
        );
    }

}
