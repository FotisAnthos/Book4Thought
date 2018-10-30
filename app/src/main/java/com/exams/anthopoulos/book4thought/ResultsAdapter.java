package com.exams.anthopoulos.book4thought;

import android.support.annotation.NonNull;
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

    public static class ResultsViewHolder extends RecyclerView.ViewHolder {
        //define the View objects
        ImageView thumbnail;
        public TextView title;
        TextView author;
        public LinearLayout container;

        ResultsViewHolder(View itemView) {
            super(itemView);
            //initialize the View objects
            this.thumbnail = itemView.findViewById(R.id.book_thumbnail);
            this.title = itemView.findViewById(R.id.book_title);
            this.author = itemView.findViewById(R.id.book_author);
            this.container = itemView.findViewById(R.id.results_layout);

        }
    }

    ResultsAdapter(List<BookData> bookList) {
        this.bookList = bookList;
    }

    @NonNull
    @Override
    public ResultsAdapter.ResultsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.results_cardview, parent, false);
        return new ResultsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultsViewHolder holder, int position) {
        final BookData bookData = bookList.get(position);
        holder.title.setText(bookData.getTitle());
        holder.author.setText(bookData.getAuthors().get(0));//just the first one for this view

        /*
        ImageView thumbnail = holder.thumbnail;

        if(bookData.getThumbnailLink() != null) {
            DownloadImageTask DIT = new DownloadImageTask(thumbnail, new DownloadImageTask.AsyncResponse() {
                @Override
                public void imageDownloadFinish(Bitmap output) {
                    thumbnail.setImageBitmap(output);
                }
            });
            DIT.execute(bookData.getThumbnailLink());
        }
        */
        if(bookData.getThumbnailLink() != null) {
            Picasso.get().cancelRequest(holder.thumbnail);
            Picasso.get().load(bookData.getThumbnailLink()).into(holder.thumbnail);
        }
        holder.itemView.setTag(bookData);

    }

    // Return the size of your dataSet (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return bookList.size();
    }

}
