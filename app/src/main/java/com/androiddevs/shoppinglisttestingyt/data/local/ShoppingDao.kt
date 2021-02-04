package com.androiddevs.shoppinglisttestingyt.data.local

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ShoppingDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertShoppingItem(shoppingItem: ShoppingItem)

    @Delete
    suspend fun deleteShoppingItem(shoppingItem: ShoppingItem)

    /**
     * Since below is returning a live data we dont need suspend otherwise it wont work with room.
     * Since live data is already asynchronous with room.
     */
    @Query("SELECT * FROM shopping_items")
    fun observeAllShoppingItems(): LiveData<List<ShoppingItem>>

    /**
     * Since below is returning a live data we dont need suspend otherwise it wont work with room.
     * Since live data is already asynchronous with room.
     * Whenever you return a live data with room you dont want to add suspend in return type.
     */
    @Query("SELECT SUM(price * amount) FROM shopping_items")
    fun observeTotalPrice(): LiveData<Float>
}