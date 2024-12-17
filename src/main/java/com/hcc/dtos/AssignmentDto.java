package com.hcc.dtos;

import com.hcc.entities.User;
import java.util.Objects;

/**
 * This is a general DTO. This is used with updating Assignments or retrieving Assignments from the database.
 * It only shows relevant information to both Learners and Reviewers so that they can update specific fields.
 * The rest is not shown to the user as they won't be able to edit it anyway.
 */
public class AssignmentDto {
    private String status;
    private String githubUrl;
    private String branch;
    private String reviewVideoUrl;
    private User codeReviewer;

    public AssignmentDto() {}

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentDto that = (AssignmentDto) o;
        return Objects.equals(getStatus(), that.getStatus()) && Objects.equals(getGithubUrl(), that.getGithubUrl()) && Objects.equals(getBranch(), that.getBranch()) && Objects.equals(getReviewVideoUrl(), that.getReviewVideoUrl()) && Objects.equals(getCodeReviewer(), that.getCodeReviewer());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStatus(), getGithubUrl(), getBranch(), getReviewVideoUrl(), getCodeReviewer());
    }

    @Override
    public String toString() {
        return "AssignmentResponseDto{" +
                "status='" + status + '\'' +
                ", githubUrl='" + githubUrl + '\'' +
                ", branch='" + branch + '\'' +
                ", reviewVideoUrl='" + reviewVideoUrl + '\'' +
                ", codeReviewer=" + codeReviewer +
                '}';
    }

}