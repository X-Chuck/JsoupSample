package com.fiction.fiction.search.detail.widget;

import android.os.Bundle;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;

import com.fiction.R;
import com.fiction.fiction.search.detail.model.SearchDetailModel;
import com.fiction.fiction.search.detail.presenter.SearchDetailPresenter;
import com.fiction.fiction.search.detail.presenter.SearchDetailPresenterImpl;
import com.fiction.fiction.search.detail.view.SearchDetailView;
import com.framework.base.BaseActivity;
import com.framework.utils.UIUtils;
import com.framework.widget.EasyWebView;

/**
 * by y on 2017/1/8.
 */

public class SearchDetailActivity extends BaseActivity
        implements SearchDetailView, OnClickListener, EasyWebView.WebViewLoadListener {
    private static final String URL = "url";

    private Toolbar toolbar;
    private ContentLoadingProgressBar progressBar;
    private SearchDetailPresenter presenter;
    private String onUrl = null;
    private String nextUrl = null;
    private EasyWebView webView;
    private FrameLayout frameLayout;

    public static void getInstance(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(URL, url);
        UIUtils.startActivity(SearchDetailActivity.class, bundle);
    }

    @Override
    protected void initCreate(Bundle savedInstanceState) {
        webView.setLoadListener(this);
        presenter = new SearchDetailPresenterImpl(this);
        Bundle extras = getIntent().getExtras();
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        presenter.startDetail(extras.getString(URL));
    }

    @Override
    protected void initById() {
        webView = getView(R.id.detail_webView);
        frameLayout = getView(R.id.btn_rootView);
        toolbar = getView(R.id.toolbar);
        progressBar = getView(R.id.progress_bar);
        getView(R.id.btn_next).setOnClickListener(this);
        getView(R.id.btn_on).setOnClickListener(this);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_search_detail;
    }

    @Override
    public void netWorkSuccess(SearchDetailModel data) {
        onUrl = data.onPage;
        nextUrl = data.nextPage;
        toolbar.setTitle(data.title);
        webView.post(() -> webView.scrollTo(0, 0));
        webView.loadDataUrl(data.message);
    }

    @Override
    public void netWorkError() {
        UIUtils.snackBar(getView(R.id.rootView), getString(R.string.network_error));
    }

    @Override
    public void showProgress() {
        progressBar.show();
    }

    @Override
    public void hideProgress() {
        progressBar.hide();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_next:
                if (!TextUtils.isEmpty(nextUrl)) {
                    presenter.startDetail(nextUrl);
                } else {
                    UIUtils.toast(UIUtils.getString(R.string.on_empty));
                }
                break;
            case R.id.btn_on:
                if (!TextUtils.isEmpty(onUrl)) {
                    presenter.startDetail(onUrl);
                } else {
                    UIUtils.toast(UIUtils.getString(R.string.on_empty));
                }
                break;
        }
    }

    @Override
    public void loadingSuccess() {
        frameLayout.setVisibility(View.VISIBLE);
    }
}
