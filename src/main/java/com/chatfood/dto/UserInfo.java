package com.chatfood.dto;

import java.util.List;

// ChatRequest에 포함될 사용자 정보 DTO
public class UserInfo {
    private String name;
    private String gender;
    private String ageGroup;
    private String region;
    private String prefCategory;
    private List<String> favCategories;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }
    public String getAgeGroup() { return ageGroup; }
    public void setAgeGroup(String ageGroup) { this.ageGroup = ageGroup; }
    public String getRegion() { return region; }
    public void setRegion(String region) { this.region = region; }
    public String getPrefCategory() { return prefCategory; }
    public void setPrefCategory(String prefCategory) { this.prefCategory = prefCategory; }
    public List<String> getFavCategories() { return favCategories; }
    public void setFavCategories(List<String> favCategories) { this.favCategories = favCategories; }
}
