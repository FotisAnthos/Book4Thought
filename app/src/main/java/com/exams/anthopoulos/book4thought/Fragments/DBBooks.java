package com.exams.anthopoulos.book4thought.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exams.anthopoulos.book4thought.BookData;
import com.exams.anthopoulos.book4thought.DataBases.RetrieveBooksDBOperation;
import com.exams.anthopoulos.book4thought.R;
import com.exams.anthopoulos.book4thought.Utilities.ResultsAdapter;
import com.exams.anthopoulos.book4thought.Utilities.SwipeToDeleteCallback;

import java.util.List;

import static com.exams.anthopoulos.book4thought.DataBases.DatabaseHelper.DATABASE_NAME;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_CANONICAL_LINK;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.TABLE_NAME;


public class DBBooks extends Fragment {

    private List<BookData> bookList;
    private RecyclerView mRecyclerView;
    private ResultsAdapter mAdapter;
    private static final String TAG = DBBooks.class.getCanonicalName();
    private SwipeRefreshLayout swipeRefreshLayout;

    public DBBooks() {
        // Required empty public constructor
    }

    public void setBookList(List<BookData> bookList) {
        this.bookList = bookList;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        setUpDisplay(rootView);
        enableSwipeToDeleteAndUndo();

        return rootView;
    }


    private void setUpDisplay(View rootView){
        mRecyclerView = rootView.findViewById(R.id.search_recycler_view);
        try {
            mAdapter = new ResultsAdapter(getActivity(), bookList, DATABASE_NAME, TABLE_NAME, COLUMN_NAME_CANONICAL_LINK);
            mRecyclerView.setAdapter(mAdapter);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }catch (NullPointerException npe){
            Log.e(TAG, npe.getMessage());
            Log.e(TAG, "setBookList(List<BookData> bookList) MUST be called before DBBooks is shown");
        }

        swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                try {
                    RetrieveBooksDBOperation retrieve;
                    retrieve = new RetrieveBooksDBOperation(getActivity(), new RetrieveBooksDBOperation.AsyncResponse() {
                        @Override
                        public void booksRetrieved(List<BookData> savedBooks) {
                            mAdapter.setBookList(savedBooks);
                        }
                    });
                    retrieve.execute();
                }catch (Exception e){
                    //Probably database not found/created
                }
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void enableSwipeToDeleteAndUndo() {
        SwipeToDeleteCallback swipeToDeleteCallback = new SwipeToDeleteCallback(getContext()) {
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {


                final int position = viewHolder.getAdapterPosition();
                final BookData item = mAdapter.getData().get(position);

                mAdapter.removeItem(position);

                Snackbar snackbar = Snackbar
                        .make(swipeRefreshLayout, "Item was removed from the list.", Snackbar.LENGTH_LONG);
                snackbar.setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        mAdapter.restoreItem(item, position);
                        mRecyclerView.scrollToPosition(position);
                    }
                });

                snackbar.setActionTextColor(Color.YELLOW);
                snackbar.show();
            }
        };

        ItemTouchHelper itemTouchhelper = new ItemTouchHelper(swipeToDeleteCallback);
        itemTouchhelper.attachToRecyclerView(mRecyclerView);
    }
}
