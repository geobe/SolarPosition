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
     * based on two rotations on karthesian axes in a unit ball:
     * <ul>
     *     <li>T1: Transform solar position on the unit ball into karthesian coordinates (x, y, z)</li>
     *     <li>T2: Calculate coordinate transformation (x', y', z') after rotating around the Z-axis</li>
     *     <li>T3: Calculate coordinate transformation (x", y", z") after rotating around Y'-axis,
     *     e.g. the now rotated former east </li>
     *     <li>T3: perpendicular to rotation axis without rotation</li>
     *     <li>T4: perpendicular to rotation axis with rotation of tilt angle</li>
     * </ul>
     * So here is the algorithm: <br>
     * calculate cartesian coordinates on unit ball <br>
     * x -> south, y -> east, z -> zenith <br>
     * def x0 = cos(eps) * cos(azimuth) <br>
     * def y0 = -cos(eps) * sin(azimuth) <br>
     * def z0 = sin(eps) <br>
     * rotate direction angle around z axis <br>
     * def x1 = x0 * cos(direction) - y0 * sin(direction) <br>
     * def y1 = x0 * sin(direction) + y0 * cos(direction) <br>
     * def z1 = z0 <br>
     * rotate tilt angle around new y1 axis - we are only interested in z" <br>
     * def x2 = x1 * cos(tilt) + z1 * sin(tilt) <br>
     * def y2 = y1 <br>
     * def z2 = x1 * sin(tilt) + z1 * cos(tilt) <br>
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
        // bringing all transformations into one line of code
        def z2 = (cos(eps) * cos(azimuth) * cos(direction) + cos(eps) * sin(azimuth) * sin(direction)) * sin(tilt) +
                sin(eps) * cos(tilt)
        asin z2
    }

    static fp3(def val) {
        String.format('% .2f', val)
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
            def eff = cos(PI / 2.0 - releps)
            println "At $hour.00: Azimuth = ${fp3 ll.azimuth}°, Elevation = ${fp3 ll.elevation}°," +
                    " relative Elevation = ${fp3 epsMarx}°, efficiency = ${fp3(eff)}"
        }
    }
}
