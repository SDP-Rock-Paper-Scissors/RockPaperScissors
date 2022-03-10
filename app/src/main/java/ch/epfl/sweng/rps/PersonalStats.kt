package ch.epfl.sweng.rps

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TableRow.LayoutParams
import android.widget.TableRow.LayoutParams.WRAP_CONTENT
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.setPadding


class PersonalStats : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_personal_stats)
        val actionbar = supportActionBar
        actionbar?.setDisplayHomeAsUpEnabled(true)
        addNewRow("0b9d5384-9f1f-11ec-b909-0242ac120002","2022-03-09","test","12", "4:8")
        addNewRow("12345","2022-03-10","Jinglun Pan", "3", "2:1")
    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }


    private fun addNewRow(uuid: String, date:String, opponent:String, mode:String, score: String ) {
        val sizeInDp = 5
        val statsTable = findViewById<TableLayout>(R.id.statsTable)
        val row = TableRow(this)
        row.setBackgroundColor(
            Color.parseColor("#F0F7F7"))
        val scale = resources.displayMetrics.density
        val dpAsPixels = (sizeInDp * scale + 0.5f)
        row.isClickable
        row.setPadding(dpAsPixels.toInt())
        row.tag = uuid
        row.setOnClickListener {
            val intent = Intent(this, MatchDetail::class.java)
            intent.putExtra("matchUuid", row.tag as String)
            startActivity(intent)

        }
        val params = LayoutParams(WRAP_CONTENT, WRAP_CONTENT, 1f)
        val dateBlank = TextView(this)
        val opponentBlank = TextView(this)
        val modeBlank = TextView(this)
        val scoreBlank = TextView(this)
        dateBlank.text = date
        opponentBlank.text = opponent
        modeBlank.text = mode
        scoreBlank.text = score
        dateBlank.layoutParams = params
        opponentBlank.layoutParams = params
        modeBlank.layoutParams = params
        scoreBlank.layoutParams = params

        row.addView(dateBlank)
        row.addView(opponentBlank)
        row.addView(modeBlank)
        row.addView(scoreBlank)
        statsTable.addView(row)

    }

    fun displayMatchDetail(view: View) {
        when(view.id) {
            R.id.first_row_uuid -> {
                println("good")

            }
        }

    }
}

