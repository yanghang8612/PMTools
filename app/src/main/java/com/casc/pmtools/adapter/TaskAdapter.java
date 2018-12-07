package com.casc.pmtools.adapter;

import android.support.annotation.Nullable;

import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.bean.Task;
import com.casc.pmtools.utils.CommonUtils;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class TaskAdapter extends BaseItemDraggableAdapter<Task, BaseViewHolder> {

    public TaskAdapter(@Nullable List<Task> data) {
        super(R.layout.item_task, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Task item) {
        switch (item.getFlag()) {
            case '0':
                helper.setImageResource(R.id.iv_task_status, R.drawable.ic_task_0)
                        .setText(R.id.tv_task_status, "尚未生产");
                break;
            case '1':
                helper.setImageResource(R.id.iv_task_status, R.drawable.ic_task_1)
                        .setText(R.id.tv_task_status, "正在生产");
                break;
            case '2':
                helper.setImageResource(R.id.iv_task_status, R.drawable.ic_task_2)
                        .setText(R.id.tv_task_status, "上线完成");
                break;
            case '3':
                helper.setImageResource(R.id.iv_task_status, R.drawable.ic_task_3)
                        .setText(R.id.tv_task_status, "生产完成");
                break;
            case '4':
                helper.setImageResource(R.id.iv_task_status, R.drawable.ic_task_0)
                        .setText(R.id.tv_task_status, "已核验");
                break;
        }
        helper.setText(R.id.tv_task_id, item.getTaskID())
                .setText(R.id.tv_task_create_time, CommonUtils.convertDateTime(item.getTime()))
                .setText(R.id.tv_task_info, item.getTaskInfo())
                .setText(R.id.tv_task_owner, item.getUsername())
                .setGone(R.id.tv_task_owner_label, !MyVars.user.isAdmin())
                .setGone(R.id.tv_task_owner, !MyVars.user.isAdmin());
    }
}
