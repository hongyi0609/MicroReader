package name.caiyao.microreader.presenter.impl;

import name.caiyao.microreader.api.zhihu.ZhihuRequest;
import name.caiyao.microreader.bean.UpdateItem;
import name.caiyao.microreader.presenter.IMainPresenter;
import name.caiyao.microreader.ui.iView.IMain;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 蔡小木 on 2016/4/26 0026.
 */
public class MainPresenterImpl extends BasePresenterImpl implements IMainPresenter {

    private IMain mIMain;

    public MainPresenterImpl(IMain main) {
        if (main == null)
            throw new IllegalArgumentException("main must not be null");
        mIMain = main;
    }

    @Override
    public void checkUpdate() {
        Subscription s = ZhihuRequest.getZhihuApi().getUpdateInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UpdateItem>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onNext(final UpdateItem updateItem) {
                        mIMain.showUpdate(updateItem);
                    }
                });
        addSubscription(s);
    }
}
