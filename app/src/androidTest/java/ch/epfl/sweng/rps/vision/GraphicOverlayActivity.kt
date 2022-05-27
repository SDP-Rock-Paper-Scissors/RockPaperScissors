package ch.epfl.sweng.rps.vision

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import ch.epfl.sweng.rps.databinding.GraphicOverlayTestBinding


class GraphicOverlayActivity : AppCompatActivity() {


    private lateinit var binding: GraphicOverlayTestBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = GraphicOverlayTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }


}