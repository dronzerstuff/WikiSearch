
package com.deepak.moneytap.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Terms {

    @SerializedName("description")
    @Expose
    private List<String> description = null;

    public String getDescription() {
        if(description!=null)
        {
            return description.toString().replace("[", "").replace("]", "");

        }
        return "";
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

}
