package com.a6706210162.myproducts.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Product::class], version = 1, exportSchema = false)
abstract class ProductRoomDatabase: RoomDatabase() {

    abstract fun itemDao(): ProductDao

    companion object{

        private var INSTANCE : ProductRoomDatabase? = null

        fun getDatabase(context: Context): ProductRoomDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ProductRoomDatabase::class.java,
                    "item_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance

                instance
            }
        }

    }

}