package com.hcc.dtos;

import java.util.Objects;

public class AssignmentCreateDto {
    private String githubUrl;
    private String branch;
    private Integer number;

    public AssignmentCreateDto() {
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

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AssignmentCreateDto that = (AssignmentCreateDto) o;
        return Objects.equals(getGithubUrl(), that.getGithubUrl()) && Objects.equals(getBranch(), that.getBranch()) && Objects.equals(getNumber(), that.getNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGithubUrl(), getBranch(), getNumber());
    }

    @Override
    public String toString() {
        return "AssignmentCreateDto{" +
                "githubUrl='" + githubUrl + '\'' +
                ", branch='" + branch + '\'' +
                ", number=" + number +
                '}';
    }

//    public static class Builder {
//        private String githubUrl;
//        private String branch;
//        private Integer number;
//
//        public Builder() {
//        }
//
//        public Builder withGithubUrl(String githubUrl) {
//            this.githubUrl = githubUrl;
//            return this;
//        }
//
//        public Builder withBranch(String branch) {
//            this.branch = branch;
//            return this;
//        }
//
//        public Builder withNumber(Integer number) {
//            this.number = number;
//            return this;
//        }
//
//        public AssignmentCreateDto build() {
//            return new AssignmentCreateDto(this);
//        }
//    }
}
