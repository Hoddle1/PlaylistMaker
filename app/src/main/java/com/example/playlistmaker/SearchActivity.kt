package com.example.playlistmaker

import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.core.widget.doOnTextChanged
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.playlistmaker.databinding.ActivitySearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySearchBinding
    private var searchText: String = SEARCH_DEF
    private val tracks = ArrayList<Track>()
    private val trackAdapter = TrackAdapter(tracks)

    private val iTunesBaseUrl = "https://itunes.apple.com"
    private val retrofit = Retrofit.Builder()
        .baseUrl(iTunesBaseUrl)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    private val iTunesService = retrofit.create(iTunesSearchApi::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.back.setOnClickListener { finish() }

        binding.clearIcon.setOnClickListener {
            clearSearchText()
            tracks.clear()
            trackAdapter.notifyDataSetChanged()
        }

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF)
            binding.queryInput.setText(searchText)
        }

        binding.placeholderUpdateButton.setOnClickListener{
            search()
        }

        binding.queryInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                search()
                true
            }
            false
        }

        binding.queryInput.doOnTextChanged { s, _, _, _ ->
            binding.clearIcon.isVisible = !s.isNullOrEmpty()
            searchText = s.toString()
        }


        binding.tracksList.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.tracksList.adapter = trackAdapter

    }

    private fun clearSearchText() {
        binding.queryInput.setText(R.string.empty_string)
        binding.queryInput.clearFocus()
        hideKeyboard()
    }

    private fun hideKeyboard() {
        val inputMethodManager =
            getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(binding.queryInput.windowToken, 0)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(SEARCH_TEXT, searchText)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        searchText = savedInstanceState.getString(SEARCH_TEXT, SEARCH_DEF)
    }

    private fun search() {
        iTunesService.search(binding.queryInput.text.toString())
            .enqueue(object : Callback<TrackResponse> {
                override fun onResponse(
                    call: Call<TrackResponse>,
                    response: Response<TrackResponse>
                ) {
                    when (response.code()) {
                        200 -> {
                            if (response.body()?.results?.isNotEmpty() == true) {
                                tracks.clear()
                                tracks.addAll(response.body()?.results!!)
                                trackAdapter.notifyDataSetChanged()
                                showMessage(SearchStatus.NORMAL)
                            } else {
                                showMessage(SearchStatus.NOT_FOUND)
                            }
                        }

                        else -> showMessage(SearchStatus.NO_INTERNET)
                    }

                }

                override fun onFailure(call: Call<TrackResponse>, t: Throwable) {
                    showMessage(SearchStatus.NO_INTERNET)
                }
            })
    }

    private fun showMessage(status: SearchStatus) {
        when (status) {
            SearchStatus.NORMAL -> {
                binding.tracksList.visibility = View.VISIBLE
                binding.placeholderText.visibility = View.GONE
                binding.placeholderImage.visibility = View.GONE
                binding.placeholderUpdateButton.visibility = View.GONE
            }

            SearchStatus.NOT_FOUND -> {
                binding.tracksList.visibility = View.GONE
                binding.placeholderText.text = getString(R.string.not_found)
                binding.placeholderText.visibility = View.VISIBLE

                binding.placeholderImage.setImageResource(R.drawable.not_found)
                binding.placeholderImage.visibility = View.VISIBLE

                binding.placeholderUpdateButton.visibility = View.GONE
            }

            SearchStatus.NO_INTERNET -> {
                binding.tracksList.visibility = View.GONE
                binding.placeholderText.text = getString(R.string.no_internet)
                binding.placeholderText.visibility = View.VISIBLE

                binding.placeholderImage.setImageResource(R.drawable.no_internet)
                binding.placeholderImage.visibility = View.VISIBLE

                binding.placeholderUpdateButton.visibility = View.VISIBLE
            }
        }
    }

    companion object {
        const val SEARCH_TEXT = "SEARCH_TEXT"
        const val SEARCH_DEF = ""

    }

    enum class SearchStatus {
        NORMAL, NOT_FOUND, NO_INTERNET
    }

}