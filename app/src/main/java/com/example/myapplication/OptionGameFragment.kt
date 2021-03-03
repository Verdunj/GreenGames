package com.example.myapplication

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController

class OptionGameFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.option_game_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        /* Recuperation de l'ID jeu et set les options dispo */
        val model= ViewModelProviders.of(requireActivity()).get(Communicator::class.java)
        model.message.observe(viewLifecycleOwner, Observer<Any> { t ->
            println("at end ${t.toString()}")
            val gameId: Int = t.toString().toInt()
            println("gameid = $gameId")

            /* set le nom du jeu */
            val game = GameData.core.findGameById(t.toString().toInt())
            view.findViewById<TextView>(R.id.gameName).text = game!!.name


            if((gameId == 1) or (gameId ==  5) or (gameId === 6)) {
                println("dans if 1")
                view.findViewById<Button>(R.id.button_multi).visibility = View.GONE
            }
            if(gameId == 7){
                println("dans if 7")
                view.findViewById<Button>(R.id.button_niveau).text = "Theme"
            }

            val toast = Toast.makeText(context, "Jeu pas encore implémenté", Toast.LENGTH_LONG)
            view.findViewById<Button>(R.id.button_reprendre).setOnClickListener {
                findNavController().navigate(game.fragmentId)
            }
            view.findViewById<Button>(R.id.button_niveau).setOnClickListener {
                toast.show()
            }
            view.findViewById<Button>(R.id.button_multi).setOnClickListener {
                toast.show()
            }
            view.findViewById<Button>(R.id.button_personnaliser).setOnClickListener {
                toast.show()
            }
        })

        view.findViewById<Button>(R.id.menu_button).setOnClickListener {
            findNavController().navigate(R.id.action_optionGameFragment_to_FirstFragment)
        }
    }

}