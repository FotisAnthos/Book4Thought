package com.exams.anthopoulos.book4thought;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BookData implements Parcelable {
    private final String title;
    private final List<String> authors;
    private final String description; //a short description of the book's contents
    private String selfLink; //a google books link to get just this specific book
    private String previewLink;
    private final String canonicalLink;
    private String thumbnailLink;
    private Bitmap thumbnail;
    private final List<String> categories;
    private int rating;
    private int ratingsCount;
    private String webReaderLink;

    public BookData(String title, List<String> authors, String description, String selfLink, String previewLink, String canonicalLink, String thumbnailLink, List<String> categories, int rating, int ratingsCount, String webReaderLink) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.selfLink = selfLink;
        this.previewLink = previewLink;
        this.canonicalLink = canonicalLink;
        this.thumbnailLink = thumbnailLink;
        this.categories = categories;
        this.rating = rating;
        this.ratingsCount = ratingsCount;
        this.webReaderLink = webReaderLink;
    }

    //when the book is already saved
    public BookData(String title, String author, String description, String canonicalLink, Bitmap thumbnail, String category, int rating, int ratingsCount, String webReaderLink) {
        this.title = title;
        this.authors = new ArrayList<>();
        this.authors.add(author);
        this.description = description;
        this.canonicalLink = canonicalLink;
        this.thumbnail = thumbnail;
        this.categories = new ArrayList<>();
        this.categories.add(category);
        this.rating = rating;
        this.ratingsCount = ratingsCount;
        this.webReaderLink = webReaderLink;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getRating() {
        return rating;
    }

    public int getRatingsCount() {
        return ratingsCount;
    }

    public String getWebReaderLink() {
        return webReaderLink;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getCanonicalLink() {
        return canonicalLink;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public List<String> getAuthors() {
        return authors;
    }

    public Bitmap getThumbnail() {
        return thumbnail;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeStringList(this.authors);
        dest.writeString(this.description);
        dest.writeString(this.selfLink);
        dest.writeString(this.previewLink);
        dest.writeString(this.canonicalLink);
        dest.writeString(this.thumbnailLink);
        dest.writeParcelable(this.thumbnail, flags);
        dest.writeStringList(this.categories);
        dest.writeInt(this.rating);
        dest.writeInt(this.ratingsCount);
        dest.writeString(this.webReaderLink);
    }

    private BookData(Parcel in) {
        this.title = in.readString();
        this.authors = in.createStringArrayList();
        this.description = in.readString();
        this.selfLink = in.readString();
        this.previewLink = in.readString();
        this.canonicalLink = in.readString();
        this.thumbnailLink = in.readString();
        this.thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
        this.categories = in.createStringArrayList();
        this.rating = in.readInt();
        this.ratingsCount = in.readInt();
        this.webReaderLink = in.readString();
    }

    public static final Creator<BookData> CREATOR = new Creator<BookData>() {
        @Override
        public BookData createFromParcel(Parcel source) {
            return new BookData(source);
        }

        @Override
        public BookData[] newArray(int size) {
            return new BookData[size];
        }
    };
}
