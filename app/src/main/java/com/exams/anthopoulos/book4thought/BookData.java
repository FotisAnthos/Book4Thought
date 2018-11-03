package com.exams.anthopoulos.book4thought;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class BookData implements Parcelable {
    private String previewLink;
    private String title;
    private List<String> authors;
    private String description; //a short description of the book's contents
    private String selfLink; //a google books link to get just this specific book
    private String canonicalLink;
    private String thumbnailLink;

    public BookData (String title, List<String> authors, String description, String selfLink, String canonicalLink, String thumbnailLink, String previewLink) {
        this.title = title;
        this.authors = authors;
        this.description = description;
        this.selfLink = selfLink;
        this.canonicalLink = canonicalLink;
        this.thumbnailLink = thumbnailLink;
        this.previewLink = previewLink;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getSelfLink() {
        return selfLink;
    }

    public String getCanonicalLink() {
        return canonicalLink;
    }

    public String getThumbnailLink() {
        return thumbnailLink;
    }

    public String getPreviewLink() {return previewLink;}

    public List<String> getAuthors() {
        return authors;
    }


    //Parcelable Components

    protected BookData(Parcel in) {
        previewLink = in.readString();
        title = in.readString();
        if (in.readByte() == 0x01) {
            authors = new ArrayList<String>();
            in.readList(authors, String.class.getClassLoader());
        } else {
            authors = null;
        }
        description = in.readString();
        selfLink = in.readString();
        canonicalLink = in.readString();
        thumbnailLink = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(previewLink);
        dest.writeString(title);
        if (authors == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(authors);
        }
        dest.writeString(description);
        dest.writeString(selfLink);
        dest.writeString(canonicalLink);
        dest.writeString(thumbnailLink);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BookData> CREATOR = new Parcelable.Creator<BookData>() {
        @Override
        public BookData createFromParcel(Parcel in) {
            return new BookData(in);
        }

        @Override
        public BookData[] newArray(int size) {
            return new BookData[size];
        }
    };
}
