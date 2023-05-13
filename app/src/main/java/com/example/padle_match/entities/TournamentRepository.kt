package com.example.padle_match.entities

import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class TournamentRepository {
    private val tournamentList: MutableList<Tournament> = mutableListOf()

    init{
        tournamentList.add(Tournament("1","Club Chacarita - Torneo 2", "2022-02-03","22:00","C1",5,55.00,"Aplausos","url:Image" ) )
        tournamentList.add(Tournament("2","Club Chacarita - Torneo 2", "2022-02-03","22:00","C2",5,55.00,"Aplausos","url:Image" ) )
        tournamentList.add(Tournament("3","Club Chacarita - Torneo 2", "2022-02-03","22:00","C2",5,55.00,"Aplausos","url:Image" ) )
        tournamentList.add(Tournament("4","Club Chacarita - Torneo 2", "2022-02-03","22:00","C3",5,55.00,"Aplausos","url:Image" ) )
        tournamentList.add(Tournament("5","Club Chacarita - Torneo 2", "2022-02-03","22:00","C4",5,55.00,"Aplausos","url:Image" ) )
    }

    fun getTournaments () : MutableList<Tournament> {
       return tournamentList
    }
}