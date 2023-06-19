package com.a6706210162.myproducts

import androidx.lifecycle.*
import com.a6706210162.myproducts.data.Product
import com.a6706210162.myproducts.data.ProductDao
import kotlinx.coroutines.launch

class ProductMangementViewModel(private val productDao: ProductDao): ViewModel() {

    val allItems : LiveData<List<Product>> = productDao.getAllItems().asLiveData()

    fun getItemById(id : Int) : LiveData<Product> = productDao.getItemById(id).asLiveData()

    private fun insertItem(product : Product){
        viewModelScope.launch {
            productDao.insert(product)
        }
    }

    fun updateItem(product: Product){ viewModelScope.launch { productDao.update(product) } }

    fun deleteItem(product : Product){ viewModelScope.launch{ productDao.delete(product) } }

    fun isStockAvailable(product: Product) : Boolean{
        return (product.quantityInStock > 0)
    }

    private fun getNewItemEntry(itemName: String, itemPrice: String, itemCount: String): Product {
        return Product(
            itemName = itemName,
            itemPrice = itemPrice.toDouble(),
            quantityInStock = itemCount.toInt()
        )
    }

    fun addNewItem(itemName: String, itemPrice: String, itemCount: String) {
        val newItem = getNewItemEntry(itemName, itemPrice, itemCount)
        insertItem(newItem)
    }

    fun isEntryValid(itemName: String, itemPrice: String, itemCount: String): Boolean {
        if (itemName.isBlank() || itemPrice.isBlank() || itemCount.isBlank()) {
            return false
        }
        return true
    }

}

class InventoryViewModelFactory(private val productDao: ProductDao): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if ( modelClass.isAssignableFrom(ProductMangementViewModel::class.java) ){
            return ProductMangementViewModel(productDao) as T
        }
        throw IllegalArgumentException("ViewModel not found")
    }
}