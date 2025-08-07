package com.ecoapkdownload;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import androidx.annotation.NonNull;

import com.hjq.http.EasyConfig;
import com.hjq.http.EasyLog;
import com.hjq.http.config.IRequestHandler;
import com.hjq.http.exception.CancelException;
import com.hjq.http.exception.DataException;
import com.hjq.http.exception.HttpException;
import com.hjq.http.exception.NetworkException;
import com.hjq.http.exception.ResponseException;
import com.hjq.http.exception.ServerException;
import com.hjq.http.exception.TimeoutException;
import com.hjq.http.request.HttpRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import okhttp3.Headers;
import okhttp3.Response;
import okhttp3.ResponseBody;

public final class RequestHandler implements IRequestHandler {
    @NonNull
    @Override
    public Object requestSucceed(@NonNull HttpRequest<?> httpRequest, @NonNull Response response, @NonNull Type type) throws Exception {
        String result=response.body().string();
        return result;
    }

    @NonNull
    @Override
    public Exception requestFail(@NonNull HttpRequest<?> httpRequest, @NonNull Exception e) {
        // 判断这个异常是不是自己抛的
        return new HttpException(e.getMessage(), e);
    }

}
