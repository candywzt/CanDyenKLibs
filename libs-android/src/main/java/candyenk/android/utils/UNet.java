package candyenk.android.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;

/**
 * Android网络工具
 * 读取当前网络状态
 */
public class UNet {
    public static final int TRANSPORT_CELLULAR = NetworkCapabilities.TRANSPORT_CELLULAR;//4G网络
    public static final int TRANSPORT_WIFI = NetworkCapabilities.TRANSPORT_WIFI;//Wifi网络
    public static final int TRANSPORT_BLUETOOTH = NetworkCapabilities.TRANSPORT_BLUETOOTH;//蓝牙网络
    public static final int TRANSPORT_ETHERNET = NetworkCapabilities.TRANSPORT_ETHERNET;//有线网络
    public static final int TRANSPORT_VPN = NetworkCapabilities.TRANSPORT_VPN;//VPN网络
    public static final int TRANSPORT_WIFI_AWARE = NetworkCapabilities.TRANSPORT_WIFI_AWARE; //Wifi感知网络
    public static final int TRANSPORT_LOWPAN = NetworkCapabilities.TRANSPORT_LOWPAN;//低功耗无线物联网络
    public static final int TRANSPORT_USB = NetworkCapabilities.TRANSPORT_USB;//USB网络


    /**
     * 当前是否连接Wifi网络
     */
    public static boolean isWifi(Context context) {
        return equals(context, TRANSPORT_WIFI);
    }

    /**
     * 当前是否是4G网络
     */
    public static boolean is4G(Context context) {
        return equals(context, TRANSPORT_CELLULAR);
    }

    /**
     * 当前是否是VPN网络
     */
    public static boolean isVPN(Context context) {
        return equals(context, TRANSPORT_VPN);
    }

    /**
     * 当前是否是有线网络
     */
    public static boolean isEthernet(Context context) {
        return equals(context, TRANSPORT_ETHERNET);
    }

    /**
     * 比较当前网络状态
     */
    public static boolean equals(Context context, int transportType) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        Network network = connectivityManager.getActiveNetwork();
        if (network != null) return false;
        NetworkCapabilities networkCapabilities = connectivityManager.getNetworkCapabilities(network);
        if (networkCapabilities != null) return false;
        return networkCapabilities.hasTransport(transportType);
    }


}
