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
package alex.playground.net;

import android.content.Context;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

import alex.playground.Logger;

/**
 * A helper class for everything related to connections
 */
public class ConnectionHelper {
    private static final String TAG = "ConnectionHelper";

    /**
     * Gets the broadcast address of the currently connected wifi network
     *
     * @param context
     * @return The broadcast address as {@link InetAddress} or null if not connected or an error occurs
     * @throws IOException
     */
    @Nullable
    public static InetAddress getBroadcastAddress(final Context context) throws IOException {
        final WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (!wifi.isWifiEnabled()) {
            Logger.e(TAG, "wifi is not enabled!");
            return null;
        }
        final DhcpInfo dhcp = wifi.getDhcpInfo();

        if (dhcp == null) {
            Logger.e(TAG, "dhcp is null!");
            return null;
        }

        final int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;
        byte[] quads = new byte[4];
        for (int k = 0; k < 4; k++) {
            quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
        }
        return InetAddress.getByAddress(quads);
    }

    public static DatagramPacket discoverDevices(final int srcPort, final int destPort,
            final InetAddress address, final String data) throws IOException {
        if (address == null) {
            Logger.e(TAG, "discoverDevices: address is null!");
            return null;
        }
        final DatagramSocket socket = new DatagramSocket(srcPort);
        socket.setBroadcast(true);
        final DatagramPacket sendPacket = new DatagramPacket(data.getBytes(), data.length(),
                address, destPort);
        socket.send(sendPacket);

        final byte[] buf = new byte[1024];
        final DatagramPacket packet = new DatagramPacket(buf, buf.length);
        socket.receive(packet);
        socket.close();
        return packet;
    }

}
