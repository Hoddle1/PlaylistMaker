package com.example.playlistmaker

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.playlistmaker.databinding.ActivitySettingsBinding


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        ViewCompat.setOnApplyWindowInsetsListener(binding.llMain) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageButton>(R.id.iBtnBack)
        backBtn.setOnClickListener {
            finish()
        }

        val sharedPrefs: SharedPreferences =
            getSharedPreferences(PLAYLIST_MAKER_PREFERENCES, MODE_PRIVATE)

        when (AppCompatDelegate.getDefaultNightMode()) {
            AppCompatDelegate.MODE_NIGHT_NO -> {
                binding.scThemeSwitcher.isChecked = false
            }

            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> {
                val currentNightMode =
                    resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                binding.scThemeSwitcher.isChecked =
                    currentNightMode == Configuration.UI_MODE_NIGHT_YES
            }

            AppCompatDelegate.MODE_NIGHT_YES -> {
                binding.scThemeSwitcher.isChecked = true
            }
        }
        binding.scThemeSwitcher.isSaveEnabled = false
        binding.scThemeSwitcher.setOnCheckedChangeListener { switcher, checked ->
            Log.d("CheckedChange", "YES")
            val newTheme = if (checked) {
                Constants.DARK_THEME
            } else {
                Constants.LIGHT_THEME
            }

            sharedPrefs.edit()
                .putInt("dark_theme", newTheme)
                .apply()

            (applicationContext as App).switchTheme(newTheme)

        }

        binding.mbShareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_link))
            startActivity(Intent.createChooser(shareIntent, getString(R.string.share_app)))
        }

        binding.mbSendSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.setData(Uri.parse("mailto:"))
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_email_to)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            startActivity(emailIntent)
        }

        binding.mbUserAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.user_agreement_link))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(agreementIntent)
        }
    }
}