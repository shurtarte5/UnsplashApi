package com.example.unsplashapi.ui

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels

import com.example.unsplashapi.R
import androidx.paging.LoadState
import com.example.unsplashapi.data.UnsplashPhoto

import com.example.unsplashapi.databinding.FragmentHomeBinding
import com.example.unsplashapi.ui.adapter.UnsplashPhotoAdapter
import com.example.unsplashapi.ui.adapter.UnsplashPhotoLoadStateAdapter
import com.example.unsplashapi.ui.vm.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint




@AndroidEntryPoint
class HomeFragment : Fragment(), UnsplashPhotoAdapter.OnItemClickListener{
    private val viewModel by viewModels<HomeViewModel>()



    private var _binding:FragmentHomeBinding?=null
    private val binding get() = _binding!!



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding= FragmentHomeBinding.inflate(inflater,container,false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val adapter = UnsplashPhotoAdapter(this)

        binding.apply {
            recyclerView.setHasFixedSize(true)
            recyclerView.adapter = adapter.withLoadStateHeaderAndFooter(
                header = UnsplashPhotoLoadStateAdapter{adapter.retry()},
                footer = UnsplashPhotoLoadStateAdapter{adapter.retry()}

            )
            buttonRetry.setOnClickListener {
                adapter.retry()
            }
        }

        viewModel.photos.observe(viewLifecycleOwner){
            adapter.submitData(viewLifecycleOwner.lifecycle, it)
        }


        adapter.addLoadStateListener { loadState ->
            binding.apply {
                progressBar.isVisible = loadState.source.refresh is LoadState.Loading
                recyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                buttonRetry.isVisible = loadState.source.refresh is LoadState.Error
                textViewError.isVisible = loadState.source.refresh is LoadState.Error

                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached &&
                    adapter.itemCount < 1) {
                    recyclerView.isVisible = false
                    textViewEmpty.isVisible = true
                } else {
                    textViewEmpty.isVisible = false
                }

            }
        }

        setHasOptionsMenu(true)

        
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_home,menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                if (query != null) {
                    binding.recyclerView.scrollToPosition(0)
                    viewModel.searchPhotos(query)
                    searchView.clearFocus()
                }
                return true

            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return true
            }

        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding=null
    }

    override fun onItemClick(photo: UnsplashPhoto) {
        val intent = Intent(context,DetailActivity::class.java)
        intent.putExtra(INTENT_DETAIL_KEY,photo)
        startActivity(intent)
    }

    companion object{
        const val INTENT_DETAIL_KEY="detail"
    }
}