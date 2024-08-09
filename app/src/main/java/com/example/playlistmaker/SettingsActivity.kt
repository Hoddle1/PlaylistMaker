package com.example.playlistmaker

import android.R.id.message
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContentView(R.layout.activity_settings)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val backBtn = findViewById<ImageButton>(R.id.back)
        backBtn.setOnClickListener {
            finish()
        }

        val shareApp = findViewById<FrameLayout>(R.id.shareApp)
        shareApp.setOnClickListener {
            val shareIntent = Intent(Intent.ACTION_SEND)
            shareIntent.setType("text/plain")
            shareIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.android_link))
            startActivity(Intent.createChooser(shareIntent, "Поделиться приложением"))
        }

        val sendSupport = findViewById<FrameLayout>(R.id.sendSupport)
        sendSupport.setOnClickListener {
            val emailIntent = Intent(Intent.ACTION_SENDTO)
            emailIntent.setData(Uri.parse("mailto:"))
            emailIntent.putExtra(Intent.EXTRA_EMAIL, arrayOf(getString(R.string.mail_email_to)))
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.mail_subject))
            emailIntent.putExtra(Intent.EXTRA_TEXT, getString(R.string.mail_text))
            startActivity(emailIntent)
        }

        val userAgreement = findViewById<FrameLayout>(R.id.userAgreement)
        userAgreement.setOnClickListener {
            val url = Uri.parse(getString(R.string.user_agreement_link))
            val agreementIntent = Intent(Intent.ACTION_VIEW, url)
            startActivity(agreementIntent)
        }
    }
}