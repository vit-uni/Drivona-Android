package com.drivona.speed.utils

class MushedStateManagement {
    var hasmap = HashMap<Int, Int>()

    fun saveMushedState(userId: Int, state: Int) {
        hasmap[userId] = state
    }

    fun getMushedState(userId: Int): Int? {
        return hasmap.get(userId)
    }

    fun clear() {
        hasmap.clear()
    }
}