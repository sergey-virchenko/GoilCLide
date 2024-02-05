package com.virser.testapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.virser.image_library.GoilClide
import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.databinding.FragmentRegularBinding
import com.virser.testapp.databinding.ItemImageBinding
import com.virser.testapp.ui.main.ImageListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegularFragment : Fragment() {

    private val viewModel by viewModels<ImageListViewModel>()
    private var binding: FragmentRegularBinding? = null
    private val adapter by lazy { ItemsAdapter(this) }
    private val layoutManager by lazy { LinearLayoutManager(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentRegularBinding.inflate(inflater)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding?.apply {
            fetch.setOnClickListener { viewModel.onFetchClick() }
            invalidate.setOnClickListener { viewModel.onInvalidateClick() }
            listImages.adapter = adapter
            listImages.layoutManager = layoutManager
        }

        lifecycleScope.launch {
            viewModel.uiState.flowWithLifecycle(lifecycle, Lifecycle.State.STARTED).collect {
                adapter.submitList(it.items)

                it.userMessage?.let {
                    viewModel.snackbarMessageShown()
                    binding?.apply {
                        Snackbar.make(root, "Snackbar test", Snackbar.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}

private class ItemsAdapter(
    private val fragment: Fragment,
) : ListAdapter<ImageInfo, ImageItemViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageItemViewHolder =
        ImageItemViewHolder.from(parent, fragment)

    override fun onBindViewHolder(holder: ImageItemViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

}

private class ImageItemViewHolder(
    private val binding: ItemImageBinding,
    private val fragment: Fragment,
) :
    RecyclerView.ViewHolder(binding.root) {

    fun bind(item: ImageInfo) {
        binding.txtImageId.text = item.id.toString()
        GoilClide.get().load(
            fragment,
            item.imageUrl,
            binding.imgImage,
            R.drawable.placeholder_load,
            R.drawable.placeholder_error
        )
    }

    companion object {
        fun from(
            parent: ViewGroup,
            fragment: Fragment,
        ): ImageItemViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = ItemImageBinding.inflate(
                layoutInflater, parent, false
            )
            return ImageItemViewHolder(binding, fragment)
        }
    }
}

class DiffCallback : DiffUtil.ItemCallback<ImageInfo>() {
    override fun areItemsTheSame(
        oldItem: ImageInfo,
        newItem: ImageInfo,
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: ImageInfo,
        newItem: ImageInfo,
    ): Boolean {
        return oldItem == newItem
    }
}
