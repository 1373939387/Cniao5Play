package com.haoruigang.cniao5play.ui.fragment;

import androidx.recyclerview.widget.RecyclerView;

import com.haoruigang.cniao5play.di.component.AppComponent;
import com.haoruigang.cniao5play.di.component.DaggerAppManagerComponent;
import com.haoruigang.cniao5play.di.module.AppManagerModule;
import com.haoruigang.cniao5play.ui.adapter.DownloadingAdapter;

import java.util.List;

import zlc.season.rxdownload2.entity.DownloadRecord;

/**
 * 下载中
 */
public class DownloadingFragment extends AppManagerFragment {

    @Override
    protected RecyclerView.Adapter setupAdapter() {
        return new DownloadingAdapter(mPresenter.getRxDownload());
    }

    @Override
    public void init() {
        super.init();
        mPresenter.getDownloadingApps();
    }

    @Override
    public void showDownloading(List<DownloadRecord> downloadRecords) {
        super.showDownloading(downloadRecords);
    }

}
