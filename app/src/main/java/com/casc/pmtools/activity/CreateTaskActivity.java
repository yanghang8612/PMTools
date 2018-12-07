package com.casc.pmtools.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.bean.Config;
import com.casc.pmtools.helper.NetAdapter;
import com.casc.pmtools.helper.NetHelper;
import com.casc.pmtools.helper.param.Reply;
import com.casc.pmtools.message.ConfigUpdatedMessage;
import com.casc.pmtools.message.TaskAlertedMessage;
import com.casc.pmtools.message.TaskCreatedMessage;
import com.casc.pmtools.utils.ActivityCollector;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateTaskActivity extends BaseActivity {

    private static final String TAG = CreateTaskActivity.class.getSimpleName();
    private static final String TASK_ID = "task_id";

    public static void actionStart(Context context) {
        actionStart(context, "");
    }

    public static void actionStart(Context context, String taskID) {
        if (!(ActivityCollector.getTopActivity() instanceof CreateTaskActivity)) {
            Intent intent = new Intent(context, CreateTaskActivity.class);
            intent.putExtra(TASK_ID, taskID);
            context.startActivity(intent);
        }
    }

    @BindView(R.id.cl_create_task) ConstraintLayout mRootCl;
    @BindView(R.id.tb_create_task) Toolbar mToolbar;
    @BindView(R.id.sp_product_name) Spinner mProductNameSp;
    @BindView(R.id.et_product_number) EditText mProductNumberEt;
    @BindView(R.id.btn_task_operation) Button mTaskOperationBtn;

    private String mTaskID;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ConfigUpdatedMessage message) {
        mProductNameSp.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item,
                MyVars.config.getProducts()));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_task);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mTaskID = getIntent().getStringExtra(TASK_ID);
        mTaskOperationBtn.setText(TextUtils.isEmpty(mTaskID) ? "创建任务单" : "提交修改");

        NetHelper.getInstance().getConfig(MyVars.user.getUserID()).enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                Reply reply = response.body();
                if (response.isSuccessful() && reply != null && reply.getCode() == 200) {
                    //SpHelper.setParam("config_json", reply.getContent().toString());
                    MyVars.config = new Gson().fromJson(reply.getContent().toString(), Config.class);
                    EventBus.getDefault().post(new ConfigUpdatedMessage());
                }
            }

            @Override
            public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                Snackbar.make(mRootCl, "网络连接失败，请检查后重试", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.btn_task_operation)
    void onCreateTaskButtonClicked() {
        if (mProductNameSp.getSelectedItem() == null) {
            Snackbar.make(mRootCl, "请选择产品名称", Snackbar.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(mProductNumberEt.getText())) {
            Snackbar.make(mRootCl, "请输入产品数量", Snackbar.LENGTH_SHORT).show();
        } else {
            final String productName = mProductNameSp.getSelectedItem().toString();
            final int productCount = Integer.valueOf(mProductNumberEt.getText().toString());
            if (TextUtils.isEmpty(mTaskID)) {
                NetHelper.getInstance().createTask(MyVars.user.getUserID(),
                        MyVars.config.getProductCodeByName(productName),
                        productCount).enqueue(new NetAdapter() {
                    @Override
                    public void onSuccess(Reply reply) {
                        EventBus.getDefault().post(new TaskCreatedMessage());
                        finish();
                    }
                });
            } else {
                NetHelper.getInstance().alterTask(mTaskID,
                        MyVars.config.getProductCodeByName(productName),
                        productCount).enqueue(new NetAdapter() {
                    @Override
                    public void onSuccess(Reply reply) {
                        EventBus.getDefault().post(new TaskAlertedMessage(productName, productCount));
                        finish();
                    }
                });

            }
        }
    }
}
