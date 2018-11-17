package com.exams.anthopoulos.book4thought.Utilities;


import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exams.anthopoulos.book4thought.BookData;
import com.exams.anthopoulos.book4thought.BookDisplay;
import com.exams.anthopoulos.book4thought.Fragments.AdvancedSearchFragment;
import com.exams.anthopoulos.book4thought.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private static final String TAG = ResultsAdapter.class.getCanonicalName();
    private List<BookData> bookList;
    private FragmentActivity fragmentActivity;

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {
        //define the View objects
        ImageView thumbnail;
        public TextView title;
        TextView author;
        public LinearLayout container;
        Button advancedSearchButton;

        ResultsViewHolder(View itemView) {
            super(itemView);
            //initialize the View objects
            this.thumbnail = itemView.findViewById(R.id.book_thumbnail);
            this.title = itemView.findViewById(R.id.book_title);
            this.author = itemView.findViewById(R.id.book_author);
            this.container = itemView.findViewById(R.id.results_layout);
            this.advancedSearchButton = itemView.findViewById(R.id.advanced_search__button);
        }
    }

    public ResultsAdapter(List<BookData> bookList, FragmentActivity fragmentActivity) {
        this.bookList = bookList;
        this.fragmentActivity = fragmentActivity;
    }

    public void setBookList(List<BookData> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ResultsAdapter.ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType == R.layout.results_cardview){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_cardview, parent, false);
            return new ResultsViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.advanced_search_button, parent, false);
            return new ResultsViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        if(position == bookList.size()) {
            holder.advancedSearchButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AdvancedSearchFragment asf = new AdvancedSearchFragment();
                    FragmentTransaction transaction = fragmentActivity.getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, asf, "searchResultsFragment");
                    transaction.commit();
                }
            });

        }else {
            final BookData bookData = bookList.get(position);
            holder.title.setText(bookData.getTitle());
            holder.author.setText(bookData.getAuthors().get(0));//just the first one for this view

            if (bookData.getThumbnailLink() != null) {
                Picasso.get().cancelRequest(holder.thumbnail);
                Picasso.get().load(bookData.getThumbnailLink()).into(holder.thumbnail);
            }else if (bookData.getThumbnail() != null){
                holder.thumbnail.setImageBitmap(bookData.getThumbnail());
            }
            holder.itemView.setTag(bookData);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent displayBook = new Intent(v.getContext(), BookDisplay.class);
                    BookData bookData = (BookData) v.getTag();
                    displayBook.putExtra("bookData", bookData);
                    v.getContext().startActivity(displayBook);
                }
            });
        }

    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        try{
            return bookList.size()+1; //the number of the results items +1 more view for the "advanced button"
        }catch (NullPointerException npe){
            return 1;//this means that the bookList is empty, so make the size =1 for the advanced button
        }

    }

    @Override
    public int getItemViewType(int position) {
        try{
            return (position == bookList.size()) ? R.layout.advanced_search_button : R.layout.results_cardview;
        }catch (Exception e){
            //if there is an exception the advanced search button is the only thing to be displayed(no data is to be displayed)
            return R.layout.advanced_search_button;
        }
    }




}
