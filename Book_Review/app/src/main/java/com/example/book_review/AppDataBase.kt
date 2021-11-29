package com.example.book_review

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.book_review.dao.HistoryDao
import com.example.book_review.dao.ReviewDao
import com.example.book_review.model.History
import com.example.book_review.model.Review

@Database(entities = [History::class, Review::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun reviewDao(): ReviewDao
}

fun getAppDatabase(context: Context): AppDatabase {

    val migration_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            database.execSQL("CREATE TABLE 'REVIEW' ('id' INTEGER, 'review' TEXT," + "PRIMARY KEY('id'))")
        }

    }

    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        "BookSearchDB"
    )
        .addMigrations(migration_1_2)
        .build()
}