package com.drivona.speed.utils

import android.os.AsyncTask
import android.util.Log
import java.io.File

class AudioConverterTask (private val inputFile: File, private val outputFile: File) : AsyncTask<Void, Void, Boolean>() {

    override fun doInBackground(vararg params: Void?): Boolean {
        try {

        } catch (e: Exception) {
            Log.e("AudioConverterTask", "Error converting audio file", e)
            return false
        }
        return true
    }
}