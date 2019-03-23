package com.example.httpconnection;

import android.content.Context;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpUrlConnectionPool {
    private HttpUrlConnectionPool() {}

    public static HttpUrlConnectionPool getInstance() {
        return SingletHolder.httpURLConnectionPool;
    }

    public ExecutorService getCachePool() {
        return SingletHolder.cachePool;
    }

    private static class SingletHolder {
        private static final ExecutorService cachePool = Executors.newCachedThreadPool();
        private static final HttpUrlConnectionPool httpURLConnectionPool = new HttpUrlConnectionPool();
    }

    public void ConnectionByPOST(HttpInstance httpInstance, HttpInstance.Callback callback) {
        httpInstance.SendRequestByPOST(callback);
    }

    public void ConnectionByGET(HttpInstance httpInstance, HttpInstance.Callback callback) {
        httpInstance.SendRequestByGET(callback);
    }

    public void DownloadPicture(Context context,ImageDownloader imageDownloader) {
        imageDownloader.Downloader(context, imageDownloader);

    }
}