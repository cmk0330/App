package com.cmk.app.ui.activity.test

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.cmk.app.R
import kotlinx.android.synthetic.main.activity_fab.*

class FabActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fab)
        floatingActionButton.setOnClickListener{
            floatingActionButton.isExpanded = true
        }
        circularRevealCardView.setOnClickListener {
            floatingActionButton.isExpanded = false
        }
    }

    override fun onBackPressed() {

        if (floatingActionButton.isExpanded) {
            floatingActionButton.isEnabled =false
        } else{
            super.onBackPressed()
        }
    }
}