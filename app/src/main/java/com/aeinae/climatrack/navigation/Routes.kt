package com.aeinae.climatrack.navigation

object Routes {
    const val MAP_PICKER = "map_picker"
    const val ADD_FAVOURITE = "add_favourite"
    const val FAVOURITE_DETAIL = "favourite/{id}"

    fun favouriteDetail(id: Int): String = "favourite/$id"
}