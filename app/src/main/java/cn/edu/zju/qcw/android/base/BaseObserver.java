package cn.edu.zju.qcw.android.base;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

/**
 * Created by SQ on 2017/5/1.
 */

public class BaseObserver<T> implements Observer<T> {

    @Override
    public void onSubscribe(Disposable d) {

    }

    @Override
    public void onNext(T value) {

    }

    @Override
    public void onError(Throwable e) {

    }

    @Override
    public void onComplete() {

    }
}
