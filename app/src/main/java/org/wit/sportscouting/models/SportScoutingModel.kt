package org.wit.sportscouting.models

import kotlinx.serialization.Serializable

@Serializable
data class SportScoutingModel(var title: String = "",
                              var description: String = "",
                              var position: String = "")