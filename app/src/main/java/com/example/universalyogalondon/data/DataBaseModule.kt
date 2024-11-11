package com.example.universalyogalondon.data

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataBaseModule {

    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context
    ) = Room.databaseBuilder(context, YogaDB::class.java, "yoga_db").fallbackToDestructiveMigration().build()


    @Singleton
    @Provides
    fun provideClassDao(db: YogaDB) = db.classDao()

    @Singleton
    @Provides
    fun provideCourseDao(db: YogaDB) = db.courseDao()
}