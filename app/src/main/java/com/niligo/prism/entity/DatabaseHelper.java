package com.niligo.prism.entity;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "niligoprism.db";
    private static final int DATABASE_VERSION = 1;

    private RuntimeExceptionDao<ProfileEntity, String> profilesRuntimeDao = null;


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
        try {
            Log.i(DatabaseHelper.class.getName(), "onCreate");
            TableUtils.createTable(connectionSource, ProfileEntity.class);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {

    }

    public List<ProfileEntity> getProfileEntities()
    {
        return getProfilesRuntimeDao().queryForAll();
    }

    private RuntimeExceptionDao<ProfileEntity, String> getProfilesRuntimeDao() {
        if (profilesRuntimeDao == null) {
            profilesRuntimeDao = getRuntimeExceptionDao(ProfileEntity.class);
        }
        return profilesRuntimeDao;
    }


    public void addProfile(ProfileEntity profileEntity) {
        getProfilesRuntimeDao().create(profileEntity);
    }

    public void updateProfile(ProfileEntity profileEntity) {
        getProfilesRuntimeDao().update(profileEntity);
    }

    public void deleteProfile(ProfileEntity profileEntity) {
        getProfilesRuntimeDao().delete(profileEntity);
    }

    @Override
    public void close() {
        super.close();
        profilesRuntimeDao = null;
    }


    public void cleanAll(){
        getProfilesRuntimeDao().delete(getProfilesRuntimeDao().queryForAll());

    }
}