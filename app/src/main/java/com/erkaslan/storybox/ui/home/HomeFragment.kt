package com.erkaslan.storybox.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.databinding.FragmentHomeBinding
import com.erkaslan.storybox.ui.StoryAdapter
import com.erkaslan.storybox.ui.StoryListener
import com.erkaslan.storybox.ui.adapter.StoryDetailAdapter
import com.erkaslan.storybox.ui.adapter.StoryDetailListener
import com.erkaslan.storybox.ui.component.CubicalPageTransformer
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), StoryListener, StoryDetailListener {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val viewModel by activityViewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObservers()
    }

    private fun initViews() {
        binding.rvStory.adapter = StoryAdapter(this)
    }

    private fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.viewState.collect {
                it.storyGroupList?.let { list ->
                    (binding.rvStory.adapter as StoryAdapter).submitList(list)
                }
            }
        }
    }

    override fun onStoryClicked(storyIndex: Int) {
        setStoryViewPager(storyIndex)
    }
    
    private fun setStoryViewPager(storyIndex: Int) {
        if (binding.vpStoryDetail.adapter == null) {
            binding.vpStoryDetail.visibility = View.VISIBLE
            binding.vpStoryDetail.adapter = StoryDetailAdapter().also {
                it.setStoryDetailListener(this)
                it.submitList(viewModel.viewState.value.storyGroupList?.toMutableList())
            }
            binding.vpStoryDetail.setPageTransformer(CubicalPageTransformer())
            binding.vpStoryDetail.setCurrentItem(storyIndex, false)
        } else {
            binding.vpStoryDetail.setCurrentItem(storyIndex, false)
        }
        binding.vpStoryDetail.requestLayout()
    }

    override fun onStoryNextClicked(storyGroup: StoryGroup?, position: Int?) {
        storyGroup?.let {
            position?.let {
                val nextIndex = storyGroup.lastStoryIndex + 1
                if (nextIndex < storyGroup.storyList.size) {
                    storyGroup.lastStoryIndex = nextIndex
                    (binding.vpStoryDetail.adapter as StoryDetailAdapter).notifyItemChanged(position)
                } else {
                    if (nextIndex == storyGroup.storyList.size) {
                        storyGroup.isAllStoriesWatched = true
                        storyGroup.lastStoryIndex = 0
                    }
                    (binding.vpStoryDetail.adapter as StoryDetailAdapter).notifyItemChanged(position)
                    goToNextGroup(storyGroup)
                }
            }
        }
    }

    private fun goToNextGroup(storyGroup: StoryGroup) {
        val list = viewModel.viewState.value.storyGroupList?.toMutableList()
        list?.let {
            if (it.size > it.indexOf(storyGroup) + 1) {
                binding.vpStoryDetail.setCurrentItem(it.indexOf(storyGroup) + 1, true)
            } else {
                closeStoryDetailView()
            }
        }
    }

    override fun onStoryPreviousClicked(storyGroup: StoryGroup?, position: Int?) {
        storyGroup?.let {
            position?.let {
                val previousIndex = storyGroup.lastStoryIndex - 1
                if (previousIndex > -1) {
                    storyGroup.lastStoryIndex = previousIndex
                    (binding.vpStoryDetail.adapter as StoryDetailAdapter).notifyItemChanged(position)
                } else {
                    if (previousIndex == -1) {
                        storyGroup.lastStoryIndex = 0
                    }
                    (binding.vpStoryDetail.adapter as StoryDetailAdapter).notifyItemChanged(position)
                    goToPreviousGroup(storyGroup)
                }
            }
        }
    }

    private fun goToPreviousGroup(storyGroup: StoryGroup) {
        val list = viewModel.viewState.value.storyGroupList?.toMutableList()
        list?.let {
            if (it.indexOf(storyGroup) - 1 >= 0) {
                binding.vpStoryDetail.setCurrentItem(it.indexOf(storyGroup) - 1, true)
            } else {
                setCurrentTimeToZero()
            }
        }
    }

    private fun setCurrentTimeToZero() { }

    private fun closeStoryDetailView() {
        binding.vpStoryDetail.adapter = null
        binding.vpStoryDetail.visibility = View.GONE
    }

    override fun onPauseVideo(storyGroup: StoryGroup?, position: Int?) {
        storyGroup?.let {
            position?.let {
                storyGroup.storyList[storyGroup.lastStoryIndex].isPaused = true
                (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(position)
            }
        }
    }

    override fun onResumeVideo(storyGroup: StoryGroup?, position: Int?) {
        storyGroup?.let {
            position?.let {
                storyGroup.storyList[storyGroup.lastStoryIndex].isPaused = false
                (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(position)
            }
        }
    }

    override fun onCloseStory() {
        closeStoryDetailView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}