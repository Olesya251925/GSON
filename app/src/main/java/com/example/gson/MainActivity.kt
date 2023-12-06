package com.example.gson

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import okhttp3.*
import timber.log.Timber
import timber.log.Timber.Forest.plant
import java.io.IOException

const val CODE = 1

interface CellClickListener {
    fun onCellClickListener(link: String)
}

class MainActivity : AppCompatActivity(), CellClickListener {

    private val URL =
        "https://api.flickr.com/services/rest/?method=flickr.photos.search&api_key=ff49fcd4d4a08aa6aafb6ea3de826464&tags=cat&format=json&nojsoncallback=1"
    private val okHttpClient: OkHttpClient = OkHttpClient()
    private var links: Array<String> = arrayOf()
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        plant(Timber.DebugTree())
        setContentView(R.layout.activity_main)
        recyclerView = findViewById(R.id.rView)

        getJSONFromServer()
    }

    private fun getJSONFromServer() {
        val request: Request = Request.Builder().url(URL).build()

        okHttpClient.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {

            }

            override fun onResponse(call: Call, response: Response) {
                val jsonFromServer = response.body?.string()
                parseJSON(jsonFromServer)
            }
        })
    }

    private fun parseJSON(jsonFromServer: String?) {

        val result: Wrapper = Gson().fromJson(jsonFromServer, Wrapper::class.java)
        for (i in 0 until result.photos.photo.size) {
            val link =
                "https://farm${result.photos.photo[i].farm}.staticflickr.com/${result.photos.photo[i].server}/${result.photos.photo[i].id}_${result.photos.photo[i].secret}_z.jpg"
            links += link
            Timber.d("Photo_link", links[i])
        }
        runOnUiThread {
            recyclerView.layoutManager =
                StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL)
            recyclerView.adapter = MyRecyclerAdapter(this, links, this)
        }
    }

    override fun onCellClickListener(link: String) {
        val intent = Intent(this, PicActivity::class.java)
        intent.putExtra(getString(R.string.key_link), link)
        startActivityForResult(intent, CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == CODE && resultCode == RESULT_OK) {
            val link = data?.getStringExtra(getString(R.string.key_link))

            // Добавьте ваш код для обработки результата
            if (link != null) {
                val snackbar = Snackbar.make(
                    findViewById(android.R.id.content),
                    "Картинка добавлена в избранное",
                    Snackbar.LENGTH_LONG
                )

                snackbar.setAction("Открыть") {
                    // Открыть ссылку во встроенном браузере
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    startActivity(intent)
                }

                snackbar.show()
            }
        }
    }
}

data class Wrapper(
    val photos: Page,
    val stat: String
)

data class Page(
    val page: Int,
    val pages: Int,
    val perpage: Int,
    val total: Int,
    val photo: Array<Photo>
)

data class Photo(
    val id: String,
    val owner: String,
    val secret: String,
    val server: Int,
    val farm: Int,
    val title: String,
    val isPublic: Boolean,
    val isFamily: Boolean
)
