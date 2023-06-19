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


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.a6706210162.myproducts.R
import com.a6706210162.myproducts.databinding.FragmentProductDetailBinding
import com.a6706210162.myproducts.data.Product
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class ProductDetailFragment : Fragment() {
    private val navigationArgs: ProductDetailFragmentArgs by navArgs()

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

    private lateinit var currentProduct: Product

    private val viewModel: ProductMangementViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as ProductManagementApplication).database.itemDao()
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getItemById(navigationArgs.itemId).observe(viewLifecycleOwner){
            currentProduct = it
            with(binding){
                itemName.text = it.itemName
                itemPrice.text = it.itemPrice.toString()
                itemCount.text = it.quantityInStock.toString()
            }
            binding.sellItem.isEnabled = viewModel.isStockAvailable(currentProduct)
        }
        binding.apply {
            sellItem.setOnClickListener { sellItem() }
            deleteItem.setOnClickListener { showConfirmationDialog() }
            editItem.setOnClickListener { editItem(currentProduct.id) }
        }
    }

    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }

    private fun sellItem(){
        if ( currentProduct.quantityInStock <= 0 ){
            return
        }
        val newItem = currentProduct.copy(quantityInStock = currentProduct.quantityInStock-1)
        viewModel.updateItem(newItem)
    }

    private fun editItem(id: Int){
        val action = ProductDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(
            getString(R.string.edit_fragment_title),
            itemId = id
        )
        findNavController().navigate(action)
    }

    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteItem(currentProduct)
        findNavController().navigateUp()
    }

    /**
     * Called when fragment is destroyed.
     */
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
