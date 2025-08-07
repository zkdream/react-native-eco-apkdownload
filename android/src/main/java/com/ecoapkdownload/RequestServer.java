package com.ecoapkdownload;


import androidx.annotation.NonNull;

import com.hjq.http.config.IRequestServer;
import com.hjq.http.model.BodyType;

public class RequestServer implements IRequestServer {
    @NonNull
    @Override
    public BodyType getBodyType() {
        return BodyType.FORM;
    }

    @NonNull
    @Override
    public String getHost() {
        return "https://api.ecosteam.cn";
    }

//    @Override
//    public String getHost() {
//        return AppConfig.getHostUrl();
//    }
//
//    @Override
//    public String getPath() {
//        return "api/";
//    }
//
//    @Override
//    public BodyType getType() {
//        // 以表单的形式提交参数
//        return BodyType.FORM;
//    }
}
