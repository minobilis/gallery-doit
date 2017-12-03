
package com.minobi.gallerydoit.data;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class SignUpResponse {

    @SerializedName("avatar")
    private String avatarUrl;
    @SerializedName("creation_time")
    private String creationTime;
    @SerializedName("token")
    private String authToken;

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(String creationTime) {
        this.creationTime = creationTime;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

}
