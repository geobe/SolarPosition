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

//    SolarGraph(double lat = 50.802368072602334, double lon = 12.956812953316136) {
    SolarGraph(double lat = 50.83600391781902, double lon = 12.923330207171258) {
        latitude = lat
        longitude = lon
    }

    def csvPlot(long year, List<List<Long>> date) {
        StringBuffer values = new StringBuffer()
        StringBuffer hourazi = new StringBuffer()
        StringBuffer hourelv = new StringBuffer()
        for (long min = 180; min < 1260; min += 60) {
            long minh = min % 60
            long h = min / 60
            def time = String.format('%2d:%02d; ', h, minh)
            values.append time
            if (minh == 0) {
                hourazi.append time
                hourelv.append time
            }
            date.each {
                assert it.size() == 2
                def month = it[0]
                def day = it[1]
                def coordinates = solarPosition.solarCoordinates(year, month, day, h, minh, latitude, longitude)
                def azimuth = coordinates.azimuth
                def elevation = coordinates.elevation
                def line = String.format("${elevation >= 0.0 ? '% 7.2f; % 7.2f; ' : '       ;        ; '}", azimuth, elevation)
                values.append line
                if (minh == 0 && elevation >= -4.0) {
                    hourelv.append String.format('% 7.2f; ', elevation)
                    hourazi.append String.format('% 7.2f; ', azimuth)
                }
            }
            values.append '\n'
            if (minh == 0) {
                hourazi.append('\n').append(hourelv).append '\n'
                hourelv = new StringBuffer()
            }
        }
        values.append '\n'
        values.append hourazi.toString()
//        values.append hourelv.toString()
        values.toString()
    }

    static void main(String[] args) {
//        new SolarGraph(SolarPosition.atKarlMarx[0], SolarPosition.atKarlMarx[1]).csvPlot(2021,6, 21)
        def days = [[12, 21], [1, 20], [2, 18], [3, 20], [4, 20], [5, 21], [6, 21]]
        def csv = new SolarGraph(50.83, 0.0).csvPlot(2021, days)
//        print csv
        File csvFile = new File('/home/georg/tmp/tmp/solar.csv')
        csvFile.write csv
        print csvFile.text
    }
}
