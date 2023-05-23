package com.example.myproducts

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.myproducts.R
import com.example.myproducts.databinding.ProductListFragmentBinding

/**
 * Main fragment displaying details for all items in the database.
 */
class ListProductFragment : Fragment(), ItemClickListener {

    private var _binding: ProductListFragmentBinding? = null
    private val binding get() = _binding!!

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
        _binding = ProductListFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = ProductListAdapter(this)
        binding.recyclerView.adapter = adapter

        viewModel.allItems.observe(viewLifecycleOwner){
            adapter.submitList(it)
        }

        binding.floatingActionButton.setOnClickListener {
            val action = ListProductFragmentDirections.actionItemListFragmentToAddItemFragment(
                getString(R.string.add_fragment_title)
            )
            this.findNavController().navigate(action)
        }
    }

    override fun onItemClicked(position: Int) {
        val action = ListProductFragmentDirections.actionItemListFragmentToItemDetailFragment(
            viewModel.allItems.value?.get(position)!!.id
        )
        findNavController().navigate(action)
    }
}
