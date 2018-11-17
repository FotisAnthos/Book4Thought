package com.exams.anthopoulos.book4thought.Fragments;

import android.content.Context;
import android.os.Bundle;
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


public class SearchResultsFragment extends Fragment {

    private OnFragmentInteractionListener mListener;
    //TODO maybe add a button-option for "advanced search"


    private List<BookData> bookList;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    public SearchResultsFragment() {
        // Required empty public constructor
    }

    public void setSearchResults(List<BookData> searchResults) {this.bookList = searchResults;}

    public static SearchResultsFragment newInstance() {
        SearchResultsFragment fragment = new SearchResultsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setRetainInstance(true);
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        mRecyclerView = rootView.findViewById(R.id.search_recycler_view);
        mAdapter = new ResultsAdapter(bookList, getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mLayoutManager = new LinearLayoutManager(rootView.getContext());
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
        void onFragmentInteraction();
    }
}
