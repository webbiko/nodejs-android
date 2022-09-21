package com.web.nodejsandroid

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.web.nodejsandroid.databinding.ActivityMainBinding
import android.os.AsyncTask
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (!startedNodeAlready) {
            startedNodeAlready = true
            Thread {
                startNodeWithArguments(
                    arrayOf(
                        "node", "-e",
                        "var http = require('http'); " +
                                "var versions_server = http.createServer( (request, response) => { " +
                                "  response.end('Versions: ' + JSON.stringify(process.versions)); " +
                                "}); " +
                                "versions_server.listen(3000);"
                    )
                )
            }.start()
        }

        binding.btVersions.setOnClickListener {
            object : AsyncTask<Void?, Void?, String?>() {
                override fun onPostExecute(result: String?) {
                    binding.tvVersions.text = result
                }

                override fun doInBackground(vararg p0: Void?): String? {
                    var nodeResponse = ""
                    try {
                        val localNodeServer = URL("http://127.0.0.1:3000/")
                        val inputBuffer = BufferedReader(
                            InputStreamReader(localNodeServer.openStream())
                        )
                        var inputLine: String = ""
                        while (inputBuffer?.readLine().also { line ->
                                line?.let {
                                    inputLine = it
                                }
                        } != null) nodeResponse += inputLine
                        inputBuffer?.close()
                    } catch (ex: Exception) {
                        nodeResponse = ex.toString()
                    }
                    return nodeResponse
                }
            }.execute()
        }

        // Example of a call to a native method
//        binding.sampleText.text = stringFromJNI()
    }


    companion object {
        // Used to load the 'nodejsandroid' library on application startup.
        external fun startNodeWithArguments(arguments: Array<String>): Int

        init {
            System.loadLibrary("native-lib");
            System.loadLibrary("node");
        }

        private var startedNodeAlready = false
    }
}