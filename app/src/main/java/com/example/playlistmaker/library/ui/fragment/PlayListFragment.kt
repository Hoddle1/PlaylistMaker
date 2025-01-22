package com.example.playlistmaker.library.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.playlistmaker.databinding.FragmentPlayListBinding
import com.example.playlistmaker.library.ui.view_model.PlayListViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel


class PlayListFragment : Fragment() {
    private var _binding: FragmentPlayListBinding? = null

    private val binding get() = _binding!!

    private val viewModel by viewModel<PlayListViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlayListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance() = PlayListFragment()
    }

}