package com.example.mlk;

public class Book {
//    String uid;
    String title;
    String content;

    public Book(){}

//    public String getUid() {
//        return uid;
//    }
//
//    public void setUid(String uid) {
//        this.uid = uid;
//    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Book(String title, String content) {
//        this.uid = uid;
        this.title = title;
        this.content = content;
    }

    @Override
    public String toString() {
        return "Book{" +
                "uid='"  + '\'' +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                '}';
    }
}
