/*
 * <!--  Copyright (C) 2014 Alexander "Evisceration" Martinz
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * -->
 */

package alex.playground.fragments;

import android.app.Fragment;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;

import alex.playground.Logger;
import alex.playground.R;
import alex.playground.net.ConnectionHelper;
import alex.playground.utils.CustomTimingLogger;

public class SensorFragment extends Fragment {
    private static final String TAG = "SensorFragment";

    private final CustomTimingLogger mLogger = new CustomTimingLogger(TAG, "MAIN");

    private TextView mOutput;

    private SensorManager mSensorManager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
    }

    @Override public void onPause() {
        super.onPause();
        if (mSensorManager != null) {
            mSensorManager.unregisterListener(mSensorEventListener);
        }
    }

    @Override public void onResume() {
        super.onResume();
        if (mSensorManager != null) {
            final Sensor proximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
            mSensorManager.registerListener(mSensorEventListener, proximity, Sensor.REPORTING_MODE_CONTINUOUS);
        }
    }

    @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shell_vs_native, container, false);

        mOutput = (TextView) v.findViewById(R.id.tvOutput);

        return v;
    }

    private final SensorEventListener mSensorEventListener = new SensorEventListener() {
        @Override public void onSensorChanged(SensorEvent event) {
            setText(String.format("value: %s", event.values[0]));
        }

        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) {

        }
    };

    private void setText(final String txt) {
        if (mOutput == null) return;
        mOutput.setText(txt);
    }

}
