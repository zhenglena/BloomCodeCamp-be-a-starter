package com.hcc.entities;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "assignments")
public class Assignment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "assignment_id")
    private Long id;
    private String status;
    private Integer number;
    @Column(name = "github_url")
    private String githubUrl;
    private String branch;
    @Column(name = "video_url")
    private String reviewVideoUrl;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    private User codeReviewer;

    /**
     * no-args constructor
     */
    public Assignment() {}

    /**
     * All args except id constructor
     * @param status the status of the assignment
     * @param number the assignment number
     * @param githubUrl the url of the assignment
     * @param branch the branch of the assignment
     * @param reviewVideoUrl the url of the video
     * @param user the user
     * @param codeReviewer the reviewer
     */
    public Assignment(String status, Integer number, String githubUrl, String branch, String reviewVideoUrl, User user, User codeReviewer) {
        this.status = status;
        this.number = number;
        this.githubUrl = githubUrl;
        this.branch = branch;
        this.reviewVideoUrl = reviewVideoUrl;
        this.user = user;
        this.codeReviewer = codeReviewer;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getBranch() {
        return branch;
    }

    public void setBranch(String branch) {
        this.branch = branch;
    }

    public String getReviewVideoUrl() {
        return reviewVideoUrl;
    }

    public void setReviewVideoUrl(String reviewVideoUrl) {
        this.reviewVideoUrl = reviewVideoUrl;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getCodeReviewer() {
        return codeReviewer;
    }

    public void setCodeReviewer(User codeReviewer) {
        this.codeReviewer = codeReviewer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Assignment that = (Assignment) o;
        return Objects.equals(getId(), that.getId()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getNumber(), that.getNumber()) && Objects.equals(getGithubUrl(), that.getGithubUrl()) && Objects.equals(getBranch(), that.getBranch()) && Objects.equals(getReviewVideoUrl(), that.getReviewVideoUrl()) && Objects.equals(getUser(), that.getUser()) && Objects.equals(getCodeReviewer(), that.getCodeReviewer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getStatus(), getNumber(), getGithubUrl(), getBranch(), getReviewVideoUrl(), getUser(), getCodeReviewer());
    }
}
