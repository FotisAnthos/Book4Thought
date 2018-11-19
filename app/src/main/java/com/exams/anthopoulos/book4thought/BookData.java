package com.exams.anthopoulos.book4thought;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BookData implements Parcelable {
    private String previewLink;
    private final String title;
    private final List<String> authors;
    private final String description; //a short description of the book's contents
    private String selfLink; //a google books link to get just this specific book
    private final String canonicalLink;
    private String thumbnailLink;
    private Bitmap thumbnail;

    public BookData(String title, List<String> authors, String description, String selfLink, String canonicalLink, String thumbnailLink, String previewLink) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.selfLink = selfLink;
        this.canonicalLink = canonicalLink;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
    }

    //when the book is already saved
    public BookData(String title, String author, String description, String canonicalLink, Bitmap thumbnail) {
        this.title = title;
        this.authors = new ArrayList<>();
        this.authors.add(author);
        this.description = description;
        this.canonicalLink = canonicalLink;
        this.thumbnail = thumbnail;
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
        dest.writeString(this.previewLink);
        dest.writeString(this.title);
        dest.writeStringList(this.authors);
        dest.writeString(this.description);
        dest.writeString(this.selfLink);
        dest.writeString(this.canonicalLink);
        dest.writeString(this.thumbnailLink);
        dest.writeParcelable(this.thumbnail, flags);
    }

    private BookData(Parcel in) {
        this.previewLink = in.readString();
        this.title = in.readString();
        this.authors = in.createStringArrayList();
        this.description = in.readString();
        this.selfLink = in.readString();
        this.canonicalLink = in.readString();
        this.thumbnailLink = in.readString();
        this.thumbnail = in.readParcelable(Bitmap.class.getClassLoader());
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
