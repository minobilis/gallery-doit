
package com.minobi.gallerydoit.data;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class AddImageResponse {

    @SerializedName("bigImage")
    private String BigImage;
    @SerializedName("parameters")
    private com.minobi.gallerydoit.data.Parameters Parameters;
    @SerializedName("smallImage")
    private String SmallImage;

    public String getBigImage() {
        return BigImage;
    }

    public void setBigImage(String bigImage) {
        BigImage = bigImage;
    }

    public com.minobi.gallerydoit.data.Parameters getParameters() {
        return Parameters;
    }

    public void setParameters(com.minobi.gallerydoit.data.Parameters parameters) {
        Parameters = parameters;
    }

    public String getSmallImage() {
        return SmallImage;
    }

    public void setSmallImage(String smallImage) {
        SmallImage = smallImage;
    }

}
