package com.vietcombank.models;

/**
 * Simple POJO representing a single search result item.
 */
public class SearchResult {

    private String title;
    private String url;
    private String description;

    public SearchResult() {
    }

    public SearchResult(String title, String url, String description) {
        this.title = title;
        this.url = url;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "SearchResult{title='" + title + "', url='" + url + "', description='" + description + "'}";
    }
}
