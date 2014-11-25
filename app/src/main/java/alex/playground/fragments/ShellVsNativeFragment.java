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

import alex.playground.Logger;
import alex.playground.R;
import alex.playground.utils.CustomTimingLogger;
import alex.playground.utils.NativeWrapper;
import alex.playground.utils.cmdprocessor.CMDProcessor;

public class ShellVsNativeFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ShellVsNativeFragment";
    private static final String TEST_FILE_PATH = "/proc/meminfo";

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
                new NativeVsShellTest().execute();
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

    private class NativeVsShellTest extends AsyncTask<Void, Void, String> {
        private final StringBuilder sb = new StringBuilder();

        @Override protected void onPreExecute() {
            mIsRunning = true;
            updateButton();
        }

        @Override protected String doInBackground(Void... params) {
            final Time today = new Time(Time.getCurrentTimezone());
            today.setToNow();
            sb.append(String.format("Starting at: %s", today.format("%k:%M:%S"))).append("\n\n");
            mLogger.reset(TAG, "NativeVsShellTest");
            startShellTest();
            startNativeTest();
            sb.append(mLogger.dumpToString()).append('\n');
            return sb.toString();
        }

        @Override protected void onPostExecute(final String result) {
            mIsRunning = false;
            updateButton();

            setText(result);
        }
    }

    private void startNativeTest() {
        Logger.v(this, "startNativeTest: %s", NativeWrapper.readMemoryInfo(TEST_FILE_PATH));

        // log time
        mLogger.addSplit("startNativeTest");
    }

    private void startShellTest() {
        final String output = new CMDProcessor().sh
                .runWaitFor(String.format("cat %s", TEST_FILE_PATH)).stdout;
        Logger.v(this, "startShellTest: %s", output);

        // log time
        mLogger.addSplit("startShellTest");
    }

}
