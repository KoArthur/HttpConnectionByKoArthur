package com.example.httpconnection;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import java.util.UUID;


public class MainActivity extends AppCompatActivity {
    private String url = "http://service.picasso.adesk.com/v1/vertical/category";
    private String params = "adult=false&first=1";
    private String getUrl = "http://service.picasso.adesk.com/v1/vertical/category?adult=false&first=1";
    private String imageUrl = "http://img5.adesk.com/5c921bd5e7bce7554e131894?imageMogr2/thumbnail/!640x480r/gravity/Center/crop/640x480";
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HttpUrlConnectionPool httpUrlConnectionPool = HttpUrlConnectionPool.getInstance();
        HttpInstance httpInstance = new HttpInstance.Builder().Url(url).Parameter(params).builder();
        httpUrlConnectionPool.ConnectionByPOST(httpInstance, new HttpInstance.Callback() {
            @Override
            public void finish(String response) {
                Log.d("response", response);
            }
        });

        HttpInstance httpInstance1 = new HttpInstance.Builder().Url(getUrl).builder();
        httpUrlConnectionPool.ConnectionByGET(httpInstance1, new HttpInstance.Callback() {
            @Override
            public void finish(String response) {
                Log.d("response", response);
            }
        });

        //检查是否有SD卡存储权限，没有则申请权限
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        ImageDownloader imageDownloader = new ImageDownloader.Builder().urlPath(imageUrl).
                pictureName(UUID.randomUUID() + "").builder();
        httpUrlConnectionPool.DownloadPicture(getBaseContext(), imageDownloader);

    }
}
