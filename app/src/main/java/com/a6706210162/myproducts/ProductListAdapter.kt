package com.a6706210162.myproducts

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.a6706210162.myproducts.databinding.ProductListItemBinding
import com.a6706210162.myproducts.data.Product

class ProductListAdapter(private val listener: ItemClickListener) : ListAdapter<Product, ProductListAdapter.ItemViewHolder>(DiffCallBack) {

    companion object DiffCallBack : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldProduct: Product, newProduct: Product): Boolean {
            return ( oldProduct.id == newProduct.id )
        }

        override fun areContentsTheSame(oldProduct: Product, newProduct: Product): Boolean {
            return ( oldProduct == newProduct )
        }
    }

    inner class ItemViewHolder(val bind : ProductListItemBinding) : RecyclerView.ViewHolder(bind.root){
        fun bindValues(product: Product){
            with(bind) {
                itemName.text = product.itemName
                itemPrice.text = product.itemPrice.toString()
                itemQuantity.text = product.quantityInStock.toString()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            ProductListItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val current = getItem(position)
        holder.bindValues(current)
        holder.itemView.setOnClickListener {
            listener.onItemClicked(holder.adapterPosition)
        }
    }

}

interface ItemClickListener{
    fun onItemClicked(position: Int)
}