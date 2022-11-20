package com.erkaslan.storybox.ui.home

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.viewpager2.widget.ViewPager2
import com.erkaslan.storybox.data.models.StoryGroup
import com.erkaslan.storybox.databinding.FragmentHomeBinding
import com.erkaslan.storybox.ui.StoryAdapter
import com.erkaslan.storybox.ui.StoryListener
import com.erkaslan.storybox.ui.adapter.StoryDetailAdapter
import com.erkaslan.storybox.ui.adapter.StoryDetailListener
import com.erkaslan.storybox.ui.component.CubicalPageTransformer
import com.erkaslan.storybox.util.GeneralUtils
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
            binding.vpStoryDetail.isClickable = true
            binding.vpStoryDetail.visibility = View.VISIBLE
            binding.vpStoryDetail.adapter = StoryDetailAdapter().also {
                it.setStoryDetailListener(this)
                val list = viewModel.viewState.value.storyGroupList?.apply { get(storyIndex).isInvisible = false }?.toMutableList()
                it.submitList(list)
            }
            binding.vpStoryDetail.setPageTransformer(CubicalPageTransformer())
            binding.vpStoryDetail.registerOnPageChangeCallback(pagerChangeCallback)
        }
        binding.vpStoryDetail.setCurrentItem(storyIndex, false)
        binding.vpStoryDetail.requestLayout()
    }

    private val pagerChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            when (state) {
                // set story visible states if pager is swiped
                ViewPager2.SCROLL_STATE_IDLE -> {
                    Log.d("STORYBOX", "IDLE")
                    val list = viewModel.viewState.value.storyGroupList
                    list?.let { list ->
                        val currentIndex = binding.vpStoryDetail.currentItem
                        if (currentIndex > 0) {
                            val previousGroup = list[currentIndex - 1]
                            if (!previousGroup.isInvisible) {
                                previousGroup.isInvisible = true
                                (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(currentIndex - 1)
                            }
                        }

                        if (currentIndex < list.size - 1) {
                            val nextGroup = list[currentIndex + 1]
                            if (!nextGroup.isInvisible) {
                                nextGroup.isInvisible = true
                                (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(currentIndex + 1)
                            }
                        }

                        val currentGroup = list[currentIndex]
                        currentGroup.isInvisible = false
                        (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(currentIndex)
                    }
                }
                ViewPager2.SCROLL_STATE_DRAGGING -> {
                    Log.d("STORYBOX", "DRAGGING")
                }
                else -> { }
            }
        }
    }

    override fun onStoryNextClicked(storyGroup: StoryGroup?, position: Int?) {
        storyGroup?.let {
            position?.let {
                val nextIndex = storyGroup.lastStoryIndex + 1
                if (nextIndex < storyGroup.storyList.size) {
                    storyGroup.lastStoryIndex = nextIndex
                    (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(position)
                } else {
                    if (nextIndex == storyGroup.storyList.size) storyGroup.isAllStoriesWatched = true
                    goToNextGroup(storyGroup)
                }
            }
        }
    }

    private fun goToNextGroup(storyGroup: StoryGroup) {
        val list = viewModel.viewState.value.storyGroupList?.toMutableList()
        list?.let {
            val nextGroupIndex = it.indexOf(storyGroup) + 1
            if (it.size > nextGroupIndex) {
                list[nextGroupIndex].isInvisible = false
                (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(nextGroupIndex)
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
                    storyGroup.isInvisible = false
                    (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(position)
                } else {
                    val list = viewModel.viewState.value.storyGroupList?.toMutableList()
                    val previousGroupIndex = list?.indexOf(storyGroup)?.minus(1)
                    val previousGroup = list?.get(previousGroupIndex ?: 0)
                    if (previousIndex == -1) {
                        storyGroup.isInvisible = true
                        storyGroup.lastStoryIndex = 0
                    }
                    (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(position)
                    goToPreviousGroup(previousGroup, previousGroupIndex)
                }
            }
        }
    }

    private fun goToPreviousGroup(storyGroup: StoryGroup?, index: Int?) {
        storyGroup?.let {
            index?.let {
                if (index >= 0) {
                    storyGroup.isInvisible = false
                    (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(index)
                    binding.vpStoryDetail.setCurrentItem(index, true)
                }
            }
        }
    }

    private fun closeStoryDetailView(storyGroup: StoryGroup? = null, position: Int? = null) {
        binding.vpStoryDetail.isClickable = false
        storyGroup?.let {
            position?.let {
                storyGroup.isInvisible = true
                val nextIndex = storyGroup.lastStoryIndex + 1
                if (nextIndex < storyGroup.storyList.size) storyGroup.lastStoryIndex = nextIndex
                else if (nextIndex == storyGroup.storyList.size) storyGroup.isAllStoriesWatched = true
            }
        }
        binding.vpStoryDetail.unregisterOnPageChangeCallback(pagerChangeCallback)
        GeneralUtils.slideStory(binding.vpStoryDetail, binding.vpStoryDetail.y.toInt(), requireActivity().window.decorView.height)
        binding.vpStoryDetail.adapter = null
        binding.rvStory.adapter?.notifyDataSetChanged()
        position?.let { binding.rvStory.scrollToPosition(it) }
    }

    override fun onResumeVideo(storyGroup: StoryGroup?, position: Int?) {
        storyGroup?.let {
            position?.let {
                storyGroup.isInvisible = false
                (binding.vpStoryDetail.adapter as? StoryDetailAdapter)?.notifyItemChanged(position)
            }
        }
    }

    override fun onCloseStory(storyGroup: StoryGroup?, position: Int?) {
        closeStoryDetailView(storyGroup, position)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}