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
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import alex.playground.R;
import alex.playground.utils.CustomTimingLogger;

public class CameraFragment extends Fragment {
    private static final String TAG = "SensorFragment";

    private final CustomTimingLogger mLogger = new CustomTimingLogger(TAG, "MAIN");

    private TextView mOutput;

    private CameraManager mCameraManager;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCameraManager = (CameraManager) getActivity().getSystemService(Context.CAMERA_SERVICE);
    }

    @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shell_vs_native, container, false);

        mOutput = (TextView) v.findViewById(R.id.tvOutput);

        return v;
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        appendText("Starting...\n");

        String[] camIds;
        try {
            camIds = mCameraManager.getCameraIdList();
        }catch (Exception e) {
            appendText("could not get cam ids");
            return;
        }
        appendText("camIds:");
        for (final String id : camIds) {
            appendText(id);
        }
        appendText("");

        appendText("characteristics:");
        for (final String id : camIds) {
            CameraCharacteristics characteristics;
            try {
                characteristics = mCameraManager.getCameraCharacteristics(id);
            } catch (Exception ignored) {
                appendText("error getting characteristics for " + id);
                continue;
            }
            List<CameraCharacteristics.Key<?>> keys = characteristics.getKeys();
            for (final CameraCharacteristics.Key key : keys) {
                appendText(key.getName());
                appendText("  -> " + characteristics.get(key));
            }

            appendText("");
        }
    }

    private void appendText(final String txt) {
        if (mOutput == null) return;
        mOutput.setText(mOutput.getText() + "\n" + txt);
    }

}
