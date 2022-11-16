package com.erkaslan.storybox.ui.home

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.erkaslan.storybox.databinding.FragmentHomeBinding
import com.erkaslan.storybox.ui.StoryAdapter
import com.erkaslan.storybox.ui.StoryListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment(), StoryListener {
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
                it.storyGroupList?.let {
                    // Multiply data for test
                    val list = it.toMutableList() + it.toMutableList()
                    (binding.rvStory.adapter as StoryAdapter).submitList(list)
                }
            }
        }
    }

    override fun onStoryClicked(storyIndex: Int) {
        setStoryViewPager()
    }
    
    private fun setStoryViewPager() {
        binding.vpStoryDetail.visibility = View.VISIBLE
        binding.vpStoryDetail.requestLayout()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}