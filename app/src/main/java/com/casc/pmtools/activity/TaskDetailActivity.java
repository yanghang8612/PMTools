package com.casc.pmtools.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.casc.pmtools.MyParams;
import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.adapter.PackageAdapter;
import com.casc.pmtools.bean.Detail;
import com.casc.pmtools.bean.Package;
import com.casc.pmtools.bean.Task;
import com.casc.pmtools.helper.NetAdapter;
import com.casc.pmtools.helper.NetHelper;
import com.casc.pmtools.helper.param.Reply;
import com.casc.pmtools.message.DecodeResultMessage;
import com.casc.pmtools.message.TaskAlertedMessage;
import com.casc.pmtools.message.TaskRefreshedMessage;
import com.casc.pmtools.utils.ActivityCollector;
import com.casc.pmtools.utils.CommonUtils;
import com.casc.pmtools.view.NumberSwitcher;
import com.chad.library.adapter.base.animation.ScaleInAnimation;
import com.chad.library.adapter.base.animation.SlideInRightAnimation;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class TaskDetailActivity extends BaseActivity {

    private static final String TAG = TaskDetailActivity.class.getSimpleName();

    private static final int REQUEST_CODE_CAMERA = 0;
    private static final int REQUEST_CODE_QR_CODE = 1;

    public static void actionStart(Context context) {
        if (!(ActivityCollector.getTopActivity() instanceof TaskDetailActivity)) {
            Intent intent = new Intent(context, TaskDetailActivity.class);
            context.startActivity(intent);
        }
    }

    @BindView(R.id.tb_task_detail) Toolbar mToolbar;
    @BindView(R.id.iv_down_bucket) ImageView mDownBucketIv;
    @BindView(R.id.ll_task_detail) LinearLayout mRootCl;
    @BindView(R.id.iv_task_status) ImageView mTaskStatusIv;
    @BindView(R.id.tv_task_status) TextView mTaskStatusTv;
    @BindView(R.id.tv_task_id) TextView mTaskIDTv;
    @BindView(R.id.tv_task_create_time) TextView mTaskCreateTimeTv;
    @BindView(R.id.tv_task_product_name) TextView mTaskProductNameTv;
    @BindView(R.id.tv_task_product_count) TextView mTaskProductCountTv;
    @BindView(R.id.tv_task_owner_label) TextView mTaskOwnerLabelTv;
    @BindView(R.id.tv_task_owner) TextView mTaskOwnerTv;
    @BindView(R.id.ns_online_count) NumberSwitcher mOnlineCountNs;
    @BindView(R.id.ns_filter_count) NumberSwitcher mFilterCountNs;
    @BindView(R.id.ns_offline_count) NumberSwitcher mOfflineCountNs;
    @BindView(R.id.ns_package_count) NumberSwitcher mPackageCountNs;
    @BindView(R.id.ns_down_count) NumberSwitcher mDownCountNs;
    @BindView(R.id.rv_package_list) RecyclerView mPackageRv;
    @BindView(R.id.btn_affirm_task) Button mAffirmBtn;
    @BindView(R.id.cl_verify_commit) ConstraintLayout mVerifyCommitCl;
    @BindView(R.id.et_package_count) EditText mPackageCountEt;
    @BindView(R.id.et_scatter_count) EditText mScatterCountEt;
    @BindView(R.id.btn_verify_task) Button mVerifyBtn;
    @BindView(R.id.cl_verify_info) ConstraintLayout mVerifyInfoCl;
    @BindView(R.id.tv_package_count) TextView mPackageCountTv;
    @BindView(R.id.tv_scatter_count) TextView mScatterCountTv;

    private Task mTask;
    private Detail mDetail;
    private RefreshTask mRefreshTask;
    private List<Package> mPackages = new ArrayList<>();
    private PackageAdapter mAdapter = new PackageAdapter(mPackages);

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(DecodeResultMessage message) {
        String[] result = message.code.split("=");
        if (result.length == 2 && result[1].length() == MyParams.BODY_CODE_LENGTH) {
            final String code = result[1];
            showDialog("确认Down桶:" + code + "吗？", new MaterialDialog.SingleButtonCallback() {
                @Override
                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                    NetHelper.getInstance().downBucket(mTask.getTaskID(), code).enqueue(new NetAdapter() {
                        @Override
                        public void onSuccess(Reply reply) {
                            Snackbar.make(mRootCl, "(" + code + ")" + "Down桶成功", Snackbar.LENGTH_LONG).show();
                        }
                    });
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskAlertedMessage message) {
        mTaskProductNameTv.setText(message.productName);
        mTaskProductCountTv.setText(message.productCount + "(桶)");
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskRefreshedMessage message) {
        setTaskStatus(mDetail.getFlag());
        mOnlineCountNs.setNumber(mDetail.getOnlineNumber());
        mFilterCountNs.setNumber(mDetail.getFilterNumber());
        mOfflineCountNs.setNumber(mDetail.getOfflineNumber());
        mPackageCountNs.setNumber(mDetail.getPackageNumber());
        mDownCountNs.setNumber(mDetail.getDownNumber());
        mPackageCountTv.setText(String.valueOf(mDetail.getCribNumber()));
        mScatterCountTv.setText(String.valueOf(mDetail.getScatterNumber()));

//        switch ((int) System.currentTimeMillis() % 4) {
//            case 0:
//                mOnlineCountNs.setNumber(mOnlineCountNs.getNumber() + (int) System.currentTimeMillis() % 10);
//                break;
//            case 1:
//                mFilterCountNs.setNumber(mFilterCountNs.getNumber() + (int) System.currentTimeMillis() % 10);
//                break;
//            case 2:
//                mOfflineCountNs.setNumber(mOfflineCountNs.getNumber() + (int) System.currentTimeMillis() % 10);
//                break;
//            case 3:
//                mPackageCountNs.setNumber(mPackageCountNs.getNumber() + (int) System.currentTimeMillis() % 10);
//                break;
//        }

        mPackages.clear();
        mPackages.addAll(mDetail.getPackages());
        mAdapter.notifyDataSetChanged();

        switch (mDetail.getFlag()) {
            case '0':
            case '1':
                mAffirmBtn.setVisibility(View.GONE);
                mVerifyCommitCl.setVisibility(View.GONE);
                mVerifyInfoCl.setVisibility(View.GONE);
                break;
            case '2':
                mAffirmBtn.setVisibility(MyVars.user.isAdmin() ? View.VISIBLE : View.GONE);
                mVerifyCommitCl.setVisibility(View.GONE);
                mVerifyInfoCl.setVisibility(View.GONE);
                break;
            case '3':
                mAffirmBtn.setVisibility(View.GONE);
                mVerifyCommitCl.setVisibility(MyVars.user.isAdmin() ? View.VISIBLE : View.GONE);                                                                                                                                                                                                                       ;
                mVerifyInfoCl.setVisibility(View.GONE);                                                                                                                                                                                                                       ;
                break;
            case '4':
                mAffirmBtn.setVisibility(View.GONE);
                mVerifyCommitCl.setVisibility(View.GONE);
                mVerifyInfoCl.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(mToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mDownBucketIv.setVisibility(MyVars.user.isAdmin() ? View.VISIBLE : View.GONE);
        mTaskOwnerLabelTv.setVisibility(MyVars.user.isAdmin() ? View.GONE : View.VISIBLE);
        mTaskOwnerTv.setVisibility(MyVars.user.isAdmin() ? View.GONE : View.VISIBLE);
        mTask = MyVars.task;
        setTaskStatus(mTask.getFlag());
        mTaskIDTv.setText(mTask.getTaskID());
        mTaskCreateTimeTv.setText(CommonUtils.convertDateTime(mTask.getTime()));
        mTaskProductNameTv.setText(mTask.getProductName());
        mTaskProductCountTv.setText(CommonUtils.covertBucketNumToStr(mTask.getNumber()));
        mTaskOwnerTv.setText(mTask.getUsername());

        mOnlineCountNs.setNumber(0);
        mFilterCountNs.setNumber(0);
        mOfflineCountNs.setNumber(0);
        mPackageCountNs.setNumber(0);
        mDownCountNs.setNumber(0);

        mAdapter.openLoadAnimation(new ScaleInAnimation());
        mPackageRv.setLayoutManager(new GridLayoutManager(this, 4));
        mPackageRv.setAdapter(mAdapter);

        mRefreshTask = new RefreshTask();
        MyVars.executor.execute(mRefreshTask);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRefreshTask.stop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_modify_task:
                CreateTaskActivity.actionStart(this, mTask.getTaskID());
                break;
            case R.id.action_delete_task:
                if (mTask.getFlag() == '0') {
                    showDialog("确认删除该任务单吗？", new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                            NetHelper.getInstance().deleteTask(mTask.getTaskID()).enqueue(new NetAdapter() {
                                @Override
                                public void onSuccess(Reply reply) {
                                    showToast("删除成功");
                                    finish();
                                }
                            });
                        }
                    });
                } else {
                    Snackbar.make(mRootCl, "已开始生产的任务单无法删除", Snackbar.LENGTH_SHORT).show();
                }
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (MyVars.user.isAdmin()) {
            getMenuInflater().inflate(R.menu.task_operations, menu);
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    DecoderActivity.actionStart(this);
//                    Intent intent = new Intent(this, CaptureActivity.class);
//                    startActivityForResult(intent, REQUEST_CODE_QR_CODE);

                } else {
                    Snackbar.make(mRootCl, "未获得相机权限", Snackbar.LENGTH_SHORT).show();
                }
                break;
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, final int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        switch (requestCode) {
//            case REQUEST_CODE_QR_CODE:
//                if (data != null && data.hasExtra(CodeUtils.RESULT_STRING)) {
//                    String content = data.getStringExtra(CodeUtils.RESULT_STRING);
//                    String[] result = content.split("=");
//                    if (result.length == 2 && result[1].length() == MyParams.BODY_CODE_LENGTH) {
//                        final String code = result[1];
//                        showDialog("确认Down桶:" + code + "吗？", new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                NetHelper.getInstance().downBucket(mTask.getTaskID(), code).enqueue(new NetAdapter() {
//                                    @Override
//                                    public void onSuccess(Reply reply) {
//                                        Snackbar.make(mRootCl, "(" + code + ")" + "Down桶成功", Snackbar.LENGTH_LONG).show();
//                                    }
//                                });
//                            }
//                        });
//                    }
//                }
//                break;
//        }
//    }

    @OnClick(R.id.iv_down_bucket)
    void onDownBucketIvClicked() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    REQUEST_CODE_CAMERA);
        } else {
//            Intent intent = new Intent(this, CaptureActivity.class);
//            startActivityForResult(intent, REQUEST_CODE_QR_CODE);
            DecoderActivity.actionStart(this);
        }
    }

    @OnClick(R.id.btn_affirm_task)
    void onAffirmTaskBtnClicked() {
        showDialog("确认提交当前的任务单吗？", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                NetHelper.getInstance().affirmTask(mTask.getTaskID()).enqueue(new NetAdapter() {
                    @Override
                    public void onSuccess(Reply reply) {
                        Snackbar.make(mRootCl, "确认成功，请核验该任务单", Snackbar.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    @OnClick(R.id.btn_verify_task)
    void onVerifyTaskBtnClicked() {
        String packageCountStr = mPackageCountEt.getText().toString();
        final int packageCount = TextUtils.isEmpty(packageCountStr) ? -1 : Integer.valueOf(packageCountStr);
        String scatterCountStr = mScatterCountEt.getText().toString();
        final int scatterCount = TextUtils.isEmpty(scatterCountStr) ? -1 : Integer.valueOf(scatterCountStr);
        showDialog("确认提交核验信息吗？", new MaterialDialog.SingleButtonCallback() {
            @Override
            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                NetHelper.getInstance().verifyTask(mTask.getTaskID(), packageCount, scatterCount)
                        .enqueue(new NetAdapter() {
                            @Override
                            public void onSuccess(Reply reply) {
                                Snackbar.make(mRootCl, "核验成功，该生产任务单完成", Snackbar.LENGTH_LONG).show();
                            }
                        });
            }
        });
    }

    private void setTaskStatus(char flag) {
        switch (flag) {
            case '0':
                mTaskStatusIv.setImageResource(R.drawable.ic_task_0);
                mTaskStatusTv.setText("尚未生产");
                break;
            case '1':
                mTaskStatusIv.setImageResource(R.drawable.ic_task_1);
                mTaskStatusTv.setText("正在生产");
                break;
            case '2':
                mTaskStatusIv.setImageResource(R.drawable.ic_task_2);
                mTaskStatusTv.setText("上线完成");
                break;
            case '3':
                mTaskStatusIv.setImageResource(R.drawable.ic_task_3);
                mTaskStatusTv.setText("生产完成");
                break;
            case '4':
                mTaskStatusIv.setImageResource(R.drawable.ic_task_0);
                mTaskStatusTv.setText("已核验");
                break;
        }
    }

    private void showDialog(String content, MaterialDialog.SingleButtonCallback callback) {
        new MaterialDialog.Builder(this)
                .content(content)
                .positiveText("确认")
                .positiveColorRes(R.color.white)
                .btnSelector(R.drawable.md_btn_postive, DialogAction.POSITIVE)
                .negativeText("取消")
                .negativeColorRes(R.color.grey)
                .onPositive(callback)
                .show();
    }

    private class RefreshTask implements Runnable {

        private boolean isRunning = true;

        @Override
        public void run() {
            while (isRunning) {
                NetHelper.getInstance().getTaskDetail(mTask.getTaskID()).enqueue(new NetAdapter() {
                    @Override
                    public void onSuccess(Reply reply) {
                        mDetail = new Gson().fromJson(reply.getContent(), Detail.class);
                        EventBus.getDefault().post(new TaskRefreshedMessage());
                    }
                });
                try {
                    Thread.sleep(MyParams.TASK_DETAIL_REFRESH_INTERVAL);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        public void stop() {
            isRunning = false;
        }
    }
}
