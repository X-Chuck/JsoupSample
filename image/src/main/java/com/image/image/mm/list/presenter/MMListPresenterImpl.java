package com.image.image.mm.list.presenter;

import com.framework.base.PresenterImplCompat;
import com.framework.utils.UIUtils;
import com.image.R;
import com.image.image.mm.list.model.MMListModel;
import com.image.image.mm.list.view.MMListView;
import com.image.manager.ApiConfig;
import com.image.manager.JsoupMMManager;
import com.rxjsoupnetwork.manager.RxJsoupNetWork;

import org.jsoup.nodes.Document;

import java.util.List;

/**
 * by y on 2017/3/26.
 */

public class MMListPresenterImpl extends PresenterImplCompat<List<MMListModel>, MMListView> implements MMListPresenter {
    public MMListPresenterImpl(MMListView view) {
        super(view);
    }

    @Override
    public void netWorkRequest(int type, int page) {
        RxJsoupNetWork
                .getInstance()
                .getApi(
                        RxJsoupNetWork.onSubscribe(String.format(ApiConfig.MM_URL + UIUtils.getStringArray(R.array.mm_array_suffix)[type], page), this),
                        this);
    }

    @Override
    public List<MMListModel> getT(Document document) {
        return JsoupMMManager.get(document).getImageList();
    }
}