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

package alex.playground;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;

import alex.playground.fragments.MenuFragment;
import alex.playground.utils.CustomTimingLogger;
import alex.playground.utils.NativeWrapper;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Enable logging
        Logger.setEnabled(true);

        // Test jni
        Logger.v(this, "jniTest: %s", NativeWrapper.stringFromJNI());

        // Setup timed logger for initial tests
        final CustomTimingLogger timingLogger = new CustomTimingLogger("MainActivity", "Main");

        // Display metrics test
        final Display display = getWindowManager().getDefaultDisplay();
        timingLogger.addSplit("getting display");

        final DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getMetrics(displayMetrics);
        timingLogger.addSplit("getting metrics");

        final int displayDpi = displayMetrics.densityDpi;
        Logger.v(this, "displayDpi -> %s\n%s", displayDpi, timingLogger.dumpToString());

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.root_container, new MenuFragment())
                .commit();
    }

    @Override public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
            return;
        }
        super.onBackPressed();
    }
}
