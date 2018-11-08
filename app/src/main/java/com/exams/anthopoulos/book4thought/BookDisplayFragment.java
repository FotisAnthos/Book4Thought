package com.exams.anthopoulos.book4thought;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class BookDisplayFragment extends Fragment {


    private OnFragmentInteractionListener mListener;
    private BookData bookData;

    public BookDisplayFragment() {
        // Required empty public constructor
    }


    public static BookDisplayFragment newInstance() {
        BookDisplayFragment fragment = new BookDisplayFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View rootView = inflater.inflate(R.layout.fragment_book_display, container, false);

        ImageView thumbnail = rootView.findViewById(R.id.display_thumbnail);
        TextView title = rootView.findViewById(R.id.display_book_title);
        TextView authors = rootView.findViewById(R.id.display_book_authors);
        TextView description = rootView.findViewById(R.id.display_book_description);
        Button readBookButton = rootView.findViewById(R.id.display_read_book_button);

        if(bookData.getThumbnailLink() != null) {
            Picasso.get().cancelRequest(thumbnail);
            Picasso.get().load(bookData.getThumbnailLink()).into(thumbnail);
        }

        title.setText(bookData.getTitle());

        StringBuilder tmpAuthors = new StringBuilder(bookData.getAuthors().get(0));

        for(int i=1; i<bookData.getAuthors().size(); i++){
            tmpAuthors.append(", " + bookData.getAuthors().get(i));
        }
        authors.setText(tmpAuthors.toString());

        description.setText(bookData.getDescription());

        readBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.book_display_scroll).scrollTo(0, 0);
                mListener.onFragmentInteraction(bookData);
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

    public void setArguments(BookData bookData) {
        this.bookData = bookData;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(BookData bookData);
    }
}
