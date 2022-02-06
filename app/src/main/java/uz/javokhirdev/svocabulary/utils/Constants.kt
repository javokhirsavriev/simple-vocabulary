package uz.javokhirdev.svocabulary.utils

import android.os.Build

/* Base constants */
const val MATCH_PARENT = -1
const val WRAP_CONTENT = -2

/* Build versions */
fun isMarshmallowPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
fun isNougatPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N
fun isNougatMRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1
fun isOreoPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O
fun isRPlus() = Build.VERSION.SDK_INT >= Build.VERSION_CODES.R