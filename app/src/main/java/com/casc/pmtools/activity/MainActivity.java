package com.casc.pmtools.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;

import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.adapter.TaskAdapter;
import com.casc.pmtools.bean.Task;
import com.casc.pmtools.helper.NetHelper;
import com.casc.pmtools.helper.SpHelper;
import com.casc.pmtools.helper.param.Reply;
import com.casc.pmtools.message.TaskCreatedMessage;
import com.casc.pmtools.message.TaskUpdatedMessage;
import com.casc.pmtools.utils.CommonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.github.ybq.android.spinkit.SpinKitView;
import com.google.gson.Gson;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.srain.cube.views.ptr.PtrFrameLayout;
import in.srain.cube.views.ptr.PtrHandler;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.tb_main) Toolbar mToolbar;
    @BindView(R.id.dl_main) DrawerLayout mMainDl;
    @BindView(R.id.nv_main) NavigationView mMainNv;
    @BindView(R.id.pfl_task_list) PtrFrameLayout mTasksPfl;
    @BindView(R.id.skv_task_list) SpinKitView mTasksSkv;
    @BindView(R.id.rv_task_list) RecyclerView mTasksRv;
    @BindView(R.id.fab_create_task) FloatingActionButton mCreateTaskFab;

    private List<Task> mTasks = new ArrayList<>();
    private TaskAdapter mAdapter = new TaskAdapter(mTasks);
    private LinearLayoutManager mAdapterLlm;
    private View mEmptyView;

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskCreatedMessage message) {
        refreshTaskList();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(TaskUpdatedMessage message) {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(mToolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mMainDl, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mMainDl.addDrawerListener(toggle);
        toggle.syncState();
        mMainNv.setNavigationItemSelectedListener(this);
        ((TextView) mMainNv.getHeaderView(0).findViewById(R.id.tv_user_name)).setText(SpHelper.getString("username"));
        ((TextView) mMainNv.getHeaderView(0).findViewById(R.id.tv_user_role)).setText(CommonUtils.getUserRoleStr(MyVars.user.getRole()));
        mCreateTaskFab.setVisibility(MyVars.user.isAdmin() ? View.VISIBLE : View.GONE);
        mCreateTaskFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateTaskActivity.actionStart(MainActivity.this);
            }
        });

        refreshTaskList();
        mTasksPfl.setPtrHandler(new PtrHandler() {
            @Override
            public boolean checkCanDoRefresh(PtrFrameLayout frame, View content, View header) {
                int pos = mAdapterLlm.findFirstCompletelyVisibleItemPosition();
                return pos == -1 || pos == 0;
            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                refreshTaskList();
            }
        });

        //mAdapter.openLoadAnimation();
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                MyVars.task = mTasks.get(position);
                TaskDetailActivity.actionStart(MainActivity.this);
            }
        });
        //mAdapter.openLoadAnimation(new ScaleInAnimation());
        mAdapterLlm = new LinearLayoutManager(this);
        mTasksRv.setLayoutManager(mAdapterLlm);
        mTasksRv.setAdapter(mAdapter);
        mEmptyView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) mTasksRv.getParent(), false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTasksPfl.autoRefresh(true);
    }

    @Override
    public void onBackPressed() {
        if (mMainDl.isDrawerOpen(GravityCompat.START)) {
            mMainDl.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    private class Tasks {

        private List<Task> taskinfo;

        public List<Task> getTasks() {
            return taskinfo;
        }
    }

    private void refreshTaskList() {
        NetHelper.getInstance().getAllTasks(MyVars.user.getUserID()).enqueue(new Callback<Reply>() {
            @Override
            public void onResponse(@NonNull Call<Reply> call, @NonNull Response<Reply> response) {
                Reply reply = response.body();
                if (response.isSuccessful() && reply != null && reply.getCode() == 200) {
                    mTasks.clear();
                    if (!reply.getContent().isJsonNull()) {
                        mTasks.addAll(new Gson().fromJson(reply.getContent(), Tasks.class).getTasks());
                        Iterator<Task> i = mTasks.iterator();
                        while (i.hasNext()) {
                            Task task = i.next();
                            if (task.getFlag() == '4') {
                                i.remove();
                            }
                        }
                    }
                    if (mTasks.isEmpty()) {
                        mAdapter.setEmptyView(mEmptyView);
                    }
                    EventBus.getDefault().post(new TaskUpdatedMessage());
                } else {
                    Snackbar.make(mCreateTaskFab, "平台内部错误，请联系运维人员", Snackbar.LENGTH_SHORT).show();
                }
                mTasksPfl.refreshComplete();
            }

            @Override
            public void onFailure(@NonNull Call<Reply> call, @NonNull Throwable t) {
                Snackbar.make(mCreateTaskFab, "网络连接失败，请检查后重试", Snackbar.LENGTH_SHORT).show();
                mTasksPfl.refreshComplete();
            }
        });
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_exit_app) {
            SpHelper.clearAll();
            LoginActivity.actionStart(this);
            finish();
        }

        mMainDl.closeDrawer(GravityCompat.START);
        return true;
    }
}
