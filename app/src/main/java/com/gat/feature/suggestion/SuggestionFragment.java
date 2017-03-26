package com.gat.feature.suggestion;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import com.gat.R;
import com.gat.app.fragment.ScreenFragment;
import com.gat.repository.entity.Book;
import java.util.List;
import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by mryit on 3/26/2017.
 */

public class SuggestionFragment extends ScreenFragment<SuggestionScreen, SuggestionPresenter> {

    @BindView(R.id.image_button_search)
    ImageButton textViewTitle;

    @BindView(R.id.recycler_view_most_search)
    RecyclerView mRecyclerViewMostSearch;

    private CompositeDisposable disposables;
    private BookSuggestAdapter mBookSuggestAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_suggestion;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable(
                getPresenter().onResult().subscribe(this::onResult),
                getPresenter().onError().subscribe(this::onError)
        );

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // setup recycler view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerViewMostSearch.setLayoutManager(linearLayoutManager);

        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        getPresenter().suggestMostSearched();
    }

    @OnClick(R.id.image_button_search)
    void onSearchButtonTap() {
        // TODO start search activity
    }

    void onResult (List<Book> list) {
        Toast.makeText(getActivity().getApplicationContext(),
                "Lấy được: " + list.size() + " quyển sách", Toast.LENGTH_SHORT).show();

        // setup adapter
        mBookSuggestAdapter = new BookSuggestAdapter(getActivity(), list);
        mRecyclerViewMostSearch.setAdapter(mBookSuggestAdapter);
    }

    void onError (String message) {
        Toast.makeText(getActivity().getApplicationContext(), message, Toast.LENGTH_LONG).show();
    }

    @Override
    protected SuggestionScreen getDefaultScreen() {
        return SuggestionScreen.instance();
    }

    @Override
    protected Class<SuggestionPresenter> getPresenterClass() {
        return SuggestionPresenter.class;
    }

}
