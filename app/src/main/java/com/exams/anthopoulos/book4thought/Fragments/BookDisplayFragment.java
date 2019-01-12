package com.exams.anthopoulos.book4thought.Fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.exams.anthopoulos.book4thought.BookData;
import com.exams.anthopoulos.book4thought.DataBases.SaveBookDBOperation;
import com.exams.anthopoulos.book4thought.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class BookDisplayFragment extends Fragment {
    private static final String TAG = "BookDisplayFragmentTag";

    private OnFragmentInteractionListener mListener;
    private BookData bookData;
    private View rootView;
    private ImageView thumbnailView;

    public BookDisplayFragment() {
        // Required empty public constructor
    }


    public static BookDisplayFragment newInstance() {
        return new BookDisplayFragment();
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
        rootView = inflater.inflate(R.layout.fragment_book_display, container, false);

        thumbnailView = rootView.findViewById(R.id.display_thumbnail);
        TextView title = rootView.findViewById(R.id.display_book_title);
        TextView authors = rootView.findViewById(R.id.display_book_authors);
        TextView description = rootView.findViewById(R.id.display_book_description);
        Button openInGooglePlayBookButton = rootView.findViewById(R.id.open_gplay_book_button);
        Button readBookButton = rootView.findViewById(R.id.read_book_button);
        Button suggestionsButton = rootView.findViewById(R.id.suggestions_button);
        RatingBar ratingBar = rootView.findViewById(R.id.ratingBar);
        ImageButton bookmarkButton = rootView.findViewById(R.id.save_button);

        if (bookData.getThumbnailLink() != null) {
            Picasso.get().cancelRequest(thumbnailView);
            Picasso.get().load(bookData.getThumbnailLink()).into(thumbnailView);
        } else if (bookData.getThumbnail() != null) {
            thumbnailView.setImageBitmap(bookData.getThumbnail());
        }

        title.setText(bookData.getTitle());

        if(bookData.getAuthors().size()>0){
            StringBuilder tmpAuthors = new StringBuilder(bookData.getAuthors().get(0));

            for (int i = 1; i < bookData.getAuthors().size(); i++) {
                tmpAuthors.append(", ").append(bookData.getAuthors().get(i));
            }
            authors.setText(tmpAuthors.toString());
        }

        ratingBar.setRating(bookData.getRating() + 1);

        bookmarkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = ((BitmapDrawable) thumbnailView.getDrawable()).getBitmap();
                Handler handler = new Handler();
                SaveBookDBOperation save = new SaveBookDBOperation(getActivity(), bookData, bitmap);
                handler.post(save);
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            description.setText(Html.fromHtml(bookData.getDescription(), Html.FROM_HTML_MODE_COMPACT));
        } else {
            description.setText(Html.fromHtml(bookData.getDescription()));
        }

        openInGooglePlayBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.book_display_scroll).scrollTo(0, 0);
                mListener.onFragmentInteraction(bookData.getTitle(), bookData.getCanonicalLink());
            }
        });

        readBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rootView.findViewById(R.id.book_display_scroll).scrollTo(0, 0);
                mListener.onFragmentInteraction(bookData.getTitle(), bookData.getWebReaderLink());
            }
        });

        suggestionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSuggestions();
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
        void onFragmentInteraction(String bookTitle, String canonicalLink);
    }

    private void showSuggestions() {
        /*
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());
        StringBuilder urlString = new StringBuilder();
        try {
            String baseUrl = getString(R.string.google_Books_API);
            //https://www.googleapis.com/books/v1/volumes/recommended?key={YOUR_API_KEY}
            //Base url
            urlString.append(baseUrl);
            urlString.append("volumes/recommended?access_token=");

            Log.e(TAG, account.getIdToken());
            Log.e(TAG, account.getId());
            Log.e(TAG, account.getAccount().toString());
            if(account.isExpired()){
                Log.e(TAG, "Account Expired");
            } else{
                Log.e(TAG, "Account Valid");
            }
            try{
                urlString.append(account.getIdToken());
            }catch (Exception e){
                Log.e(TAG, e.getMessage());
            }

            Log.i(TAG, urlString.toString());
        } catch (Exception e) {
            //Probably database not found/created
        }

        RequestTask request = new RequestTask(urlString.toString(), new RequestTask.AsyncResponse() {
            @Override
            public void searchFinish(JSONObject output) {
                BookDataGather gather = new BookDataGather(output, 5);
                booksReady(gather.dataGather());
            }
        });
        request.execute();
        */

    }

    private void booksReady(List<BookData> savedBooks){
        DBBooks books = new DBBooks();
        books.setBookList(savedBooks);
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();

        for (Fragment fragment:getActivity().getSupportFragmentManager().getFragments()) {
            getActivity().getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
        transaction.add(R.id.fragment_container, books, "recommended_books");
        transaction.commit();
    }
}

