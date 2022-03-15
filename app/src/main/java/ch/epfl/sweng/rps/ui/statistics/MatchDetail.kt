package ch.epfl.sweng.rps.ui.statistics

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.R

class MatchDetail : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_match_detail)
        val matchUuid = intent.getStringExtra("matchUuid")
        println(matchUuid)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}
