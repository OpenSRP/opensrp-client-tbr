package org.smartregister.tbr.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;

import org.smartregister.tbr.R;
import org.smartregister.repository.AllSharedPreferences;

import java.net.MalformedURLException;
import java.net.URL;

import static org.smartregister.util.Log.logError;
import static org.smartregister.util.Log.logInfo;
import static util.TbrConstants.TBREACH_ORGANIZATION;

public class SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }


    public static class MyPreferenceFragment extends PreferenceFragment {
        @Override
        public void onCreate(final Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);

            Preference baseUrlPreference = findPreference("DRISHTI_BASE_URL");
            Preference tbreachOrganization = findPreference("TBREACH_ORGANIZATION");
            if (baseUrlPreference != null) {
                EditTextPreference baseUrlEditTextPreference = (EditTextPreference) baseUrlPreference;
                baseUrlEditTextPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateUrl(newValue.toString());
                        }
                        return true;
                    }
                });
            }
            if (tbreachOrganization != null) {
                EditTextPreference editViewConfigPreference = (EditTextPreference) tbreachOrganization;
                editViewConfigPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (newValue != null) {
                            updateTbreachOrganization(newValue.toString());
                        }
                        return true;
                    }
                });
            }

        }

        private void updateUrl(String baseUrl) {
            try {
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                AllSharedPreferences allSharedPreferences = new AllSharedPreferences(preferences);

                URL url = new URL(baseUrl);

                String base = url.getProtocol() + "://" + url.getHost();
                int port = url.getPort();

                logInfo("Base URL: " + base);
                logInfo("Port: " + port);

                allSharedPreferences.saveHost(base);
                allSharedPreferences.savePort(port);

                logInfo("Saved URL: " + allSharedPreferences.fetchHost(""));
                logInfo("Port: " + allSharedPreferences.fetchPort(0));
            } catch (MalformedURLException e) {
                logError("Malformed Url: " + baseUrl);
            }
        }

        private void updateTbreachOrganization(String organization) {
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            preferences.edit().putString(TBREACH_ORGANIZATION, organization).apply();
        }

    }

}
