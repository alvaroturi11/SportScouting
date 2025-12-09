package org.wit.sportscouting.models

import kotlinx.serialization.Serializable

@Serializable
data class SportScoutingModel(var player: String = "",
                              var team: String = "",
                              var position: String = "",
                              var ownerEmail: String = "",
                              var image: String = "")