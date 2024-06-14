package com.sangcx.biometrics;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.sangcx.R;

import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.Base64;
import java.util.concurrent.Executor;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

public class BiometricsFragment extends Fragment {

    TextView status;
    Button btnDecrypt;
    Button btnEncrypt;
    String TAG = "SangCX-Biometrics";
    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;
    private static final String ANDROID_KEYSTORE = "AndroidKeyStore";
    private static final String ENCRYPTION_BLOCK_MODE = KeyProperties.BLOCK_MODE_GCM;
    private static final String ENCRYPTION_PADDING = KeyProperties.ENCRYPTION_PADDING_NONE;
    private static final int KEY_SIZE = 128;
    private static final String ENCRYPTION_ALGORITHM = KeyProperties.KEY_ALGORITHM_AES;
    private static final String KEY_NAME = "BiometricsFragment";
    private static String secret = "super_secret"; // data must be encrypted
    SharedPreferences sharedPreferences;


    private SecretKey getOrCreateSecretKey(String keyName) throws Exception {
        KeyStore keyStore = KeyStore.getInstance(ANDROID_KEYSTORE);
        keyStore.load(null);
        KeyStore.Entry entry = keyStore.getEntry(keyName, null);
        if (entry != null && entry instanceof KeyStore.SecretKeyEntry) {
            return ((KeyStore.SecretKeyEntry) entry).getSecretKey();
        }
        KeyGenParameterSpec.Builder paramsBuilder = new KeyGenParameterSpec.Builder(
                keyName,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT
        );
        paramsBuilder.setBlockModes(ENCRYPTION_BLOCK_MODE);
        paramsBuilder.setEncryptionPaddings(ENCRYPTION_PADDING);
        paramsBuilder.setKeySize(KEY_SIZE);
        KeyGenParameterSpec keyGenParams = paramsBuilder.build();
        KeyGenerator keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEYSTORE);
        keyGenerator.init(keyGenParams);
        return keyGenerator.generateKey();
    }

    private BiometricPrompt createEncryptBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this.getContext());
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                status.setText("Authentication error: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                try {
                    if (result.getCryptoObject() != null) {

                        Cipher cipher = result.getCryptoObject().getCipher();

                        byte[] encrypted = cipher.doFinal(secret.getBytes());

                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("secret", Base64.getEncoder().encodeToString(encrypted));
                        editor.putString("iv", Base64.getEncoder().encodeToString(cipher.getIV()));
                        editor.apply();

                        status.setText("Encryption succeeded!");
                    } else {
                        status.setText("Authentication failed");
                    }
                } catch (Exception e) {
                    status.setText("Authentication failed");
                    throw new RuntimeException(e);
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                status.setText("Authentication failed");
            }
        });
        return biometricPrompt;
    }

    private BiometricPrompt createDecryptBiometricPrompt() {
        executor = ContextCompat.getMainExecutor(this.getContext());
        biometricPrompt = new BiometricPrompt(this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                status.setText("Authentication error: " + errString);
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);

                String encrypted = sharedPreferences.getString("secret", null);

                try {
                    Cipher cipher = result.getCryptoObject().getCipher();
                    byte[] secretDecrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
                    String plaintext = new String(secretDecrypted, StandardCharsets.UTF_8);
                    status.setText(plaintext);
                } catch (Exception e) {
                    Log.d(TAG, e.toString());
                    status.setText("Authentication failed");
                }
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                status.setText("Authentication failed");
            }
        });
        return biometricPrompt;
    }

    public void authenticateToEncrypt() throws Exception {
        if (BiometricManager.from(getContext()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            String transformation = ENCRYPTION_ALGORITHM + "/" + ENCRYPTION_BLOCK_MODE + "/" + ENCRYPTION_PADDING;
            Cipher cipher = Cipher.getInstance(transformation);
            SecretKey secretKey = getOrCreateSecretKey(KEY_NAME);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
            .setTitle("Biometric verify")
            .setSubtitle("Biometric verify for secret data encryption")
            .setNegativeButtonText("Cancel")
            .build();
            createEncryptBiometricPrompt().authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        }
    }

    public void authenticateToDecrypt() throws Exception {
        if (BiometricManager.from(getContext()).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG) == BiometricManager.BIOMETRIC_SUCCESS) {
            String transformation = ENCRYPTION_ALGORITHM + "/" + ENCRYPTION_BLOCK_MODE + "/" + ENCRYPTION_PADDING;

            SecretKey secretKey = getOrCreateSecretKey(KEY_NAME);
            Cipher cipher = Cipher.getInstance(transformation);
            String iv = sharedPreferences.getString("iv", null);
            cipher.init(Cipher.DECRYPT_MODE, secretKey, new GCMParameterSpec(KEY_SIZE, Base64.getDecoder().decode(iv)));
            promptInfo = new BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric verify")
                    .setSubtitle("Biometric verify for secret data decryption")
                    .setNegativeButtonText("Cancel")
                    .build();
            createDecryptBiometricPrompt().authenticate(promptInfo, new BiometricPrompt.CryptoObject(cipher));
        }
    }


    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_biometrics, container, false);

        status = view.findViewById(R.id.status);
        btnDecrypt = view.findViewById(R.id.decrypt);
        btnEncrypt = view.findViewById(R.id.encrypt);
        sharedPreferences = getContext().getSharedPreferences("biometrics", Context.MODE_PRIVATE);
        btnDecrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Decrypting");
                try {
                    authenticateToDecrypt();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        });

        btnEncrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Encrypting");
                try {
                    authenticateToEncrypt();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

            }
        });
        return view;
    }

}
