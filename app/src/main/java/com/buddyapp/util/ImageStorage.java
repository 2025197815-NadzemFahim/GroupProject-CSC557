package com.buddyapp.util;

import android.content.Context;
import android.net.Uri;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public final class ImageStorage {

    private static final String PHOTO_DIR = "friend_photos";
    private static final int BUFFER_SIZE = 8192;

    private ImageStorage() {
    }

    public static String copyToInternal(Context context, Uri source) {
        File dir = new File(context.getFilesDir(), PHOTO_DIR);
        if (!dir.exists() && !dir.mkdirs()) {
            return null;
        }
        File dest = new File(dir, UUID.randomUUID() + ".jpg");
        try (InputStream in = context.getContentResolver().openInputStream(source);
             OutputStream out = new FileOutputStream(dest)) {
            if (in == null) {
                return null;
            }
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            return Uri.fromFile(dest).toString();
        } catch (IOException e) {
            return null;
        }
    }
}
