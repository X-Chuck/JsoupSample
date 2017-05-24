package com.movie.ui.fragment;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.text.Html;

import com.framework.base.BaseFragment;
import com.framework.utils.ApkUtils;
import com.framework.utils.ImageLoaderUtils;
import com.framework.utils.UIUtils;
import com.framework.widget.LoadMoreRecyclerView;
import com.movie.R;
import com.movie.mvp.model.MovieModel;
import com.movie.mvp.presenter.PiaoHuaListPresenterImpl;
import com.movie.mvp.presenter.PresenterManager;
import com.movie.mvp.view.ViewManager;
import com.movie.ui.activity.PiaoHuaDetailActivity;
import com.xadapter.adapter.XRecyclerViewAdapter;

import java.util.List;

/**
 * by y on 2017/4/18
 */

public class PiaoHuaListFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener,
        LoadMoreRecyclerView.LoadMoreListener,
        ViewManager.PiaoHuaListView {

    private int page = 1;

    private SwipeRefreshLayout swipeRefreshLayout;
    private LoadMoreRecyclerView recyclerView;
    private PresenterManager.PiaoHuaListPresenter presenter;
    private XRecyclerViewAdapter<MovieModel> mAdapter;

    public static PiaoHuaListFragment newInstance(int position) {
        PiaoHuaListFragment fragment = new PiaoHuaListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(FRAGMENT_INDEX, position);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Override
    protected void initBundle() {
        super.initBundle();
        tabPosition = bundle.getInt(FRAGMENT_INDEX);
    }

    @Override
    protected void initById() {
        swipeRefreshLayout = getView(R.id.refresh_layout);
        recyclerView = getView(R.id.recyclerView);
    }

    @Override
    protected void initActivityCreated() {
        if (!isPrepared || !isVisible || isLoad) {
            return;
        }

        presenter = new PiaoHuaListPresenterImpl(this);

        mAdapter = new XRecyclerViewAdapter<>();

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        recyclerView.setLoadingMore(this);
        recyclerView.setAdapter(
                mAdapter.setLayoutId(R.layout.item_piao_hua)
                        .onXBind((holder, position, movieModel) -> {
                            holder.setTextView(R.id.piaohua_item_tv, Html.fromHtml(movieModel.title));
                            ImageLoaderUtils.display(holder.getImageView(R.id.piaohua_item_iv), movieModel.url);
                        })
                        .setOnItemClickListener((view, position, info) -> {
                            if (ApkUtils.getXLIntent() != null) {
                                PiaoHuaDetailActivity.startIntent(info.detailUrl);
                            } else {
                                UIUtils.snackBar(getActivity().findViewById(R.id.coordinatorLayout), UIUtils.getString(R.string.xl));
                            }
                        })
        );

        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.post(this::onRefresh);

        setLoad();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_piao_hua;
    }

    @Override
    public void onRefresh() {
        page = 1;
        presenter.netWorkRequest(tabPosition, page);
    }

    @Override
    public void onLoadMore() {
        if (swipeRefreshLayout.isRefreshing()) {
            return;
        }
        ++page;
        presenter.netWorkRequest(tabPosition, page);
    }

    @Override
    public void netWorkSuccess(List<MovieModel> data) {
        if (page == 1) {
            mAdapter.removeAll();
        }
        mAdapter.addAllData(data);
    }

    @Override
    public void netWorkError() {
        if (getActivity() != null)
            UIUtils.snackBar(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.network_error));
    }

    @Override
    public void showProgress() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        if (swipeRefreshLayout != null)
            swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void noMore() {
        if (getActivity() != null)
            UIUtils.snackBar(getActivity().findViewById(R.id.coordinatorLayout), getString(R.string.data_empty));
    }
}
