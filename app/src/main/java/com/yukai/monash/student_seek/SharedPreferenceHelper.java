package com.yukai.monash.student_seek;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by yukaima on 18/05/16.
 */
public class SharedPreferenceHelper {

    private Context context;
    private String preference_file_name;

    public SharedPreferenceHelper(Context context,String preference_file_name)
    {
        this.context = context;
        this.preference_file_name = preference_file_name;
    }

    public SharedPreferences getPreferences()
    {
        return this.context.getSharedPreferences(preference_file_name, this.context.MODE_PRIVATE);
    }

    /**
     *  Method used to save Preferences */
    public void savePreferences(String key, String value)
    {
        SharedPreferences sharedPreferences = getPreferences();
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    /**
     *  Method used to load Preferences */
    public String loadPreferences(String key)
    {
        try {
            SharedPreferences sharedPreferences = getPreferences();
            String strSavedMemo = sharedPreferences.getString(key, "");
            return strSavedMemo;
        } catch (NullPointerException nullPointerException)
        {
            return null;
        }
    }
    /**
     *  Method used to delete Preferences */
    public boolean deletePreferences(String key)
    {
        SharedPreferences.Editor editor=getPreferences().edit();
        editor.remove(key).commit();
        return false;
    }

}
