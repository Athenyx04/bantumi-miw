package es.upm.miw.bantumi;

import android.content.Context;
import android.content.SharedPreferences;

public class ThemePreferenceHelper {
    public static boolean getThemeMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(
                context.getString(R.string.preferencesFile), Context.MODE_PRIVATE);
        return preferences.getBoolean(context.getString(R.string.key_UseSecondaryTheme), false);
    }
}
