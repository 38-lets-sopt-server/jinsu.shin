package org.sopt.domain;

public class Post {
    private Long id;
    private String title;
    private String content;
    private String author;
    private String createdAt;
    private boolean isAnonymous;

    public Post(Long id, String title, String content, String author, String createdAt, boolean isAnonymous) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.author = author;
        this.createdAt = createdAt;
        this.isAnonymous = isAnonymous;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getAuthor() { return author; }
    public String getCreatedAt() { return createdAt; }
    public boolean isAnonymous() { return isAnonymous; }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public String getInfo() {
        return "[" + id + "] " + title + " - " + author + " (" + createdAt + ")\n" + content;
    }
}
