package com.sangcx.accessibility;

import android.accessibilityservice.AccessibilityServiceInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangcx.R;
import java.util.List;


public class AccessibilityFragment extends Fragment {

    TextView result;
    static String text = "";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_accessibility, container, false);
        result = view.findViewById(R.id.result);
        listAppsUsingAccessibilityService(requireContext());
        result.setText(text);

        if (getActivity() != null) {
            getActivity().setTitle("Accessibility service");
        }
        return view;
    }

    public static void listAppsUsingAccessibilityService(Context context) {
        String enabledServices = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        );
        AccessibilityManager am = (AccessibilityManager) context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        List<AccessibilityServiceInfo> installedServices = am.getInstalledAccessibilityServiceList();

        if (enabledServices == null) {return;}

        String[] enabledServicesArray = enabledServices.split(":");

        for (String enabledService: enabledServicesArray) {
            for (AccessibilityServiceInfo serviceInfo : installedServices) {
                if (serviceInfo.getId().equals(enabledService)) {
                    String packageName = serviceInfo.getResolveInfo().serviceInfo.packageName;
                    String appName = getAppName(context, packageName);
                    text += "----------------------------------" + "\n";
                    text += "Package Name: " + packageName + "\n";
                    text += "App Name: " + appName + "\n";
                    text += "Service ID: " + serviceInfo.getId() + "\n";
                    text += "----------------------------------" + "\n";
                }
            }
        }
    }


    private static String getAppName(Context context, String packageName) {
        try {
            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(packageName, 0);
            return (String) packageManager.getApplicationLabel(applicationInfo);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }

}
