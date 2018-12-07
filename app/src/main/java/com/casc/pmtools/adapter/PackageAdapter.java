package com.casc.pmtools.adapter;

import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;

import com.casc.pmtools.MyApplication;
import com.casc.pmtools.MyVars;
import com.casc.pmtools.R;
import com.casc.pmtools.bean.Package;
import com.casc.pmtools.bean.Task;
import com.casc.pmtools.utils.CommonUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

public class PackageAdapter extends BaseQuickAdapter<Package, BaseViewHolder> {

    public PackageAdapter(@Nullable List<Package> data) {
        super(R.layout.item_package, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, Package item) {
        CardView root = helper.getView(R.id.cv_package);
        if (item.getFlag() == '1') { // 已出库
            root.setCardBackgroundColor(ContextCompat.getColor(MyApplication.getInstance(), R.color.red));
        }
        helper.setText(R.id.tv_package_info, (item.getCribFlag() == '0' ? "整垛" : "散货") + " " + item.getNumber() + "桶");
    }
}
