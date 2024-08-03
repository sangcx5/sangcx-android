package com.sangcx.keyboardCheck;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangcx.R;

public class KeyboardCheckFragment extends Fragment {

    TextView textView;
    String TAG = "SangCX-KeyboardCheck";
    Context context;
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = requireContext();
        View view = inflater.inflate(R.layout.fragment_keyboard_check, container, false);
        textView = view.findViewById(R.id.tv);

        if (isUsingCustomKeyboard()) {
            textView.setText("You are using CUSTOM keyboard");
        } else {
            textView.setText("You are using SYSTEM keyboard");
        }

        if (getActivity() != null) {
            getActivity().setTitle("Custom keyboard check");
        }
        return view;
    }

    public boolean isUsingCustomKeyboard() {
        String id = Settings.Secure.getString(
                context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD
        );
        try {
            String packageName = id.split("/")[0];
            ApplicationInfo applicationInfo;
            applicationInfo = context.getPackageManager().getApplicationInfo(packageName, 0);
            return (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != ApplicationInfo.FLAG_SYSTEM;
        } catch (Exception e) {
            Log.d(TAG, "Can not get package info");
        }
        return false;
    }
}
