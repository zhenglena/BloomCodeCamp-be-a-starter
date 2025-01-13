package com.hcc.dtos;

import com.hcc.entities.User;
import com.hcc.enums.AssignmentEnum;
import com.hcc.enums.AssignmentStatusEnum;

import java.util.Objects;

/**
 * This is a general DTO. This is used with updating Assignments or retrieving Assignments from the database.
 * It only shows relevant information to both Learners and Reviewers so that they can update specific fields.
 * The rest is not shown to the user as they won't be able to edit it anyway.
 */
public class AssignmentDto {
    //cannot be edited
    private Integer number;
    private Long id;
    private User user;

    //can be edited
    private String status; //automatically updates
    private String githubUrl; //learner
    private String branch; //learner
    private String reviewVideoUrl; //reviewer
    private User codeReviewer; //reviewer

    public AssignmentDto() {
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public User getCodeReviewer() {
        return codeReviewer;
    }

    public void setCodeReviewer(User codeReviewer) {
        this.codeReviewer = codeReviewer;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentDto that = (AssignmentDto) o;
        return Objects.equals(getNumber(), that.getNumber()) && Objects.equals(getId(), that.getId()) && Objects.equals(getUser(), that.getUser()) && Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getGithubUrl(), that.getGithubUrl()) && Objects.equals(getBranch(), that.getBranch()) && Objects.equals(getReviewVideoUrl(), that.getReviewVideoUrl()) && Objects.equals(getCodeReviewer(), that.getCodeReviewer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getNumber(), getId(), getUser(), getStatus(), getGithubUrl(), getBranch(), getReviewVideoUrl(), getCodeReviewer());
    }

    @Override
    public String toString() {
        return "AssignmentDto{" +
                "number=" + number +
                ", id=" + id +
                ", user=" + user +
                ", status='" + status + '\'' +
                ", githubUrl='" + githubUrl + '\'' +
                ", branch='" + branch + '\'' +
                ", reviewVideoUrl='" + reviewVideoUrl + '\'' +
                ", codeReviewer=" + codeReviewer +
                '}';
    }
}