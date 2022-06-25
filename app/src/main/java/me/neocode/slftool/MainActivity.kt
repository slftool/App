package me.neocode.slftool

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import me.neocode.slftool.databinding.ActivityMainBinding
import okhttp3.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.io.IOException
import kotlin.system.exitProcess

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: Adapter
    private lateinit var json: JSONObject
    private val icons: HashMap<String, Int> = HashMap()

    override fun onCreate(savedInstanceState: Bundle?) {
        WindowCompat.setDecorFitsSystemWindows(window, false)
        super.onCreate(savedInstanceState)

        icons["stadt"] = R.drawable.ic_baseline_location_city_24
        icons["land"] = R.drawable.ic_baseline_flag_24
        icons["fluss"] = R.drawable.ic_baseline_waves_24
        icons["name"] = R.drawable.ic_baseline_person_24
        icons["beruf"] = R.drawable.ic_baseline_work_24
        icons["tier"] = R.drawable.ic_baseline_pets_24
        icons["marke"] = R.drawable.ic_baseline_store_24
        icons["pflanze"] = R.drawable.ic_baseline_grass_24

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.fab.setOnClickListener {
            changeWords()
        }

        binding.refreshLayout.setOnRefreshListener {
            changeWords()
            binding.refreshLayout.isRefreshing = false
        }

        binding.letter.addTextChangedListener(object : TextWatcher {
            var changed = false
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(e: Editable?) {
                if(!changed && (binding.letter.text?.length ?: 0) > 0) {
                    changed = true
                    binding.letter.setText(binding.letter.text.toString().takeLast(1))
                    binding.letter.setSelection(1)
                    changeWords()
                }else{
                    changed = false
                }
            }

        })

        binding.recycler.layoutManager = LinearLayoutManager(this)

        val data = ArrayList<ItemModel>()
        data.add(ItemModel(R.drawable.ic_baseline_help_24, getString(R.string.letter_required), ""))
        adapter = Adapter(data)
        binding.recycler.adapter = adapter

        val client: OkHttpClient = OkHttpClient.Builder()
            .cache(
                Cache(
                    directory = File(application.cacheDir, "http_cache"),
                    // $0.05 worth of phone storage in 2020
                    maxSize = 50L * 1024L * 1024L // 50 MiB
                )
            )
            .build()

        val request = Request.Builder()
            .url("https://slftool.github.io/data.json")
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    MaterialAlertDialogBuilder(this@MainActivity)
                        .setTitle(getString(R.string.no_internet))
                        .setMessage(getString(R.string.loading_error))
                        .setNeutralButton(getString(R.string.ok)) { _, _ ->
                            finishAffinity()
                            exitProcess(0)
                        }
                        .show()
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) throw IOException("Unexpected code $response")

                    json = JSONObject(response.body!!.string())
                    runOnUiThread {
                        Toast.makeText(applicationContext, R.string.loaded_successfully, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        })
    }

    private fun changeWords() {
        val letter: String = binding.letter.text.toString().lowercase()
        val data = ArrayList<ItemModel>()

        if (this::json.isInitialized && json.has(letter)) {
            val letterJSON: JSONObject = json.getJSONObject(letter)

            letterJSON.keys().forEach { key ->
                val obj: JSONArray = letterJSON.getJSONArray(key)
                val random: String = obj.getString((0 until obj.length()).random())

                var icon: Int = if(icons.containsKey(key)) {
                    icons[key]!!
                }else{
                    R.drawable.ic_baseline_help_24
                }

                data.add(ItemModel(icon, random, key.capitalize()))
            }
        }else{
            data.add(ItemModel(R.drawable.ic_baseline_help_24, getString(R.string.letter_required), ""))
        }

        adapter.setItems(data)
    }

}