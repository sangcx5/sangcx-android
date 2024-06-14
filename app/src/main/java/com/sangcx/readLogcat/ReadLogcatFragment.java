package com.sangcx.readLogcat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangcx.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReadLogcatFragment extends Fragment {

    TextView content;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_read_logcat, container, false);
        content = view.findViewById(R.id.content);

        read();

        return view;
    }


    public void read() {
        final StringBuilder slog = new StringBuilder();

        try {
            Process mLogcatProc;
            mLogcatProc = Runtime.getRuntime().exec(new String[]
                    {"logcat", "-d", "AndroidSecurity:D *:S" });

            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    mLogcatProc.getInputStream()));

            String line;
            String separator = System.getProperty("line.separator");

            while ((line = reader.readLine()) != null) {
                slog.append(line);
                slog.append(separator);
            }

        } catch (IOException e) {
            // handle error
        }

        content.setText(slog);
    }

}
