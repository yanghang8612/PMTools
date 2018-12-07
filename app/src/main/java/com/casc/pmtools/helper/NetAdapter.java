package com.casc.pmtools.helper;

import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;

import com.casc.pmtools.helper.param.Reply;
import com.casc.pmtools.utils.ActivityCollector;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetAdapter implements Callback<Reply> {

    public abstract void onSuccess(Reply reply);

    @Override
    public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
        Reply reply = response.body();
        if (response.isSuccessful() && reply != null) {
            if (reply.getCode() == 200) {
                onSuccess(reply);
            } else {
                Snackbar.make(ActivityCollector.getTopActivity().getWindow().getDecorView(), reply.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        } else {
            Snackbar.make(ActivityCollector.getTopActivity().getWindow().getDecorView(), "平台内部错误(" + response.code() + "),请稍后重试", Snackbar.LENGTH_LONG).show();
        }
    }

    @Override
    public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
        Snackbar.make(ActivityCollector.getTopActivity().getWindow().getDecorView(), "网络连接失败,请检查后重试", Snackbar.LENGTH_SHORT).show();
    }
}
