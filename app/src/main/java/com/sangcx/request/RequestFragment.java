package com.sangcx.request;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.sangcx.R;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class RequestFragment extends Fragment {

    TextView textView;
    Button btn;
    String TAG = "SangCX-Request";
    Context context;
    MessageDigest digest;

    {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public static final MediaType JSON = MediaType.get("application/json");
    OkHttpClient client = new OkHttpClient();

    public static class Data {
        String enc;
        String hash;
        public void setEnc(String enc) {
            this.enc = enc;
        }
        public void setHash(String hash) {
            this.hash = hash;
        }
        public String getEnc() {
            return enc;
        }
        public String getHash() {
            return hash;
        }
    }
    @SuppressLint({"MissingInflatedId", "SetTextI18n"})
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        context = requireContext();
        View view = inflater.inflate(R.layout.fragment_request, container, false);
        textView = view.findViewById(R.id.tv);
        btn = view.findViewById(R.id.btn);

        if (getActivity() != null) {
            getActivity().setTitle("Encrypted Request");
        }

        btn.setOnClickListener(v -> sendRequest());

        return view;
    }

    void setResponse(String responseData) {
        Gson gson = new Gson();
        Data data = gson.fromJson(responseData, Data.class);
        String enc = new String(Base64.decode(data.getEnc(), Base64.DEFAULT));
        String hash = data.getHash();
        if (hash.equals(sha256(enc))) {
            textView.setText(enc);
        } else {
            textView.setText("???");
        }
    }

    String sha256(String str) {
        byte[] encodedHash = digest.digest(
                str.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
        for (byte b : encodedHash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    void sendRequest(){
        Gson gson = new Gson();
        Data data = new Data();
        String reqBody = "secret";
        data.setEnc(Base64.encodeToString("secret".getBytes(), Base64.DEFAULT).trim());
        data.setHash(sha256(reqBody));
        String json = gson.toJson(data);
        RequestBody body = RequestBody.create(json, JSON);
        Request request = new Request.Builder()
                .url("http://192.168.1.14:3000")
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                requireActivity().runOnUiThread(() -> {
                    Log.d(TAG, e.toString());
                    textView.setText("Request failed");
                });
            }

            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                Log.d(TAG, "Request sent");
                if (response.isSuccessful()) {
                    assert response.body() != null;
                    String responseData = response.body().string();
                    Log.d(TAG, "Response: " + responseData);
                    requireActivity().runOnUiThread(() -> setResponse(responseData));
                } else {
                    requireActivity().runOnUiThread(() -> {
                        Log.d(TAG, response.toString());
                        textView.setText("Request failed");
                    });
                }
            }
        });
    }
}
