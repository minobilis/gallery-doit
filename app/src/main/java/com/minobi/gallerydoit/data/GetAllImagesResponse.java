
package com.minobi.gallerydoit.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@SuppressWarnings("unused")
public class GetAllImagesResponse {

    @SerializedName("images")
    private List<Image> Images;

    public List<Image> getImages() {
        return Images;
    }

    public void setImages(List<Image> images) {
        Images = images;
    }

}
