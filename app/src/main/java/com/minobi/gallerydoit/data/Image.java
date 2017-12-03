
package com.minobi.gallerydoit.data;

import com.google.gson.annotations.SerializedName;

@SuppressWarnings("unused")
public class Image {

    @SerializedName("bigImagePath")
    private String BigImagePath;
    @SerializedName("created")
    private String Created;
    @SerializedName("description")
    private String Description;
    @SerializedName("hashtag")
    private String Hashtag;
    @SerializedName("id")
    private Long Id;
    @SerializedName("parameters")
    private com.minobi.gallerydoit.data.Parameters Parameters;
    @SerializedName("smallImagePath")
    private String SmallImagePath;

    public String getBigImagePath() {
        return BigImagePath;
    }

    public void setBigImagePath(String bigImagePath) {
        BigImagePath = bigImagePath;
    }

    public String getCreated() {
        return Created;
    }

    public void setCreated(String created) {
        Created = created;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getHashtag() {
        return Hashtag;
    }

    public void setHashtag(String hashtag) {
        Hashtag = hashtag;
    }

    public Long getId() {
        return Id;
    }

    public void setId(Long id) {
        Id = id;
    }

    public com.minobi.gallerydoit.data.Parameters getParameters() {
        return Parameters;
    }

    public void setParameters(com.minobi.gallerydoit.data.Parameters parameters) {
        Parameters = parameters;
    }

    public String getSmallImagePath() {
        return SmallImagePath;
    }

    public void setSmallImagePath(String smallImagePath) {
        SmallImagePath = smallImagePath;
    }

}
