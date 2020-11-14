package util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.json.JSONArray;
import org.json.JSONException;

/**
 * from https://stackoverflow.com/a/40983074
 */
public class ArrayUtil
{
    public static ArrayList<String> convert(JSONArray jArr)
    {
        ArrayList<String> list = new ArrayList<String>();
        try {
            for (int i=0, l=jArr.length(); i<l; i++){
                 list.add(jArr.getString(i));
            }
        } catch (JSONException e) {}

        return list;
    }

    public static JSONArray convert(Collection<Object> list)
    {
        return new JSONArray(list);
    }
    public static <T> T selectRandom(List<T> list, Random r) {
    	return list.get(r.nextInt(list.size()));
    	
    }

    public static <T> T selectRandom(T[] arr, Random r) {
    	return arr[r.nextInt(arr.length)];
    }

}