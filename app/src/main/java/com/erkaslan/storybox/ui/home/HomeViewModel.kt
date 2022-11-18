package com.erkaslan.storybox.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.data.sample.SampleData
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {
    private var _viewState = MutableStateFlow(HomeViewState())
    val viewState: StateFlow<HomeViewState> = _viewState

    init {
        getStories()
    }

    private fun getStories() {
        viewModelScope.launch {
            // Simulate initial api calls
            delay(1000)
            _viewState.value = viewState.value.copy(storyGroupList = SampleData.sampleStoryGroupList)
        }
    }
}

data class HomeViewState(
    val storyGroupList: List<StoryGroup>? = null,
    val currentGroup: StoryGroup? = null,
    val currentTimeValueInMillis: Int? = 5000,
)