package com.haoruigang.cniao5play.common.rx;

import com.haoruigang.cniao5play.bean.BaseBean;
import com.haoruigang.cniao5play.common.exception.ApiException;

import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * 2.统一错误处理
 */
public class RxHttpResponseCompat {

    public static <T> ObservableTransformer<BaseBean<T>, T> compatResult() {
        return upstream -> {
            return upstream.flatMap((Function<BaseBean<T>, ObservableSource<T>>) tBaseBean -> {
                if (tBaseBean.success()) {
                    return Observable.create(emitter -> {
                        try {
                            emitter.onNext(tBaseBean.getData());
                            emitter.onComplete();
                        } catch (Exception e) {
                            emitter.onError(e);
                        }
                    });
                } else {
                    return Observable.error(new ApiException(tBaseBean.getStatus(), tBaseBean.getMessage()));
                }
            }).observeOn(AndroidSchedulers.mainThread())//要执行的请求切换成主线程
                    .subscribeOn(Schedulers.io());//请求的网络放入线程
        };
    }

}
