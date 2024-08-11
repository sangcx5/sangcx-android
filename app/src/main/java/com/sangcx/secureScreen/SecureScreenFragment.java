package com.sangcx.secureScreen;

import android.content.Context;
import android.hardware.display.DisplayManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangcx.R;

import java.util.Arrays;

public class SecureScreenFragment extends Fragment {

    Context context;

    String TAG = "SangCX-SecureScreen";

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = requireContext();
        View view = inflater.inflate(R.layout.fragment_secure_screen, container, false);
        requireActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);

        if (isPresentation()) {
            Log.d(TAG, "Presenting");
        } else {
            Log.d(TAG, "Not Presenting");
        }

        // try to create virtual display if can not detect

        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Log.d(TAG, Arrays.toString(displayManager.getDisplays()));

        if (getActivity() != null) {
            getActivity().setTitle("Secure screen");
        }
        return view;
    }

    public boolean isPresentation() {
        DisplayManager displayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        return displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION).length > 0;
    }
}
