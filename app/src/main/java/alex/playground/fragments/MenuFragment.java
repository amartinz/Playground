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
import android.app.ListFragment;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import alex.playground.R;

public class MenuFragment extends ListFragment {

    private static final int[] MENU_ENTRIES = { R.string.native_vs_shell, R.string.networking, R.string.sensors, R.string.camera };

    @Override public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1);

        for (int entry : MENU_ENTRIES) {
            arrayAdapter.add(getString(entry));
        }

        setListAdapter(arrayAdapter);
    }

    @Override public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        switch (position) {
            case 0: // native_vs_shell
                loadFragment(new ShellVsNativeFragment());
                break;
            case 1: // networking
                loadFragment(new NetworkingFragment());
                break;
            case 2: // sensors
                loadFragment(new SensorFragment());
                break;
            case 3: // camera
                loadFragment(new CameraFragment());
                break;
        }
    }

    private void loadFragment(final Fragment fragment) {
        if (fragment == null) return;

        getFragmentManager()
                .beginTransaction()
                .replace(R.id.root_container, fragment)
                .addToBackStack(null)
                .commit();
    }
}
