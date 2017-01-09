package com.niligo.prism.entity;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Created by mahdi on 9/25/16.
 */

@DatabaseTable(tableName = "profile")
public class ProfileEntity {


    @DatabaseField(generatedId = true)
    private int id;
    @DatabaseField
    private String profile_json;


    public ProfileEntity() {
        //needed for ormlite
    }

    public ProfileEntity(String profile_json) {
        this.profile_json = profile_json;
    }

    public int getId() {
        return id;
    }

    public String getProfile_json() {
        return profile_json;
    }

    public void setProfile_json(String profile_json) {
        this.profile_json = profile_json;
    }
}
