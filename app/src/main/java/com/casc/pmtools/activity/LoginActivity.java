package com.casc.pmtools.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;

import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.bean.User;
import com.casc.pmtools.helper.NetHelper;
import com.casc.pmtools.helper.SpHelper;
import com.casc.pmtools.helper.param.Reply;
import com.google.gson.Gson;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.til_login_username) TextInputLayout mUsernameTil;
    @BindView(R.id.til_login_password) TextInputLayout mPasswordTil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mUsernameTil.getEditText().setText(SpHelper.getString("username"));
    }

    @OnClick(R.id.btn_sign_in)
    void onSignInButtonClicked() {
//        MainActivity.actionStart(this);
//        finish();
        attemptLogin();
    }

    @OnFocusChange(R.id.met_login_username)
    void onUsernameTilFocusChange(boolean focused) {
        if (focused) {
            mUsernameTil.setErrorEnabled(false);
        }
    }

    @OnFocusChange(R.id.met_login_password)
    void onPasswordTilFocusChange(boolean focused) {
        if (focused) {
            mPasswordTil.setErrorEnabled(false);
        }
    }

    private void attemptLogin() {
        hideKeyboard();
        final String username = mUsernameTil.getEditText().getText().toString();
        final String password = mPasswordTil.getEditText().getText().toString();

        if (TextUtils.isEmpty(username)) {
            mUsernameTil.setError("请输入用户名");
            return;
        } else {
            mUsernameTil.setErrorEnabled(false);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordTil.setError("请输入密码");
            return;
        } else {
            mPasswordTil.setErrorEnabled(false);
        }

        NetHelper.getInstance().login(username, password).enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                Reply reply = response.body();
                if (response.isSuccessful() && reply != null) {
                    if (reply.getCode() == 200) {
                        SpHelper.setParam("username", username);
                        SpHelper.setParam("password", password);
                        MyVars.user = new Gson().fromJson(reply.getContent(), User.class);
                        finish();
                        MainActivity.actionStart(LoginActivity.this);
                    } else {
                        showToast(reply.getMessage() + ",请检查后重试");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                showToast("网络连接失败，请检查后重试");
            }
        });
    }
}
