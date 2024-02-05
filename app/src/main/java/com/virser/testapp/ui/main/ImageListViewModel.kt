package com.virser.testapp.ui.main

import androidx.lifecycle.viewModelScope
import com.virser.image_library.GoilClide
import com.virser.testapp.R
import com.virser.testapp.data.LoadState
import com.virser.testapp.ui.common.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import com.virser.testapp.data.ApiResult
import com.virser.testapp.core.model.ImageInfo
import com.virser.testapp.data.images.ImagesRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ImageListUiState(
    val items: List<ImageInfo> = emptyList(),
    val isLoading: Boolean = false,
    val userMessage: Int? = null,
)

@HiltViewModel
class ImageListViewModel @Inject constructor(
    private val imagesRepo: ImagesRepository,
) : BaseViewModel() {

    private val _isLoading = MutableStateFlow(false)
    private val _items: MutableSharedFlow<ApiResult<List<ImageInfo>>> = MutableSharedFlow()

    private val _itemsRequest = _items.map {
            filterErrors(it)
        }.map {
            LoadState.Success(it)
        }.onStart<LoadState<List<ImageInfo>>> { emit(LoadState.Loading) }

    val uiState: StateFlow<ImageListUiState> = combine(
        _isLoading, userMessage, _itemsRequest
    ) { isLoading, userMessage, tasksRequest ->
        when (tasksRequest) {
            is LoadState.Loading -> {
                ImageListUiState(isLoading = true)
            }

            is LoadState.Success -> {
                ImageListUiState(
                    items = tasksRequest.data, isLoading = isLoading, userMessage = userMessage
                )
            }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ImageListUiState(isLoading = true)
    )

    fun onFetchClick() {
        viewModelScope.launch {
            imagesRepo.getImages().collect {
                _items.emit(it)
            }
        }
    }

    fun onInvalidateClick() {
        GoilClide.get().invalidateCache()
    }

    private fun filterErrors(
        tasksApiResult: ApiResult<List<ImageInfo>>,
    ): List<ImageInfo> = if (tasksApiResult is ApiResult.Success) {
        tasksApiResult.data
    } else {
        showSnackbarMessage(R.string.generic_error_message)
        emptyList()
    }

}
