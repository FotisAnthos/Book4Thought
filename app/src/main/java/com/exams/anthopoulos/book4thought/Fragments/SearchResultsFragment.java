package com.exams.anthopoulos.book4thought.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.exams.anthopoulos.book4thought.BookData;
import com.exams.anthopoulos.book4thought.R;
import com.exams.anthopoulos.book4thought.Utilities.ResultsAdapter;

import java.util.List;

import static com.exams.anthopoulos.book4thought.DataBases.DatabaseHelper.DATABASE_NAME;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.COLUMN_NAME_CANONICAL_LINK;
import static com.exams.anthopoulos.book4thought.DataBases.SavedBooksContract.SavedBookEntry.TABLE_NAME;


public class SearchResultsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    private List<BookData> bookList;
    private RecyclerView.Adapter mAdapter;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public void setSearchResults(List<BookData> searchResults) {this.bookList = searchResults;}

    public static SearchResultsFragment newInstance() {
        return new SearchResultsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        RecyclerView mRecyclerView = rootView.findViewById(R.id.search_recycler_view);
        mAdapter = new ResultsAdapter(getActivity(), bookList, DATABASE_NAME, TABLE_NAME, COLUMN_NAME_CANONICAL_LINK);
        mRecyclerView.setAdapter(mAdapter);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(rootView.getContext());
        mRecyclerView.setLayoutManager(mLayoutManager);

        final SwipeRefreshLayout swipeRefreshLayout = rootView.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // This method performs the actual data-refresh operation.
                // The method calls setRefreshing(false) when it's finished.
                mAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
    }
}
