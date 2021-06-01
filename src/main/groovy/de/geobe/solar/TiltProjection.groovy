/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2021.  Georg Beier. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package de.geobe.solar

import static java.lang.Math.*

/**
 * Created by georg beier on ${Date}*
 */
class TiltProjection {

    /**
     * Calculate elevation of the sun relative to a tilted and rotated plane (i.e. photovoltaic device),
     * based on four right-angled triangles in a unit ball:
     * <ul>
     *     <li>T1: perpendicular to unit plane, center to base point of sun position on the unit ball</li>
     *     <li>T2: in unit plane, center along rotation axis, right angle to azimuth direction </li>
     *     <li>T3: perpendicular to rotation axis without rotation</li>
     *     <li>T4: perpendicular to rotation axis with rotation of tilt angle</li>
     * </ul>
     * T1 is defined by elevation angle eps with side lengths 1, cos(eps) in plane and sin(eps) perpendicular. <br>
     * T2 is given by angle alpha between azimuth and rotation axis, hypothenuse as adjacent leg (cos(eps)) of T1
     * and right angle. So its opposite leg is sin(alpha) * cos(eps).<br>
     * T3 lies in the circle on the surface of the unit ball that is perpendicular to the rotation axis and contains
     * the point representing the sun position. Its hypothenuse is the radius of this circle, given by
     * rRot = sqrt((sin(alpha)*cos(eps))**2 + sin(eps)**2). Its angle phi at the rotation axis can be determined
     * by atan(sin(eps) / (sin(alpha) * cos(eps)).<br>
     * T4 lies in the same Thales circle as T3, but with center angle phi + tilt. So its opposite leg is
     * hRot = rRot * sin(phi + tilt). This is used to calculate tilted elevation.<br>
     * All angles are in radians
     * @param tilt angle of the plane relative to horizontal
     * @param direction angle of the plane relative to geographic south, eastward is negative
     * @param azimuth angle of the sun relative to geographic south
     * @param eps elevation angle of the sun
     * @return elevation angle relative to tilted plane
     */
    def relativeElevation(double tilt, double direction, double azimuth, double eps) {
        if (azimuth > PI) {
            azimuth -= 2 * PI
        }
        // problem is symmetric, so transform to positive angles
//        azimuth = abs(azimuth)
        // plane angle perpendicular to direction is axis of rotation of the unit ball
        def axis = PI + direction
        // angle between rotation axis and azimuth
        def alpha = axis - azimuth
        // T1 triangle adjacent and opposite leg
        def t1adj = cos(eps)
        def t1opp = sin(eps)
        // T2 triangle opposite leg
        def t2opp = abs(sin(alpha) * t1adj)
        // T3 triangle hypothenuse and angle at axis
//        def t3hyp2 = sqrt(t2opp**2 + t1opp**2)
        def phi = atan(t1opp / t2opp)
        def t3hyp = abs(t1opp / sin(phi))
//        println "T3 Hypothenuse from Pythagoras = $t3hyp2, from sin = $t3hyp, diff = ${t3hyp - t3hyp2}"
        // T4 triangle opposite leg
        def t4opp = t3hyp * sin(phi + tilt)
//        print "T1: $t1opp, $t1adj, 1, ${toDegrees(eps)}°; T2: ?, $t2opp, $t1adj, ${toDegrees(alpha)}°; "
        println "T3: $t1opp, $t2opp, $t3hyp, ${toDegrees(phi)}°; T4: $t4opp, ?, ?, ${toDegrees(asin(t4opp))}°"
        return asin(t4opp)
    }

    static void main(String[] args) {
        def solpos = new SolarPosition()
        def cfg = solpos.readConfig('sample.json')
        println "analyzing "
        def lat = cfg.location.lat
        def lon = cfg.location.lon
        def tilt = cfg.panel.inclination
        def tiltrad = toRadians(tilt)
        def kmdeg = cfg.panel.direction - 180
        def kmrad = toRadians(kmdeg)
        println "${cfg.name} looking to ${kmdeg + 180}° with tilt $tilt°"
        def tiltProjection = new TiltProjection()
        // relative elevation of sun on Karl Marx head on 21.06.2021 from 5:00 to 21:00
        for (long hour = 5; hour <= 21; hour++) {
            def ll = solpos.solarCoordinates(2021, 6, 21, hour - 2, 0, lat, lon)
            def azimuth = ll.azimuthRad
            def eps = ll.elevationRad
            def releps = tiltProjection.relativeElevation(tiltrad, kmrad, azimuth, eps)
            def epsMarx = toDegrees(releps)
            println "At $hour.00: Azimuth = ${ll.azimuth}°, Elevation = ${ll.elevation}°, relative Elevation = $epsMarx°"
        }
    }
}
