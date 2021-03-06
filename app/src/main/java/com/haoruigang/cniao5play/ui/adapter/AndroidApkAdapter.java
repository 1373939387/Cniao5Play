package com.haoruigang.cniao5play.ui.adapter;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.haoruigang.cniao5play.R;
import com.haoruigang.cniao5play.common.apkparset.AndroidApk;
import com.haoruigang.cniao5play.common.rx.RxSchedulers;
import com.haoruigang.cniao5play.common.util.AppUtils;
import com.haoruigang.cniao5play.common.util.FileUtils;
import com.haoruigang.cniao5play.common.util.PackageUtils;
import com.haoruigang.cniao5play.ui.widget.downloadbutton.DownloadProgressButton;
import com.jakewharton.rxbinding3.view.RxView;

import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.functions.Consumer;


/**
 * 菜鸟窝http://www.cniao5.com 一个高端的互联网技能学习平台
 *
 * @author Ivan
 * @version V1.0
 * @Package com.cniao5.cniao5market.ui.adapter
 * @Description: ${TODO}(用一句话描述该文件做什么)
 * @date
 */

public class AndroidApkAdapter extends BaseQuickAdapter<AndroidApk, BaseViewHolder> {


    public static final int FLAG_APK = 0;
    public static final int FLAG_APP = 1;

    private int flag;

    public AndroidApkAdapter(int flag) {
        super(R.layout.template_appinfo);
        this.flag = flag;
    }

    @Override
    protected void convert(BaseViewHolder helper, final AndroidApk item) {

        helper.getView(R.id.tv_position).setVisibility(View.GONE);
        helper.getView(R.id.tv_brief).setVisibility(View.GONE);
        helper.setText(R.id.tv_app_name, item.getAppName());

        helper.setImageDrawable(R.id.img_icon, item.getDrawable());

        helper.addOnClickListener(R.id.btn_download_progress);

        final DownloadProgressButton btn = helper.getView(R.id.btn_download_progress);
        final TextView txtStatus = helper.getView(R.id.tv_catenory);


        if (flag == FLAG_APK) {

            btn.setTag(R.id.tag_package_name, item.getPackageName());
            btn.setText("删除");

            RxView.clicks(btn).subscribe((Consumer<Object>) o -> {

                if (btn.getTag(R.id.tag_package_name).toString().equals(item.getPackageName())) {
                    Object obj = btn.getTag();
                    if (obj == null) {
                        PackageUtils.install(mContext, item.getApkPath());
                    } else {
                        boolean isInstall = (boolean) obj;
                        if (isInstall) {
                            if (deleteApk(item)) {
                                handle_Success(helper);
                            }
                        } else {
                            int install = PackageUtils.install(mContext, item.getApkPath());
                            if (install == PackageUtils.INSTALL_SUCCEEDED) {
                                notifyDataSetChanged();
                            }
                        }
                    }
                }
            }).isDisposed();
            isInstalled(mContext, item.getPackageName()).subscribe(aBoolean -> {
                btn.setTag(aBoolean);
                if (aBoolean) {
                    txtStatus.setText("已安装");
                    btn.setText("删除");
                } else {
                    txtStatus.setText("等待安装");
                    btn.setText("安装");
                }
            }).isDisposed();

        } else if (flag == FLAG_APP) {

            btn.setText("卸载");
            RxView.clicks(btn).subscribe((Consumer<Object>) o -> {
                if (AppUtils.uninstallApk(mContext, item.getPackageName())) {
                    handle_Success(helper);
                }
            }).isDisposed();

            txtStatus.setText(String.format("v%s\t\t%s", item.getAppVersionName(), (item.isSystem() ? "内置" : "第三方"))); // size 加进来
        }

    }

    private void handle_Success(BaseViewHolder helper) {
        getData().remove(helper.getAdapterPosition());
        notifyItemRemoved(helper.getAdapterPosition());
        notifyItemRangeChanged(helper.getAdapterPosition(),
                getData().size() - helper.getAdapterPosition());
    }


    private boolean deleteApk(AndroidApk item) {
        // 1. 删除下载记录
        // 2. 删除文件
        return FileUtils.deleteFile(item.getApkPath());
    }


    public Observable<Boolean> isInstalled(final Context context, final String packageName) {

        return Observable.create((ObservableOnSubscribe<Boolean>) e ->
                e.onNext(AppUtils.isInstalled(context, packageName))).compose(RxSchedulers.io_main());

    }


}
