package com.haoruigang.cniao5play.ui.fragment;

import android.content.Intent;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.listener.OnItemClickListener;
import com.haoruigang.cniao5play.R;
import com.haoruigang.cniao5play.bean.AppInfoBean;
import com.haoruigang.cniao5play.bean.PageBean;
import com.haoruigang.cniao5play.di.component.AppComponent;
import com.haoruigang.cniao5play.di.component.DaggerAppInfoComponent;
import com.haoruigang.cniao5play.di.module.AppInfoModule;
import com.haoruigang.cniao5play.presenter.AppInfoPresenter;
import com.haoruigang.cniao5play.presenter.contract.AppInfoContract;
import com.haoruigang.cniao5play.ui.activity.AppDetailActivity;
import com.haoruigang.cniao5play.ui.adapter.AppInfoAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import zlc.season.rxdownload2.RxDownload;

/**
 * 排行/游戏 共用基类
 */
public abstract class BaseAppInfoFragment extends ProgressFragment<AppInfoPresenter>
        implements AppInfoContract.AppInfoView, BaseQuickAdapter.RequestLoadMoreListener {

    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;

    @Inject
    RxDownload mRxDownload;

    protected AppInfoAdapter mAdapter;

    int page = 0;

    @Override
    public int setLayout() {
        return R.layout.template_recycler_view;
    }

    @Override
    public void init() {
        mPresenter.requestData(type(), page);
        initRecyclerView();
    }

    abstract int type();

    abstract AppInfoAdapter buildAdapter();

    @Override
    public void setupActivityComponent(AppComponent appComponent) {
        DaggerAppInfoComponent.builder()
                .appComponent(appComponent)
                .appInfoModule(new AppInfoModule(this))
                .build().inject(this);

    }

    protected void initRecyclerView() {
        //为RecyClerView设置布局管理器
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        //为RecyClerView设置分割线(这个DividerItemDecoration可以自定义)
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),
                DividerItemDecoration.VERTICAL));
        //动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        mAdapter = buildAdapter();
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(this, recyclerView);// 上拉刷新
        recyclerView.addOnItemTouchListener(new OnItemClickListener() {
            @Override
            public void onSimpleItemClick(BaseQuickAdapter adapter, View view, int position) {
                AppInfoBean appInfo = mAdapter.getItem(position);
                mApplication.setView(view);
                startActivity(new Intent(getActivity(), AppDetailActivity.class)
                        .putExtra("appInfo", appInfo));
            }
        });
    }

    @Override
    public void showResult(PageBean<AppInfoBean> appInfoBean) {
        mAdapter.addData(appInfoBean.getDatas());
        if (appInfoBean.isHasMore()) {
            page++;
        }
        mAdapter.setEnableLoadMore(appInfoBean.isHasMore());// 是否开启加载
    }

    @Override
    public void onLoadMoreComplete() {
        mAdapter.loadMoreComplete();// 加载完成
    }

    @Override
    public void onLoadMoreRequested() {
        mPresenter.requestData(type(), page);
    }

    @Override
    public void onEmptyViewClick() {
        mPresenter.requestData(type(), page);
    }
}
