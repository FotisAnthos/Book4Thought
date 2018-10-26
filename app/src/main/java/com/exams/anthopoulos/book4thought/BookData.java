package com.exams.anthopoulos.book4thought;

public class BookData {
    private String title;
    private String author;
    private String description; //a short description of the book's contents
    private String selfLink; //a google books link to get just this specific book
    private String canonicalLink;
    private String thumbnailLink;

    public BookData(String title, String author, String description, String selfLink, String canonicalLink, String thumbnailLink) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.selfLink = selfLink;
        this.canonicalLink = canonicalLink;
        this.thumbnailLink = thumbnailLink;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
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
}
