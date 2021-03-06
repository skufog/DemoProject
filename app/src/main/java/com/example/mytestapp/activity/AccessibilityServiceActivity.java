package com.example.mytestapp.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.mytestapp.entity.BaseItemEntity;
import com.example.mytestapp.qianggou.AccessibilityManager;
import com.example.mytestapp.service.GreenAccessibilityService;
import com.example.mytestapp.utils.PermissionUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * @author hujie
 * Email: hujie1991@126.com
 * Date : 2020-06-15 16:42
 */
public class AccessibilityServiceActivity extends BaseListActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    @Override
    public void initData(List<BaseItemEntity> datas) {
        datas.add(new BaseItemEntity("开启无障碍服务", "0"));
        datas.add(new BaseItemEntity("点击开始轮询打印页面元素", "1"));
        datas.add(new BaseItemEntity("暂停轮询", "2"));
    }

    @Override
    public void onClickItem(int position, String value) {
        switch (value) {
            case "0":
                if (isNext()) {
                    Toast.makeText(this, "无障碍服务权限已开启", Toast.LENGTH_SHORT).show();
                } else {
                    PermissionUtils.startAccessibility(this);
                }
                break;
            case "1":
                if (isNext()) {
                    AccessibilityManager.getInstance().startPolling(5000);
                }
                break;
            case "2":
                AccessibilityManager.getInstance().stopPolling();
                break;
        }
    }


    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            String format = sdf.format(new Date());
            Log.d(TAG, "format = " + format);
            switch (format) {
                case "10:00":
                case "20:00":
                case "22:00":
                    AccessibilityManager.getInstance().startRun();
                    if ("20:00".equals(format)) {
                        AccessibilityManager.getInstance().startPolling(50);
                    }
                    break;
                case "09:59":
                case "21:59":
                    AccessibilityManager.getInstance().startPolling(58000);
                    break;
//                case "19:59":
//                    AccessibilityManager.getInstance().startPolling(59975);
//                    break;
            }
        }
    };


    private boolean isNext() {
        if (PermissionUtils.isAccessibilitySettingsOn(this) && GreenAccessibilityService.getInstance() != null) {
            return true;
        }
        PermissionUtils.startAccessibility(this);
        Toast.makeText(this, "请开启无障碍服务", Toast.LENGTH_SHORT).show();
        return false;
    }
}
