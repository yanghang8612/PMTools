package com.casc.pmtools.activity;

import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.bean.User;
import com.casc.pmtools.helper.NetHelper;
import com.casc.pmtools.helper.SpHelper;
import com.casc.pmtools.helper.param.Reply;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SplashActivity extends BaseActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    @BindView(R.id.spin_kit) SpinKitView mProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mProgressBar.setVisibility(View.VISIBLE);
            }
        }, 500);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (TextUtils.isEmpty(SpHelper.getString("username"))) {
                    LoginActivity.actionStart(SplashActivity.this);
                    finish();
                } else {
                    String username = SpHelper.getString("username");
                    String password = SpHelper.getString("password");
                    NetHelper.getInstance().login(username, password).enqueue(new Callback<Reply>() {
                        @Override
                        public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                            Reply reply = response.body();
                            if (response.isSuccessful() && reply != null) {
                                if (reply.getCode() == 200) {
                                    MyVars.user = new Gson().fromJson(reply.getContent(), User.class);
                                    MainActivity.actionStart(SplashActivity.this);
                                    finish();
                                } else {
                                    showToast(reply.getMessage() + "，请重新登录");
                                    SpHelper.clearAll();
                                    LoginActivity.actionStart(SplashActivity.this);
                                    finish();
                                }
                            } else {
                                showToast("Error code: " + response.code());
                                LoginActivity.actionStart(SplashActivity.this);
                                finish();
                            }
                        }

                        @Override
                        public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                            showToast("网络连接失败，请检查后重试");
                            LoginActivity.actionStart(SplashActivity.this);
                            finish();
                        }
                    });
                }

            }
        }, 2000);
    }
}
