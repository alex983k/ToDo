package com.tomorrowit.todo

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import com.tomorrowit.todo.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actions, menu)

        return super.onCreateOptionsMenu(menu)
    }
}