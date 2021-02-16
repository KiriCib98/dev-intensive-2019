package ru.skillbranch.devintensive.extensions

import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

const val SECOND = 1000L
const val MINUTE = 60 * SECOND
const val HOUR = 60 * MINUTE
const val DAY = 24 * HOUR

fun Date.format(pattern: String = "HH:mm:ss dd.MM.yy", locale: Locale = Locale("ru")): String {
    val dateFormat = SimpleDateFormat(pattern, locale)
    return dateFormat.format(this)
}

fun Date.add(value: Int, units: TimeUnits): Date {
    var time = this.time

    time += when (units) {
        TimeUnits.SECOND -> value * SECOND
        TimeUnits.MINUTE -> value * MINUTE
        TimeUnits.HOUR -> value * HOUR
        TimeUnits.DAY -> value * DAY
    }

    this.time = time
    return this
}

fun Date.humanizeDiff(date: Date = Date()): String {
    val time = this.time
    val timeNow = date.time
    val diff = time - timeNow
    val diffAbs = abs(diff)
    val prefix = if (diff > 0) "через " else ""
    val postfix = if (diff < 0) " назад" else ""

    var result = ""
    when {
        diffAbs in 0..SECOND -> {
            result = "только что"
        }
        (diffAbs > SECOND && diffAbs <= 45 * SECOND) -> {
            result = "${prefix}несколько секунд$postfix"
        }
        (diffAbs > 45 * SECOND && diffAbs <= 75 * SECOND) -> {
            result = "${prefix}минуту$postfix"
        }
        (diffAbs > 75 * SECOND && diffAbs <= 45 * MINUTE) -> {
            val count = (diffAbs / MINUTE).toInt()
            result = "$prefix${TimeUnits.MINUTE.plural(count)}$postfix"
        }
        (diffAbs > 45 * MINUTE && diffAbs <= 75 * MINUTE) -> {
            result = "${prefix}час$postfix"
        }
        (diffAbs > 75 * MINUTE && diffAbs <= 22 * HOUR) -> {
            val count = (diffAbs / HOUR).toInt()
            result = "$prefix${TimeUnits.HOUR.plural(count)}$postfix"
        }
        (diffAbs > 22 * HOUR && diffAbs <= 26 * HOUR) -> {
            result = "${prefix}день$postfix"
        }
        (diffAbs > 26 * HOUR && diffAbs <= 360 * DAY) -> {
            val count = (diffAbs / DAY).toInt()
            result = "$prefix${TimeUnits.DAY.plural(count)}$postfix"
        }
        diffAbs > 360 * DAY -> {
            result = if (diff > 0) "более чем через год" else "более года назад"
        }
    }

    return result
}

fun Date.shortFormat(): String? {
    val pattern = if (this.isSameDay(Date())) "HH:mm" else "dd.MM.yy"
    val dateFormat = SimpleDateFormat(pattern, Locale("ru"))
    return dateFormat.format(this)
}

fun Date.isSameDay(date: Date): Boolean {
    val day1 = this.time / DAY
    val day2 = date.time / DAY
    return day1 == day2
}

enum class TimeUnits {

    SECOND,
    MINUTE,
    HOUR,
    DAY;

    fun plural(value: Int): String {
        val pluralList: List<Map<TimeUnits, String>> = listOf(
            mapOf(SECOND to "секунд", MINUTE to "минут", HOUR to "часов", DAY to "дней"),
            mapOf(SECOND to "секунду", MINUTE to "минуту", HOUR to "час", DAY to "день"),
            mapOf(SECOND to "секунды", MINUTE to "минуты", HOUR to "часа", DAY to "дня")
        )
        val v =
            if (value % 10 == 0 || (value % 10 in 5..9) || (value % 100 in 11..19)) 0 else if (value % 10 == 1) 1 else 2
        //if (value % 10 == 0 || (value % 10 in 5..9)) 0 else if (value % 10 in 2..4) 2 else 1
        return "$value ${pluralList[v][this].toString()}"
    }
}
