package com.exams.anthopoulos.book4thought;

import java.util.List;

public class BookData {
    private String previewLink;
    private String title;
    private List<String> authors;
    private String description; //a short description of the book's contents
    private String selfLink; //a google books link to get just this specific book
    private String canonicalLink;
    private String thumbnailLink;

    public BookData(String title, List<String> authors, String description, String selfLink, String canonicalLink, String thumbnailLink, String previewLink) {
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
}
