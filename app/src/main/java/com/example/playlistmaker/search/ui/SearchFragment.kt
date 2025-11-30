package com.example.playlistmaker.search.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.R
import com.example.playlistmaker.databinding.FragmentSearchBinding
import com.example.playlistmaker.player.ui.PlayerFragment
import com.example.playlistmaker.search.domain.models.Track
import com.example.playlistmaker.search.ui.view_model.SearchViewModel
import com.example.playlistmaker.utils.debounce
import org.koin.androidx.viewmodel.ext.android.viewModel


class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!

    private val searchViewModel by viewModel<SearchViewModel>()

    private lateinit var playerActivityResultLauncher: ActivityResultLauncher<Intent>
    private lateinit var onTrackClickDebounce: (Track) -> Unit

    private val tracksAdapter = TracksAdapter(object : OnTrackClickListener {
        override fun onTrackClick(track: Track) {
            onTrackClickDebounce(track)
        }
    })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        onTrackClickDebounce =
            debounce(CLICK_DEBOUNCE_DELAY, viewLifecycleOwner.lifecycleScope, false) { track ->
                searchViewModel.onTrackClick(track)
            }

        searchViewModel.observeRunPlayer().observe(viewLifecycleOwner) {
            findNavController().navigate(
                R.id.action_searchFragment_to_playerFragment,
                PlayerFragment.createArgs(it)
            )
        }

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = tracksAdapter

        searchViewModel.observeSearchState().observe(viewLifecycleOwner) {
            when (it) {
                is TracksState.Content -> {
                    tracksAdapter.updateData(it.tracks)
                    binding.progressBar.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                }

                TracksState.Empty -> {
                    showErrorData(true)
                    binding.progressBar.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                }

                TracksState.Failure -> {
                    hideHistory()
                    tracksAdapter.updateData(emptyList())
                    binding.progressBar.visibility = View.GONE
                    tracksAdapter.notifyDataSetChanged()
                    showErrorConnection(true)
                }

                TracksState.Loading -> {
                    hideHistory()
                    showErrorConnection(false)
                    showErrorData(false)
                    binding.progressBar.visibility = View.VISIBLE
                }

                is TracksState.History -> {
                    if (it.tracks.isEmpty()) {
                        binding.clearHistory.visibility = View.GONE
                        binding.lookingText.visibility = View.GONE
                    } else {
                        binding.clearHistory.visibility = View.VISIBLE
                        binding.lookingText.visibility = View.VISIBLE
                    }
                    tracksAdapter.updateData(it.tracks)
                    tracksAdapter.notifyDataSetChanged()
                }
            }
        }

        playerActivityResultLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && binding.search.text.isNullOrEmpty()) {
                searchViewModel.showHistory(true)
            }
        }

        binding.clearIcon.setOnClickListener {
            binding.search.text?.clear()
            searchViewModel.clearCurrentSearchText()
            tracksAdapter.updateData(emptyList())
            tracksAdapter.notifyDataSetChanged()
            searchViewModel.showHistory(true)
            showErrorData(false)
            showErrorConnection(false)
            val inputMethodManager =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
            inputMethodManager?.hideSoftInputFromWindow(it.windowToken, 0)
        }

        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (s.isNullOrEmpty()) {
                    binding.clearIcon.visibility = View.GONE
                    searchViewModel.clearCurrentSearchText()
                } else {
                    searchViewModel.searchDebounce(s.toString())
                    binding.clearIcon.visibility = View.VISIBLE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        }

        binding.updateButton.setOnClickListener {
            searchViewModel.updateSearch()
        }

        binding.search.addTextChangedListener(textWatcher)

        binding.search.setOnFocusChangeListener { _, hasFocus ->
            searchViewModel.showHistory(hasFocus && binding.search.text.isNullOrEmpty())
        }

        binding.clearHistory.setOnClickListener {
            searchViewModel.onHistoryClear()
        }

        searchViewModel.restoreSearchState()

    }

    override fun onResume() {
        super.onResume()

    }

    private fun hideHistory() {
        binding.clearHistory.visibility = View.GONE
        binding.lookingText.visibility = View.GONE

        tracksAdapter.updateData(ArrayList())
        tracksAdapter.notifyDataSetChanged()
    }

    private fun showErrorData(show: Boolean) {
        if (show) {
            binding.errorSearch.visibility = View.VISIBLE
            binding.errorSearchText.visibility = View.VISIBLE
        } else {
            binding.errorSearch.visibility = View.GONE
            binding.errorSearchText.visibility = View.GONE
        }
    }

    private fun showErrorConnection(show: Boolean) {
        if (show) {
            binding.errorConnect.visibility = View.VISIBLE
            binding.errorConnectText.visibility = View.VISIBLE
            binding.updateButton.visibility = View.VISIBLE
        } else {
            binding.errorConnect.visibility = View.GONE
            binding.errorConnectText.visibility = View.GONE
            binding.updateButton.visibility = View.GONE
        }
    }

    companion object {
        private const val CLICK_DEBOUNCE_DELAY = 1000L
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}