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
//import com.gat.feature.suggestion.viewholder.BookSuggestAdapter;

/**
 * Created by mryit on 3/26/2017.
 */

public class SuggestionFragment extends ScreenFragment<SuggestionScreen, SuggestionPresenter> {

    @BindView(R.id.image_button_search)
    ImageButton textViewTitle;

    @BindView(R.id.recycler_view_most_borrowing)
    RecyclerView mRecyclerViewMostBorrowing;

    @BindView(R.id.recycler_view_suggest_books)
    RecyclerView mRecyclerViewSuggestBooks;

    private CompositeDisposable disposables;
    private BookSuggestAdapter mMostBorrowingAdapter;
    private BookSuggestAdapter mBookSuggestAdapter;

    @Override
    protected int getLayoutResource() {
        return R.layout.fragment_suggestion;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        disposables = new CompositeDisposable(
                getPresenter().onTopBorrowingSuccess().subscribe(this::onTopBorrowingSuccess),
                getPresenter().onBookSuggestSuccess().subscribe(this::onSuggestBooksSuccess),
                getPresenter().onError().subscribe(this::onError)
        );
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // setup recycler view linear
        mRecyclerViewMostBorrowing.setHasFixedSize(true);
        mRecyclerViewMostBorrowing.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewMostBorrowing.setNestedScrollingEnabled(false);

        mRecyclerViewSuggestBooks.setHasFixedSize(true);
        mRecyclerViewSuggestBooks.setLayoutManager(
                new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerViewSuggestBooks.setNestedScrollingEnabled(false);


        getPresenter().suggestMostBorrowing();
        getPresenter().suggestBooks();

        return view;
    }


    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @OnClick(R.id.image_button_search)
    void onSearchButtonTap() {
        // TODO start search activity
    }

    void onTopBorrowingSuccess (List<Book> list) {
        // setup adapter
        mMostBorrowingAdapter = new BookSuggestAdapter(getActivity(), list);
        mRecyclerViewMostBorrowing.setAdapter(mMostBorrowingAdapter);
    }


    void onSuggestBooksSuccess (List<Book> list) {
        // setup adapter
        mBookSuggestAdapter = new BookSuggestAdapter(getActivity(), list);
        mRecyclerViewSuggestBooks.setAdapter(mBookSuggestAdapter);
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
