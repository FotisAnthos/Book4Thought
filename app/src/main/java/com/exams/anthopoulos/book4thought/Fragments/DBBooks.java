package com.exams.anthopoulos.book4thought.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exams.anthopoulos.book4thought.BookData;
import com.exams.anthopoulos.book4thought.DataBases.RetrieveBooksDBOperation;
import com.exams.anthopoulos.book4thought.R;
import com.exams.anthopoulos.book4thought.Utilities.ResultsAdapter;

import java.util.List;


public class DBBooks extends Fragment {

    private List<BookData> bookList;
    private RecyclerView mRecyclerView;
    private ResultsAdapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private static final String TAG = DBBooks.class.getCanonicalName();
    private Context context;

    public DBBooks() {
        // Required empty public constructor
    }

    public void setBookList(List<BookData> bookList) {
        this.bookList = bookList;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        mRecyclerView = rootView.findViewById(R.id.search_recycler_view);
        try {
            mAdapter = new ResultsAdapter(bookList, getActivity());
            mRecyclerView.setAdapter(mAdapter);
            mLayoutManager = new LinearLayoutManager(rootView.getContext());
            mRecyclerView.setLayoutManager(mLayoutManager);
        }catch (NullPointerException npe){
            Log.e(TAG, npe.getMessage());
            Log.e(TAG, "setBookList(List<BookData> bookList) MUST be called before DBBooks is shown");
        }

        final SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                try {
                    RetrieveBooksDBOperation retrieve = new RetrieveBooksDBOperation(getActivity(), new RetrieveBooksDBOperation.AsyncResponse() {
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

        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
