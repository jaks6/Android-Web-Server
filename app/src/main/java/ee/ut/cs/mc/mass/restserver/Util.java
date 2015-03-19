package ee.ut.cs.mc.mass.restserver;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.ParcelUuid;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Map;

/**
 * Created by Jakob on 6.02.2015.
 */
public class Util {

    public static final ParcelUuid BASE_UUID =
            ParcelUuid.fromString("00000000-0000-1000-8000-00805F9B34FB");

    public static String getIpAddress() {
        String ipAddress = null;
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()) {
                        ipAddress = inetAddress.getHostAddress().toString();
                    }
                }
            }
        } catch (SocketException ex) {}
        return ipAddress;
    }

    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    /** https://android.googlesource.com/platform/frameworks/base/+/master/core/java/android/bluetooth/BluetoothUuid.java */
    public static ParcelUuid createParcelUuidFromShortUuid(String UUID){
        byte[] uuidBytes = Util.hexStringToByteArray(UUID);
        long shortUuid;
        shortUuid = uuidBytes[0] & 0xFF;
        shortUuid += (uuidBytes[1] & 0xFF) << 8;

        long msb = BASE_UUID.getUuid().getMostSignificantBits() + (shortUuid << 32);
        long lsb = BASE_UUID.getUuid().getLeastSignificantBits();
        return new ParcelUuid(new java.util.UUID(msb, lsb));
    }










    /** -------- Helper function from the NanoHttpd Library -----------------*/
    public static String toString(Map<String, ? extends Object> map) {
        if (map.size() == 0) {
            return "";
        }
        return unsortedList(map);
    }
    public static String unsortedList(Map<String, ? extends Object> map) {
        StringBuilder sb = new StringBuilder();
        sb.append("<ul>");
        for (Map.Entry entry : map.entrySet()) {
            listItem(sb, entry);
        }
        sb.append("</ul>");
        return sb.toString();
    }
    public static void listItem(StringBuilder sb, Map.Entry entry) {
        sb.append("<li><code><b>").append(entry.getKey()).
                append("</b> = ").append(entry.getValue()).append("</code></li>");
    }
}
