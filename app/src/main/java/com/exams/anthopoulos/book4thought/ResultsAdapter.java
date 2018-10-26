package com.exams.anthopoulos.book4thought;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

class ResultsAdapter extends RecyclerView.Adapter<ResultsAdapter.ResultsViewHolder> {

    private List<BookData> bookList;
    private Context context;

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {
        //define the View objects
        public ImageView thumbnail;
        public TextView title;
        public TextView author;
        public TextView description;
        public LinearLayout container;

        public ResultsViewHolder(View itemView) {
            super(itemView);
            //initialize the View objects
            this.thumbnail = itemView.findViewById(R.id.book_thumbnail);
            this.title = itemView.findViewById(R.id.book_title);
            this.author = itemView.findViewById(R.id.book_author);
            this.description = itemView.findViewById(R.id.book_description);
            this.container = itemView.findViewById(R.id.results_layout);
        }
    }

    public ResultsAdapter(List<BookData> bookList, Context context) {
        this.bookList = bookList;
        this.context = context;
    }

    @Override
    public ResultsAdapter.ResultsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_cardView, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ResultsViewHolder holder, int position) {
        final BookData bookData = bookList.get(position);
        holder.title.setText(bookData.getTitle());
        holder.author.setText(bookData.getAuthor());
        holder.description.setText(bookData.getDescription());
        Picasso.get()
                .load(bookData.getThumbnailLink())
                .placeholder(R.drawable.image_placeholder)
                .error(R.drawable.image_placeholder_error)
                .centerCrop()
                .into(holder.thumbnail);
    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookList.size();
    }

}
