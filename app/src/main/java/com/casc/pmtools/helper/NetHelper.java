package com.casc.pmtools.helper;

import com.casc.pmtools.MyParams;
import com.casc.pmtools.helper.param.MsgAffirmTask;
import com.casc.pmtools.helper.param.MsgAlertTask;
import com.casc.pmtools.helper.param.MsgDownBucket;
import com.casc.pmtools.helper.param.MsgLogin;
import com.casc.pmtools.helper.param.MsgLogout;
import com.casc.pmtools.helper.param.MsgSingleTaskID;
import com.casc.pmtools.helper.param.MsgSingleUserID;
import com.casc.pmtools.helper.param.MsgCreateTask;
import com.casc.pmtools.helper.param.MsgVerifyTask;
import com.casc.pmtools.helper.param.Reply;
import com.casc.pmtools.utils.CommonUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetHelper {

    private final String TAG = NetHelper.class.getSimpleName();

    private final NetInterface netInterface;

    private static class SingletonHolder{
        private static final NetHelper instance = new NetHelper();
    }

    private NetHelper() {
        this.netInterface = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(MyParams.S_MAIN_PLATFORM_ADDR)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(MyParams.NET_CONNECT_TIMEOUT, TimeUnit.SECONDS)
                        .writeTimeout(MyParams.NET_RW_TIMEOUT, TimeUnit.SECONDS)
                        .readTimeout(MyParams.NET_RW_TIMEOUT, TimeUnit.SECONDS)
                        .build())
                .build()
                .create(NetInterface.class);
    }

    public static NetHelper getInstance() {
        return SingletonHolder.instance;
    }

    public Call<Reply> login(String username, String password) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/login",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgLogin(username, password)));
    }

    public Call<Reply> logout(int userID) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/logout",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgLogout(userID)));
    }

    public Call<Reply> getConfig(int userID) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/parameter",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgSingleUserID(userID)));
    }

    public Call<Reply> createTask(int userID, int productCode, int number) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskcomfirm",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgCreateTask(userID, productCode, number)));

    }

    public Call<Reply> getAllTasks(int userID) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskrequest",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgSingleUserID(userID)));

    }

    public Call<Reply> getTaskDetail(String taskID) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskdetail",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgSingleTaskID(taskID)));
    }

    public Call<Reply> downBucket(String taskID, String bodyCode) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/downbucket",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgDownBucket(taskID, bodyCode)));
    }

    public Call<Reply> affirmTask(String taskID) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskaffirm",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgAffirmTask(taskID)));
    }

    public Call<Reply> verifyTask(String taskID, int cribNum, int scatterNum) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskverify",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgVerifyTask(taskID, cribNum, scatterNum)));
    }

    public Call<Reply> alterTask(String taskID, int productCode, int number) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskalter",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgAlertTask(taskID, productCode, number)));
    }

    public Call<Reply> deleteTask(String taskID) {
        return netInterface.post(
                MyParams.S_MAIN_PLATFORM_ADDR + "/api/message/app/productiontaskdelete",
                CommonUtils.generateRequestHeader("02"),
                CommonUtils.generateRequestBody(new MsgSingleTaskID(taskID)));
    }
}
