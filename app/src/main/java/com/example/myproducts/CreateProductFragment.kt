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
package com.example.myproducts

import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myproducts.databinding.FragmentAddProductBinding
import com.example.myproducts.data.Product

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddProductBinding.inflate(inflater, container, false)
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

    private fun updateItem(product: Product){
        var newProduct: Product
        if ( isEntryValid() ){
            newProduct = Product(
                id = product.id,
                itemName = binding.itemName.text.toString(),
                itemPrice = binding.itemPrice.text.toString().toDouble(),
                quantityInStock = binding.itemCount.text.toString().toInt()
            )
            viewModel.updateItem(newProduct)

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
            )
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
}
