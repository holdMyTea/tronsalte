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
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import static com.translate.forsenboyz.rise42.translate.DatabaseHandler.KEY_COLUMN;
import static com.translate.forsenboyz.rise42.translate.DatabaseHandler.VALUE_COLUMN;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "14tag88";

    private static final int BACK_UP_CODE = 1;

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == BACK_UP_CODE){
            if(resultCode == RESULT_OK)
                Toast.makeText(MainActivity.this,"Backup successful",Toast.LENGTH_SHORT).show();
            else if(resultCode == RESULT_CANCELED)
                Toast.makeText(MainActivity.this, "Error while backing up", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menu_main_add){
            startActivity(new Intent(this, SearchActivity.class));
            return true;
        }

        else if(id == R.id.menu_main_backup){
            startActivityForResult(new Intent(this, BackupActivity.class), BACK_UP_CODE);
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
