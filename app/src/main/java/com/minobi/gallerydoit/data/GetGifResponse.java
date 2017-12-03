
package com.minobi.gallerydoit.data;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class GetGifResponse {

    @SerializedName("gif")
    private String Gif;

    public String getGif() {
        return Gif;
    }

    public void setGif(String gif) {
        Gif = gif;
    }

}
