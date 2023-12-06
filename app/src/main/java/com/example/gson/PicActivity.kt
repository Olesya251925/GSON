package com.example.gson

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.bumptech.glide.Glide

class PicActivity : AppCompatActivity() {

    private lateinit var pic: ImageView
    private lateinit var toolbar: Toolbar
    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pic)

        link = intent?.getStringExtra(getString(R.string.key_link)) ?: ""


        pic = findViewById(R.id.imageView)
        Glide.with(this).load(link).into(pic)

        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == R.id.btn_heart) {
            // Отправляем результат обратно в родительскую Activity
            val resultIntent = Intent()
            resultIntent.putExtra(getString(R.string.key_link), link)
            setResult(RESULT_OK, resultIntent)
            finish() // Завершаем работу текущей Activity
            return true
        }

        return super.onOptionsItemSelected(item)
    }
}
