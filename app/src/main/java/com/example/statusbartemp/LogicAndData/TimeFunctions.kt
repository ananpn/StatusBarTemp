package com.example.statusbartemp.LogicAndData

import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit.SECONDS

class TimeFunctions {
    companion object {
        //takes format eg "hhmmddMMyyyy" or "yyyy" and returns the current time in this format
        fun getTimeNow(
            format : String,
        ) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val current = LocalDateTime.now().format(formatter)
            return current
        }

        fun getISOTimeWithMinuteOffset(
            minutesOffset : Long = 0
        ) : String {
            val current = Instant.now().truncatedTo(SECONDS).plusSeconds(60*minutesOffset).toString()//LocalDateTime.now().plusMinutes(minutesOffset).format(DateTimeFormatter.ISO_INSTANT)
            return current
        }
        
        fun getISOTimeWithSecondOffset(
            secondsOffset : Long = 0
        ) : String {
            val current = Instant.now().truncatedTo(SECONDS).plusSeconds(secondsOffset).toString()//LocalDateTime.now().plusMinutes(minutesOffset).format(DateTimeFormatter.ISO_INSTANT)
            return current
        }

        fun formatISOTimeToFinnishTime(input : String, seconds : Boolean = true) : String {
            try{
                //println(input)
                //val raw = input.dropLast(1).replaceFirst("T", " ")
                //val ISORawFormatter = DateTimeFormatter.ISO_INSTANT
                val rawDate = Instant.parse(input.trim()).atZone(ZoneId.systemDefault())
                val pattern = when(seconds){
                    true -> "HH:mm:ss, dd.MM.yyyy"
                    false -> "HH:mm, dd.MM.yyyy"
                }
                val formatter = DateTimeFormatter.ofPattern(pattern)
                return rawDate.format(formatter)
            }
            catch (e : Exception) {
                //println(e)
                return ""
            }
        }

        fun formatToString(date : LocalDate, format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = date.format(formatter)
            return output
        }
        
        fun formatLocalDateTimeToString(time : LocalDateTime, format : String) : String {
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = time.format(formatter)
            return output
        }
        

        fun formatToLocalDate(time : String, format : String) : LocalDateTime {
            //Log.v("fTLC time format", time+" "+format)
            val formatter = DateTimeFormatter.ofPattern(format)
            val output = LocalDateTime.parse(time, formatter)
            return output
        }
    }
}