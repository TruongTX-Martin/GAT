package com.gat.feature.register.update.category;

import android.app.ProgressDialog;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.res.ResourcesCompat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.gat.R;
import com.gat.app.activity.ScreenActivity;
import com.gat.common.util.ClientUtils;
import com.gat.common.util.Strings;
import com.gat.common.util.Views;
import com.gat.data.response.ResponseData;
import com.gat.data.response.ServerResponse;
import com.gat.data.share.SharedData;
import com.gat.feature.main.MainActivity;
import com.gat.feature.main.MainScreen;
import com.gat.feature.register.RegisterPresenter;
import com.gat.feature.register.RegisterScreen;
import com.gat.feature.register.update.location.AddLocationActivity;
import com.gat.feature.register.update.location.AddLocationScreen;
import com.gat.feature.search.SearchActivity;
import com.gat.feature.search.SearchScreen;
import com.gat.repository.entity.BookCategory;
import com.gat.repository.entity.InterestCategory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.disposables.CompositeDisposable;

/**
 * Created by ducbtsn on 3/5/17.
 */

public class AddCategoryActivity extends ScreenActivity<AddCategoryScreen, AddCategoryPresenter> {
    private final int MAX_CATE = 15;

    private final String FAVOR_KEY = "FavouriteIndex";

    @BindView(R.id.gridview)
    GridView gridView;

    @BindView(R.id.btn_add_category)
    Button addCateBtn;

    @BindView(R.id.imgBack)
    ImageView imgBack;

    @BindView(R.id.imgSave)
    ImageView imgSave;

    @BindView(R.id.txtTitle)
    TextView txtTitle;

    @BindView(R.id.layoutMenutop)
    RelativeLayout headerLayout;

    private BookCategory bookCategories[];
    private ProgressDialog progressDialog;
    private CompositeDisposable disposables;

    @Override
    protected int getLayoutResource() {
        return R.layout.register_category_activity;
    }

    // TODO refine layout, select action, save button
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        headerLayout.setBackgroundColor(ResourcesCompat.getColor(getResources(), R.color.background_header_blue, null));
        txtTitle.setText(getString(R.string.register_category_header));
        txtTitle.setAllCaps(true);
        txtTitle.setTextColor(ResourcesCompat.getColor(getResources(), R.color.colorWhite, null));
        imgBack.setImageResource(R.drawable.narrow_back_black);
        imgSave.setVisibility(View.GONE);

        progressDialog =  new ProgressDialog(this);
        disposables = new CompositeDisposable(
                getPresenter().updateResult().subscribe(this::onUpdateSuccess),
                getPresenter().onError().subscribe(this::onUpdateError)
        );

        List<InterestCategory> savedCategoryList = getScreen().categories();
        // Initialize category list
        bookCategories = new BookCategory[MAX_CATE];
        Resources res = getResources();
        TypedArray icons = res.obtainTypedArray(R.array.category_cover);
        TypedArray names = res.obtainTypedArray(R.array.category_name);
        TypedArray categoryId = res.obtainTypedArray(R.array.category_id);
        for (int i = 0; i < MAX_CATE; i++) {
            boolean isFavor = false;
            if (savedCategoryList != null && savedCategoryList.size() > 0) {
                for (Iterator<InterestCategory> iterator = savedCategoryList.iterator(); iterator.hasNext();) {
                    if (iterator.next().getCategoryId() == categoryId.getInt(i, 0)) {
                        isFavor = true;
                        break;
                    }
                }
            }
            bookCategories[i] = BookCategory.instance(names.getString(i), icons.getResourceId(i, 0), isFavor, categoryId.getInt(i, 0));
        }

        CategoryAdapter categoryAdapter = new CategoryAdapter(this, bookCategories);
        gridView.setAdapter(categoryAdapter);

        // handling for click event
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id) {
                BookCategory category = bookCategories[position];
                bookCategories[position] = BookCategory.instance(category.name(), category.coverId(), category.favor() ? false : true, category.categoryId());

                // This tells the GridView to redraw itself
                // in turn calling your BooksAdapter's getView method again for each cell
                categoryAdapter.notifyDataSetChanged();
            }
        });

        // Update category list to server
        addCateBtn.setOnClickListener(view -> {
            List<Integer> listCate = new ArrayList<Integer>();
            for (int i = 0; i < MAX_CATE; i++) {
                if (bookCategories[i].favor()) {
                    listCate.add(bookCategories[i].categoryId());
                }
            }
            if (listCate.isEmpty()) {
                // Error
                ClientUtils.showErrorDialog(getString(R.string.titleError), getString(R.string.category_empty), this);
                return;
            }
            if (getScreen().requestFrom() == AddCategoryScreen.FROM.LOGIN) {
                onUpdating(true);
                getPresenter().setCategories(listCate);
            } else {
                // Store data temporary
                SharedData.getInstance().setCategoryList(listCate);
                finish();
            }
        });

        imgBack.setOnClickListener(v -> {
            if (getScreen().requestFrom() == AddCategoryScreen.FROM.LOGIN) {
                start(getApplicationContext(), AddLocationActivity.class, AddLocationScreen.instance());
            }
            this.finish();
        });

        categoryAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        disposables.dispose();
        super.onDestroy();
    }

    @Override
    protected Class<AddCategoryPresenter> getPresenterClass() {
        return AddCategoryPresenter.class;
    }

    @Override
    protected AddCategoryScreen getDefaultScreen() {
        return AddCategoryScreen.instance();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // construct a list of books you've favorited
        final ArrayList<Integer> favorCategory = new ArrayList<>();
        for (int i = 0; i < MAX_CATE; i++) {
            if (bookCategories[i].favor()) {
                favorCategory.add(bookCategories[i].categoryId());
            }
        }

        // save that list to outState for later
        outState.putIntegerArrayList(FAVOR_KEY, favorCategory);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get our previously saved list of favorited books
        final ArrayList<Integer> favorCategory =
                savedInstanceState.getIntegerArrayList(FAVOR_KEY);

        // look at all of your books and figure out which are the favorites
        for (Integer i : favorCategory) {
            BookCategory category = bookCategories[i];
            bookCategories[i] = BookCategory.instance(category.name(), category.coverId(), true, category.categoryId());
        }
    }

    private void onUpdating(boolean enter) {
        if (enter) {
            progressDialog.setIndeterminate(true);
            progressDialog.setMessage(getString(R.string.updating));
            progressDialog.show();
        } else {
            progressDialog.dismiss();
        }
    }

    private void onUpdateError(String error) {
        onUpdating(false);
        Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
    }

    private void onUpdateSuccess(ServerResponse<ResponseData> response) {
        onUpdating(false);
        start(this, MainActivity.class, MainScreen.instance());
        finish();
    }
}
