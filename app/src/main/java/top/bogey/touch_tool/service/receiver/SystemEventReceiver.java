package top.bogey.touch_tool.service.receiver;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.BatteryManager;
import android.os.Build;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import top.bogey.touch_tool.service.TaskInfoSummary;
import top.bogey.touch_tool.utils.AppUtil;

public class SystemEventReceiver extends BroadcastReceiver {
    private final Context context;
    private ConnectivityManager.NetworkCallback networkCallback;

    public SystemEventReceiver(Context context) {
        this.context = context;
        registerNetworkReceiver();
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (action == null) return;
        switch (action) {
            case Intent.ACTION_TIME_TICK -> {

            }

            case Intent.ACTION_BATTERY_CHANGED -> {
                int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 100);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
                int state = intent.getIntExtra(BatteryManager.EXTRA_STATUS, BatteryManager.BATTERY_STATUS_UNKNOWN);
                TaskInfoSummary.getInstance().setBatteryInfo(level * 100 / scale, TaskInfoSummary.BatteryState.values()[state - 1]);
            }

            case Intent.ACTION_SCREEN_ON, Intent.ACTION_SCREEN_OFF, Intent.ACTION_USER_PRESENT -> TaskInfoSummary.getInstance().setPhoneState(AppUtil.getPhoneState(context));

            case Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REMOVED, Intent.ACTION_PACKAGE_REPLACED -> TaskInfoSummary.getInstance().resetApps();

            case BluetoothDevice.ACTION_ACL_CONNECTED, BluetoothDevice.ACTION_ACL_DISCONNECTED -> {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                if (device == null) return;
                TaskInfoSummary.getInstance().setBluetoothInfo(device.getAddress(), device.getName(), action.equals(BluetoothDevice.ACTION_ACL_CONNECTED));
            }
        }
    }

    public IntentFilter getFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        // 电量变动
        filter.addAction(Intent.ACTION_BATTERY_CHANGED);
        // 屏幕与锁屏状态变更
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        // 应用安装与卸载
        filter.addAction(Intent.ACTION_PACKAGE_ADDED);
        filter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        filter.addAction(Intent.ACTION_PACKAGE_REPLACED);
        // 蓝牙连接或断开
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);

        return filter;
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    public void register() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(this, getFilter(), Context.RECEIVER_EXPORTED);
        } else {
            context.registerReceiver(this, getFilter());
        }
        registerNetworkReceiver();
    }

    public void unregister() {
        context.unregisterReceiver(this);
        unregisterNetworkReceiver();
    }

    private void registerNetworkReceiver() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null && networkCallback == null) {
            networkCallback = new ConnectivityManager.NetworkCallback() {
                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    TaskInfoSummary.getInstance().setNetworkState(Collections.singletonList(TaskInfoSummary.NotworkState.NONE));
                }

                @Override
                public void onCapabilitiesChanged(@NonNull Network network, @NonNull NetworkCapabilities networkCapabilities) {
                    super.onCapabilitiesChanged(network, networkCapabilities);
                    List<TaskInfoSummary.NotworkState> state = new ArrayList<>();
                    if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                        state.add(TaskInfoSummary.NotworkState.WIFI);
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                        state.add(TaskInfoSummary.NotworkState.MOBILE);
                    } else if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
                        state.add(TaskInfoSummary.NotworkState.VPN);
                    }
                    TaskInfoSummary.getInstance().setNetworkState(state);
                }
            };

            manager.registerDefaultNetworkCallback(networkCallback);
        }
    }

    private void unregisterNetworkReceiver() {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager != null && networkCallback != null) {
            manager.unregisterNetworkCallback(networkCallback);
        }
    }
}
