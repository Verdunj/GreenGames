package com.example.myapplication

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.ViewModelProviders


/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {
    private var model: Communicator?=null

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Creation du communicateur pour l'envoi au autres pages */
        model= ViewModelProviders.of(this.requireActivity()).get(Communicator::class.java)

        val layout = view.findViewById<LinearLayout>(R.id.button_layout)
        for (g in GameData.core.findAllGames()) {
            val b = Button(view.context)
            b.text = g.name
            /* Envoi l'ID du jeu a la page de configuration */
            b.setOnClickListener {
                model!!.setMsgGameId(g.id)
                g.onLoad()
                findNavController().navigate(g.fragmentId)
            }
            val p = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            layout.addView(b, p)
        }
        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_optionGameFragment)
        }
    }
}