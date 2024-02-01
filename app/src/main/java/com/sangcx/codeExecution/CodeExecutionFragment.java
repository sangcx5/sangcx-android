package com.sangcx.codeExecution;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sangcx.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CodeExecutionFragment extends Fragment {

    String TAG = "SangCX-CommandExecution";
    TextView result;
    TextView command_exec;
    EditText ed;
    Button executeAsRoot;
    Button executeAsNormalUser;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_code_execution, container, false);
        result = view.findViewById(R.id.result);
        ed = view.findViewById(R.id.command);
        command_exec = view.findViewById(R.id.command_exec);
        executeAsRoot = view.findViewById(R.id.execute_as_root);
        executeAsNormalUser = view.findViewById(R.id.execute_as_normal_user);

        executeAsRoot.setOnClickListener(view1 -> executeCommand(true));

        executeAsNormalUser.setOnClickListener(view12 -> {
            Log.d(TAG, "seeee");
            executeCommand(false);
        });

        if (getActivity() != null) {
            getActivity().setTitle("Code Execution");
        }
        return view;
    }

    @SuppressLint("SetTextI18n")
    public void executeCommand(boolean asRoot) {
        try {
            // cat /sdcard/abc.txt
            String command = ed.getText().toString().trim();
            Log.d(TAG, "Exec " + command);
            if (command.isEmpty()) {
                command_exec.setText("Please input command");
                return;
            }

            Log.d(TAG, "Exec " + command);
            command_exec.setText("Exec `" + command + "` " + (asRoot ? " as root" : "") + " got result: ");
            ProcessBuilder processBuilder;
            if (asRoot) {
                processBuilder = new ProcessBuilder("su", "-c", command);
            } else {
                processBuilder = new ProcessBuilder(command.split(" "));
            }

            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<String> fileList = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                fileList.add(line);
            }
            int exitCode = process.waitFor();
            String res = "";
            if (exitCode == 0) {
                for (String file : fileList) {
                    res += file + "\n";
                }
            } else {
                result.setText("Error while executing command");
            }

            result.setText(res);
        } catch (IOException |
                 InterruptedException e) {
            result.setText(String.valueOf(e));
            //            throw new RuntimeException(e);
        }
    }

}
