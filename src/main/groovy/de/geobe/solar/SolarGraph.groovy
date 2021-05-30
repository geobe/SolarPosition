/*
 * MIT License
 *
 * Copyright (c) 2021  Georg Beier
 *                            Permission is hereby granted, free of charge, to any person obtaining a copy
 *                            of this software and associated documentation files (the "Software"), to deal
 *                            in the Software without restriction, including without limitation the rights
 *                            to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *                            copies of the Software, and to permit persons to whom the Software is
 *                            furnished to do so, subject to the following conditions:
 *
 *                            The above copyright notice and this permission notice shall be included in all
 *                            copies or substantial portions of the Software.
 *
 *                            THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *                            IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *                            FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *                            AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *                            LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *                            OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *                            SOFTWARE.
 */

package de.geobe.solar

class SolarGraph {

    double latitude
    double longitude
    SolarPosition solarPosition = new SolarPosition()

    SolarGraph(double lat = 50.802368072602334, double lon = 12.956812953316136) {
        latitude = lat
        longitude = lon
    }

    def csvPlot(long year, long month, long day){
        StringBuffer values = new StringBuffer()
        for (long min = 60; min < 1440; min += 15){
            long minh = min % 60
            long h = min / 60
            def coordinates = solarPosition.solarCoordinates(year, month, day, h, minh, latitude, longitude)
            def azimuth = coordinates.azimuth
            def elevation = coordinates.elevation
//            values.add([h, minh, azimuth, elevation])
            def line = String.format('%2d:%02d; % 6.2f; % 6.2f%n', h, minh, azimuth, elevation)
//            print line //"$h, $minh, $azimuth, $elevation"
            values.append line
        }
        values.toString()
    }

    static void main(String[] args) {
//        new SolarGraph(SolarPosition.atKarlMarx[0], SolarPosition.atKarlMarx[1]).csvPlot(2021,6, 21)
        def csv = new SolarGraph(49.0, 0.0).csvPlot(2021,3, 20)
//        print csv
        File csvFile = new File('/home/georg/tmp/tmp/solar.csv')
        csvFile.write csv
        print csvFile.text
    }
}
