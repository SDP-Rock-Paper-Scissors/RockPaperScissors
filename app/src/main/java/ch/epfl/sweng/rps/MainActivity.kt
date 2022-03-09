package ch.epfl.sweng.rps

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.EditText

const val EXTRA_MESSAGE = "ch.epfl.sweng.rps.MESSAGE"

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun displayGreetings(view: View){
        val editText = findViewById<EditText>(R.id.nameInput)
        val message = editText.text.toString()
        val intent = Intent(this, DisplayGreeting::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    fun displayPersonalStats(view: View){
        val intent = Intent(this, PersonalStats::class.java)
        startActivity(intent)

    }

}