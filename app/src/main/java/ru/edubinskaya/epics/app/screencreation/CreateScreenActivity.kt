package ru.edubinskaya.epics.app.screencreation

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import ru.edubinskaya.epics.app.databinding.ActivityMainBinding
import ru.edubinskaya.epics.app.databinding.ActivitySreenCreationBinding
import ru.edubinskaya.epics.app.databinding.CreateFileActivityBinding


class CreateScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySreenCreationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySreenCreationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.add.setOnClickListener {
          //  startActivity(Intent(this, ::class.java))
        }
    }
}