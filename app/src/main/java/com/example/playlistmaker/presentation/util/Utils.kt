package com.example.playlistmaker.presentation.util

import android.content.Context
import android.util.TypedValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {

    fun dpToPx(dp: Float, context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp,
            context.resources.displayMetrics
        ).toInt()
    }

    fun convertMillisToTime(millis: Int): String {
        return SimpleDateFormat("mm:ss", Locale.getDefault()).format(millis)
    }

    fun convertTimeToMillis(time: String): Int {
        val parts = time.split(":")
        if (parts.size != 2) throw IllegalArgumentException("Time format must be mm:ss")

        val minutes = parts[0].toIntOrNull() ?: throw IllegalArgumentException("Invalid minutes value")
        val seconds = parts[1].toIntOrNull() ?: throw IllegalArgumentException("Invalid seconds value")

        return (minutes * 60 + seconds) * 1000
    }

    fun <T> debounce(
        delayMillis: Long,
        coroutineScope: CoroutineScope,
        useLastParam: Boolean,
        action: (T) -> Unit
    ): (T) -> Unit {
        var debounceJob: Job? = null
        return { param: T ->
            if (useLastParam) {
                debounceJob?.cancel()
            }
            if (debounceJob?.isCompleted != false || useLastParam) {
                debounceJob = coroutineScope.launch {
                    delay(delayMillis)
                    action(param)
                }
            }
        }
    }
}