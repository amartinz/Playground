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
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Time;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.InetAddress;

import alex.playground.Logger;
import alex.playground.R;
import alex.playground.net.ConnectionHelper;
import alex.playground.utils.CustomTimingLogger;

public class NetworkingFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "NetworkingFragment";

    private final CustomTimingLogger mLogger = new CustomTimingLogger(TAG, "MAIN");

    private Button mStart;
    private TextView mOutput;

    private boolean mIsRunning = false;

    @Override public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
            final Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.fragment_shell_vs_native, container, false);

        mStart = (Button) v.findViewById(R.id.bStart);
        mStart.setOnClickListener(this);

        mOutput = (TextView) v.findViewById(R.id.tvOutput);

        return v;
    }

    @Override public void onClick(View v) {
        final int id = v.getId();
        switch (id) {
            case R.id.bStart: {
                new NetworkingTest().execute();
                break;
            }
        }
    }

    private void setText(final String txt) {
        if (mOutput == null) return;
        mOutput.setText(txt);
    }

    private void updateButton() {
        if (mStart != null) {
            mStart.setEnabled(!mIsRunning);
        }
    }

    private class NetworkingTest extends AsyncTask<Void, Void, String> {
        private final StringBuilder sb = new StringBuilder();

        @Override protected void onPreExecute() {
            mIsRunning = true;
            updateButton();
        }

        @Override protected String doInBackground(Void... params) {
            final Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            sb.append(String.format("Starting at: %s", today.format("%k:%M:%S"))).append("\n\n");
            mLogger.reset(TAG, "NetworkingTest");

            InetAddress address = null;
            try {
                address = ConnectionHelper.getBroadcastAddress(getActivity());
            } catch (IOException ioe) {
                Logger.e(TAG, "Could not get broadcast address!", ioe);
            }
            if (address == null) {
                sb.append("address is null!");
            } else {
                sb.append(String.format("broadcast address: %s", address.toString()));
            }

            sb.append(mLogger.dumpToString()).append('\n');
            return sb.toString();
        }

        @Override protected void onPostExecute(final String result) {
            mIsRunning = false;
            updateButton();

            setText(result);
        }
    }

}
