package de.geobe.solar

/**
 * Utility class to calculate Julian Date. If no hour, minute and second are given,
 * Julian Day Number JND is returned. Algorithm based on Wikipedia article,
 * see <a href="https://de.wikipedia.org/wiki/Julianisches_Datum">
 *     Julianisches_Datum (German)</a> or
 * see <a href="https://en.wikipedia.org/wiki/Julian_day">Julian Day</a>
 */
class JulianDay {
    /**
     * calculate Julian Date or Julian Day number for time 00:00:00
     * @param year
     * @param month from 1 to 12
     * @param day from 1 to length of month
     * @param hour 0 .. 23
     * @param minute 0 .. 59
     * @param second 0 .. 59
     * @param gregorian if true, use Gregorian calendar, else Julian
     * @return Julian Date a a double value
     */
    def jD(long year, long month, long day,
           long hour = 0, long minute = 0, long second = 0, boolean gregorian = true) {
        if (month <= 2) {
            month += 12
            year -= 1
        }
        def partialDay = (second + minute * 60 + hour * 3600) / 86400.0
        partialDay += day
        double b = 0
        if (gregorian) {
            b = 2.0 - Math.floorDiv(year, 100) + Math.floorDiv(year, 400)
        }
        def jd = Math.floor(365.25 * (year + 4716)) + Math.floor(30.6001 * (month + 1)) + partialDay + b - 1524.5
        jd
    }
}
