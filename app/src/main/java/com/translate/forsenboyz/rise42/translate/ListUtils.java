package com.translate.forsenboyz.rise42.translate;

import android.util.Log;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.translate.forsenboyz.rise42.translate.DatabaseHandler.*;
import static com.translate.forsenboyz.rise42.translate.MainActivity.TAG;

/**
 * Created by rise42 on 18/03/17.
 */

public class ListUtils {

    public static String[] getChosenResults(ListView listView, Set<Integer> results) {
        String[] list = new String[results.size()];

        Iterator<Integer> iterator = results.iterator();

        for (int i=0; i<list.length; i++){
            list[i] = (String)
                    ((HashMap) listView.getAdapter().getItem(iterator.next()))
                            .get("value");
        }

        return list;
    }

    public static String[] getUnchosenResults(ListView listView, Set<Integer> results) {
        ArrayList<String> list = new ArrayList<>(listView.getCount()-results.size());

        for (int i=0; i<listView.getCount(); i++){
            Log.d(TAG, "getUnchosenResults iter: "+listView.getAdapter().getItem(i));
            if(!results.contains(i)){
                list.add((String)
                        ((HashMap) listView.getAdapter().getItem(i))
                                .get(VALUE_COLUMN));
            }
        }

        if(list.isEmpty())
            return null;

        return list.toArray(new String[0]);
    }

    public static String StringArrayToString(String[] array){
        if(array == null)
            return null;
        String s = "";
        for(String a: array){
            s = s + a;
        }
        return s;
    }

    public static String translationsToStringWithComma(String[] translations){
        String value = "";
        for(String s: translations){
            value = value + s + ",";
        }
        return value;
    }

    // that's a sign of bad code, isn't it?
    public static List<Map<String,String>> CommaStringToListOfMaps(String commaString){
        List<Map<String,String>> list = new ArrayList<>();

        Map<String,String> map;
        for(String s: commaString.split(", ")){
            map = new HashMap<>(1);
            map.put(VALUE_COLUMN, s);
            list.add(map);
        }

        return list;
    }
}
