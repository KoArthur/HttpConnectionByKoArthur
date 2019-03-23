package com.example.httpconnection;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class ImageDownloader {
    private String urlPath = null;
    private String pictureName = null;

    private ImageDownloader() {
    }

    private ImageDownloader(ImageDownloader imageDownloader) {
        this.urlPath = imageDownloader.urlPath;
        this.pictureName = imageDownloader.pictureName;
    }

    public static class Builder {
        private ImageDownloader imageDownloader;

        public Builder() {
            imageDownloader = new ImageDownloader();
        }

        public Builder urlPath(String url) {
            imageDownloader.urlPath = url;
            return this;
        }

        public Builder pictureName(String name) {
            imageDownloader.pictureName = name;
            return this;
        }

        public ImageDownloader builder() {
            return new ImageDownloader(imageDownloader);
        }
    }

    public void Downloader(final Context context, final ImageDownloader imageDownloader) {
        HttpUrlConnectionPool httpUrlConnectionPool = HttpUrlConnectionPool.getInstance();
        ExecutorService cachePool = httpUrlConnectionPool.getCachePool();
        cachePool.execute(new Runnable() {
            @Override
            public void run() {
                URL imageUrl = null;
                Bitmap bitmap = null;
                try {
                    imageUrl = new URL(imageDownloader.urlPath);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                if (imageUrl != null) {
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) imageUrl.openConnection();
                        httpURLConnection.setReadTimeout(5000);
                        httpURLConnection.setConnectTimeout(5000);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.connect();
                        InputStream inputStream = httpURLConnection.getInputStream();
                        bitmap = BitmapFactory.decodeStream(inputStream);
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    File pictureFile = new File(Environment.getExternalStorageDirectory(), "AndroidBackground");
                    if (!pictureFile.exists()) {
                        pictureFile.mkdir();
                    }
                    imageDownloader.pictureName += ".jpg";
                    File file = new File(pictureFile, imageDownloader.pictureName);
                    if (bitmap != null) {
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
                            fileOutputStream.flush();
                            fileOutputStream.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    //存储在图库中
                    try {
                        MediaStore.Images.Media.insertImage(context.getContentResolver(), file.getAbsolutePath(),
                                imageDownloader.pictureName, null);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE), String.valueOf(Uri.parse(file.getAbsolutePath())));
                }
            }
        });
    }

}
