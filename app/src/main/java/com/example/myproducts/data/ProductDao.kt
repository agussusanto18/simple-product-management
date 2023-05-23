package com.example.myproducts.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Query("SELECT * FROM items ORDER BY name ASC")
    fun getAllItems() : Flow<List<Product>>

    @Query("SELECT * FROM items WHERE id = :id")
    fun getItemById(id: Int) : Flow<Product>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(product: Product)

    @Update
    suspend fun update(product: Product)

    @Delete
    suspend fun delete(product: Product)

}