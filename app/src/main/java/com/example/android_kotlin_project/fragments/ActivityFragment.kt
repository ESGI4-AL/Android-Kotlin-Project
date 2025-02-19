package com.example.android_kotlin_project.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Button
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.example.android_kotlin_project.R

class ActivityFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_activity, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

      //  val fullBodyButton = view.findViewById<Button>(R.id.start_yoga)
        val yogaButton = view.findViewById<Button>(R.id.start_yoga)
     //   val cardioButton = view.findViewById<Button>(R.id.start_cardio)
        val scaleUpAnimation = AnimationUtils.loadAnimation(context, R.anim.scale_up)


    /*    fullBodyButton.setOnClickListener {
            it.startAnimation(scaleUpAnimation)
            Toast.makeText(context, "Full Body Workout commencé !", Toast.LENGTH_SHORT).show()
        }

     */

        yogaButton.setOnClickListener {
            Toast.makeText(context, "Yoga Session commencée !", Toast.LENGTH_SHORT).show()
        }

 /*       cardioButton.setOnClickListener {
            Toast.makeText(context, "Cardio Blast commencé !", Toast.LENGTH_SHORT).show()
        }

  */
    }
}