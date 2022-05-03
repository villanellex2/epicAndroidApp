package ru.edubinskaya.epics.app.view

import SettingsRecyclerViewAdapter
import android.content.Intent
import android.database.Cursor
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ru.edubinskaya.epics.app.R


class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        findViewById<View>(R.id.create_file).setOnClickListener {
            val intent = Intent(this, CreateConfigActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeRecyclerView() {
        val db = baseContext.openOrCreateDatabase("configuration.db", MODE_PRIVATE, null)
        db.execSQL("CREATE TABLE IF NOT EXISTS files (fileName TEXT)")

        val query: Cursor = db.rawQuery("SELECT * FROM files;", null)
        val recyclerView = findViewById<RecyclerView>(R.id.screen_list)
        val list = ArrayList<String>()
        while (query.moveToNext()) {
            val name: String = query.getString(0)
            list.add(name)
        }

        val adapter = SettingsRecyclerViewAdapter(this, list)

        recyclerView.addItemDecoration(DividerItemDecoration(this, DividerItemDecoration.VERTICAL))
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        query.close()
        db.close()
    }

    override fun onStart() {
        initializeRecyclerView()
        super.onStart()
    }
}