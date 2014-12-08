package com.bacon.corey.audiotimeshift;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sample Rates
        addPreferencesFromResource(R.xml.preferences);
        ListPreference listPreference = (ListPreference)findPreference("pref_sampleRates");
        ArrayList<String> supportedSampleRateStrings = RecordingOptionsCalculator.getSupportedSampleRateDescriptions();
        ArrayList<Integer> supportedSampleRateValues = RecordingOptionsCalculator.getSupportedSampleRates();
        CharSequence[] csv = new CharSequence[supportedSampleRateValues.size()];
        int i = 0;
        for(Integer temp: supportedSampleRateValues){
            csv[i] = Integer.toString(temp);
            i++;
        }
        listPreference.setEntryValues(csv);
        listPreference.setEntries(supportedSampleRateStrings.toArray(new CharSequence[supportedSampleRateStrings.size()]));
        listPreference.setDefaultValue(RecordingOptionsCalculator.getHighestSampleRate());

        // Channels

    }
}