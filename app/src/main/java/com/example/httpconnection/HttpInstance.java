package com.example.httpconnection;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;

public class HttpInstance {
    private String urlPath = null;
    private String parameter = null;
    private HttpInstance() {}

    private HttpInstance(HttpInstance httpInstance) {
        this.urlPath = httpInstance.urlPath;
        this.parameter = httpInstance.parameter;
    }

    public interface Callback {
        void finish(String response);
    }

    public void SendRequestByGET(final Callback callback) {
        HttpUrlConnectionPool httpUrlConnectionPool = HttpUrlConnectionPool.getInstance();
        final ExecutorService cachePool = httpUrlConnectionPool.getCachePool();
        cachePool.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(urlPath);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                try {
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    httpURLConnection.setRequestMethod("GET");
                    httpURLConnection.setReadTimeout(5000);
                    httpURLConnection.setConnectTimeout(5000);
                    httpURLConnection.setRequestProperty("Charset", "UTF-8");
                    if (httpURLConnection.getResponseCode() == 200) {
                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder data = new StringBuilder();
                        String line = "";
                        while ((line = bufferedReader.readLine()) != null) {
                            data.append(line);
                        }
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        if (callback != null) {
                            callback.finish(data.toString());
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    public void SendRequestByPOST(final Callback callback) {
        HttpUrlConnectionPool httpURLConnectionPool = HttpUrlConnectionPool.getInstance();
        ExecutorService cachePool = httpURLConnectionPool.getCachePool();
        cachePool.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;

                try {
                    url = new URL(urlPath);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                if (url != null) {
                    try {
                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                        httpURLConnection.setReadTimeout(5000);
                        httpURLConnection.setConnectTimeout(5000);
                        httpURLConnection.setRequestMethod("POST");
                        httpURLConnection.setInstanceFollowRedirects(true);
                        httpURLConnection.setUseCaches(false);
                        httpURLConnection.setDoOutput(true);
                        httpURLConnection.setDoInput(true);
                        httpURLConnection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
                        //打开链接
                        httpURLConnection.connect();
                        DataOutputStream dataOutputStream = new DataOutputStream(httpURLConnection.getOutputStream());
                        dataOutputStream.write(parameter.getBytes());
                        dataOutputStream.flush();
                        dataOutputStream.close();

                        InputStream inputStream = httpURLConnection.getInputStream();
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                        StringBuilder data = new StringBuilder();
                        String line = null;
                        while ((line = bufferedReader.readLine()) != null) {
                            data.append(line);
                        }
                        inputStream.close();
                        bufferedReader.close();
                        httpURLConnection.disconnect();
                        if (callback != null) {
                            callback.finish(data.toString());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public String getUrl() {
        return urlPath;
    }

    public String getParameter() {
        return parameter;
    }

    public static class Builder {
        private HttpInstance httpInstance;

        public Builder() {
            httpInstance = new HttpInstance();
        }

        public Builder Url(String url) {
            httpInstance.urlPath = url;
            return this;
        }

        public Builder Parameter(String parameter) {
            httpInstance.parameter = parameter;
            return this;
        }

        public HttpInstance builder() {
            return new HttpInstance(httpInstance);
        }
    }

}
