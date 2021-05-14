package de.geobe.solar

import spock.lang.Specification

class JulianDaySpec extends Specification {

    def "comparing the list of Julian Days from Wikipedia"() {
        given:
        def julianDay = new JulianDay()
        when:
        double jd0 = julianDay.jD(-4712, 1, 1, 12, 0, 0, false)
        double jd1 = julianDay.jD(-668, 5, 27, 1, 59, 0, false)
        double jd2 = julianDay.jD(1, 1, 1, 0, 0, 0, false)
        double jd3 = julianDay.jD(763, 9, 14, 12, 0, 0, false)
        double jd4 = julianDay.jD(1582, 10, 4, 24, 0, 0, false)
        double jd5 = julianDay.jD(1582, 10, 15)
        double jd6 = julianDay.jD(1858, 11, 17)
        double jd7 = julianDay.jD(1899, 12, 31, 19, 31, 28)
        double jd8 = julianDay.jD(2000, 1, 1, 12)
        double jd9 = julianDay.jD(2021, 5, 12, 6, 27)
        then:
        Math.abs(jd0 - 0.0) < 0.001
        Math.abs(jd1 - 1477217.583) < 0.001
        Math.abs(jd2 - 1721423.500) < 0.00001
        Math.abs(jd3 - 2000000.000) < 0.00001
        Math.abs(jd4 - 2299160.500) < 0.00001
        Math.abs(jd5 - 2299160.500) < 0.00001
        Math.abs(jd6 - 2400000.500) < 0.00001
        Math.abs(jd7 - 2415020.31352) < 0.00001
        Math.abs(jd8 - 2451545.0) < 0.00001
        Math.abs(jd9 - 2459346.769) < 0.001
    }
}