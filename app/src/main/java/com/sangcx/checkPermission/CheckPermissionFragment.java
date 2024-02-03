package com.sangcx.checkPermission;

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


public class CheckPermissionFragment extends Fragment {

    TextView result;
    static String text = "";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_check_permission, container, false);
        result = view.findViewById(R.id.result);
        result.setText(SmsPermissionChecker.checkSmsPermissions(requireContext()));


        if (getActivity() != null) {
            getActivity().setTitle("Check Permission");
        }
        return view;
    }

}
