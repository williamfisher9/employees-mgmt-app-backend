package com.apps.salaries.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "forms")
public class Form {

    @Id
    private long id;

    @Column(name = "link")
    private String link;

    @Column(name = "header")
    private String header;

    @Column(name = "body")
    private String body;

    @Column(name = "icon")
    private String icon;

    public Form(long id, String link, String header, String body, String icon) {
        this.id = id;
        this.link = link;
        this.header = header;
        this.body = body;
        this.icon = icon;
    }

    public Form() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    @Override
    public String toString() {
        return "Form{" +
                "id=" + id +
                ", link='" + link + '\'' +
                ", header='" + header + '\'' +
                ", body='" + body + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}
