/*
 * Copyright (C) 2021 The Android Open Source Project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.a6706210162.myproducts

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.a6706210162.myproducts.data.Product
import com.a6706210162.myproducts.data.Item
import com.a6706210162.myproducts.databinding.FragmentAddProductBinding
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONException
import org.json.JSONObject


class CreateProductFragment : Fragment() {

    private val navigationArgs: ProductDetailFragmentArgs by navArgs()

    private val viewModel: ProductMangementViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as ProductManagementApplication).database
                .itemDao()
        )
    }

    lateinit var product: Product

    private var _binding: FragmentAddProductBinding? = null
    private val binding get() = _binding!!

    lateinit var recyclerView: RecyclerView

    private var mAdapter: ProductOptionAdapter?= null;
    private var productsOptions: MutableList<Item> = ArrayList()

    lateinit var progressBar: ProgressBar
    lateinit var itemNameInput: TextInputEditText
    lateinit var itemPriceInput: TextInputEditText
    lateinit var itemStockInput: TextInputEditText
    lateinit var imageStockInput: TextInputEditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)

        recyclerView = binding.productsRecyclerView
        itemNameInput = binding.itemName
        itemPriceInput = binding.itemPrice
        itemStockInput = binding.itemCount
        imageStockInput = binding.itemImage
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        mAdapter = ProductOptionAdapter(productsOptions, requireContext(), itemNameInput, itemPriceInput, itemStockInput, imageStockInput)
        recyclerView.adapter = mAdapter
        progressBar = binding.progressBar
        fetchQuetionList()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val id = navigationArgs.itemId
        if (id > 0) {
            viewModel.getItemById(id).observe(this.viewLifecycleOwner) { selectedItem ->
                product = selectedItem
                bind(product)
            }
            binding.saveAction.setOnClickListener { updateItem(product) }
        } else {
            binding.saveAction.setOnClickListener {
                addNewItem()
            }
        }

    }

    private fun fetchQuetionList() {
        if (!isInternetAvailable(requireContext())) {
            Toast.makeText(requireContext(), "No internet connection", Toast.LENGTH_LONG).show()
        }

        progressBar.visibility = View.VISIBLE
        AndroidNetworking.get("https://dummyjson.com/products")
            .setPriority(Priority.MEDIUM)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    progressBar.visibility = View.GONE
                    try {
                        // Extract data by key
                        val itemsArray = response.getJSONArray("products")
                        for (i in 0 until itemsArray.length()) {
                            val itemArray = itemsArray.getJSONObject(i)
                            val id = itemArray.getInt("id")
                            val title = itemArray.getString("title")
                            val price = itemArray.getString("price")
                            val stock = itemArray.getString("stock")
                            val image = itemArray.getString("thumbnail")

                            productsOptions.add(Item(id, title, price, stock, image))
                        }
                        Log.d(TAG, productsOptions.toString())
                        mAdapter!!.notifyDataSetChanged()
                    } catch (e: JSONException) {
                        progressBar.visibility = View.GONE
                        Log.e(TAG, e.message.toString())
                    }
                }

                override fun onError(error: ANError) {
                    progressBar.visibility = View.GONE
                    Log.e(TAG, error.errorBody)
                    Toast.makeText(requireContext(), "Failed to load data", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun updateItem(product: Product){
        var newProduct: Product
        if ( isEntryValid() ){
            newProduct = Product(
                id = product.id,
                itemName = binding.itemName.text.toString(),
                itemPrice = binding.itemPrice.text.toString().toDouble(),
                quantityInStock = binding.itemCount.text.toString().toInt(),
                image = binding.itemImage.text.toString(),
            )
            viewModel.updateItem(newProduct)
            showNotification("Product with name ${binding.itemName.text} has been updated")
            val action = CreateProductFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
        else {
            Toast.makeText(requireContext(), "Enter valid information", Toast.LENGTH_SHORT).show()
        }
    }

    private fun bind(product: Product) {
        binding.apply {
            itemName.setText(product.itemName)
            itemPrice.setText(product.itemPrice.toString())
            itemCount.setText(product.quantityInStock.toString())
            itemImage.setText(product.image)
        }
    }

    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemCount.text.toString()
        )
    }

    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemPrice.text.toString(),
                binding.itemCount.text.toString(),
                binding.itemImage.text.toString()
            )
            showNotification("Product with name ${binding.itemName.text} has been added")
            val action = CreateProductFragmentDirections.actionAddItemFragmentToItemListFragment()
            findNavController().navigate(action)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        val inputMethodManager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }

    fun isInternetAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val networkCapabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            val networkInfo = connectivityManager.activeNetworkInfo ?: return false
            return networkInfo.isConnected
        }
    }

    fun showNotification(message: String){
        val notificationId = 12345678
        val channelId = "prodman123"
        val channelName = "Product Management"
        val notificationText = message

        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification = NotificationCompat.Builder(requireContext(), channelId)
            .setContentTitle(channelName)
            .setContentText(notificationText)
            .setSmallIcon(R.drawable.baseline_notifications_24)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        val notificationManager = requireContext().getSystemService(NotificationManager::class.java)
        notificationManager.notify(notificationId, notification)
    }
}
