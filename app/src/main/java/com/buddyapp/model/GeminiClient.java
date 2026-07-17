package com.buddyapp.model;

import android.os.Handler;
import android.os.Looper;

import com.buddyapp.BuildConfig;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GeminiClient {
    private static final String MODEL = "gemini-2.5-flash";
    private static final String ENDPOINT =
            "https://generativelanguage.googleapis.com/v1beta/models/"
                    + MODEL + ":generateContent";

    public interface Callback {
        void onSuccess(String text);

        void onError(String message);
    }

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainThread = new Handler(Looper.getMainLooper());

    public void ask(String prompt, Callback callback){
        executor.execute(() -> {
            try {
                String reply = request(prompt);
                mainThread.post(() -> callback.onSuccess(reply));
            } catch (Exception e) {
                mainThread.post(() -> callback.onError(e.getMessage()));
            }
        });
    }

    private String request(String prompt) throws Exception {
        // 1. Build the body: { "contents": [ { "parts": [ { "text": ... } ] } ] }
        JSONObject part = new JSONObject().put("text", prompt);
        JSONObject content = new JSONObject().put("parts", new JSONArray().put(part));
        JSONObject body = new JSONObject().put("contents", new JSONArray().put(content));

        // 2. Open connection + headers
        HttpURLConnection conn = (HttpURLConnection) new URL(ENDPOINT).openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("x-goog-api-key", BuildConfig.GEMINI_API_KEY);
        conn.setRequestProperty("content-type", "application/json");
        conn.setDoOutput(true);
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(60000);

        // 3. Send the body
        try (OutputStream os = conn.getOutputStream()) {
            os.write(body.toString().getBytes(StandardCharsets.UTF_8));
        }

        // 4. Read the response
        int code = conn.getResponseCode();
        InputStream in = (code < 400) ? conn.getInputStream() : conn.getErrorStream();
        String response = readAll(in);
        conn.disconnect();
        if (code >= 400) {
            throw new IOException("API error " + code + ": " + response);
        }

        // 5. Parse: candidates[0].content.parts[0].text
        JSONArray candidates = new JSONObject(response).getJSONArray("candidates");
        JSONArray parts = candidates.getJSONObject(0)
                .getJSONObject("content")
                .getJSONArray("parts");
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.length(); i++) {
            sb.append(parts.getJSONObject(i).optString("text"));
        }
        return sb.toString();
    }

    private String readAll(InputStream in) throws IOException {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte[] chunk = new byte[8192];
        int n;
        while ((n = in.read(chunk)) != -1) {
            out.write(chunk, 0, n);
        }
        return out.toString("UTF-8");
    }

}
