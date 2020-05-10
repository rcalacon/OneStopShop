package com.bignerdranch.android.criminalintent

import java.util.*

data class Crime (
    var id: String = "",
    var title: String = "",
    var date: Date = Date(),
    var isSolved: Boolean = false
)