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

package de.geobe.solar.fx

import de.geobe.solar.SolarPosition
import de.geobe.solar.XY
import de.gsi.chart.XYChart
import de.gsi.chart.axes.spi.CategoryAxis
import de.gsi.chart.axes.spi.DefaultNumericAxis
import de.gsi.dataset.spi.DoubleDataSet
import de.gsi.dataset.spi.LabelledMarker
import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.control.SplitPane
import javafx.scene.control.Tab
import javafx.scene.control.TabPane
import javafx.scene.layout.Priority
import javafx.scene.layout.VBox
import javafx.scene.text.Font
import javafx.stage.Stage

import java.time.ZoneId

class GroovyChartSample {

    void start(Stage primaryStage) throws Exception {
        def box1 = new VBox();
        def box2 = new VBox();
        def box3 = new VBox();
        def box4 = new VBox();
        def box5 = new VBox();

        def solarPosition = new SolarPosition()
        def cfg = SolarPosition.readConfig('sample.json')
//        Lampertheim
//        def lat = 49.590643
//        def lon = 8.475496
        def lon = cfg.location.lon
        def lat = cfg.location.lat
        def direction = cfg.panel.direction
        def tilt = cfg.panel.inclination
        def year = 2021
        def realNoon = true

        def graphData1 = solarPosition.solarPositionGraph(lat, lon, year, realNoon, tilt, direction)
        def graphData2 = solarPosition.solarPositionGraph(lat, lon, year, !realNoon, tilt, direction)

        def diag1 = makeSolarPositionChart(graphData1)
        def diag2 = makeSolarPositionChart(graphData2)
        def diag3 = makePanelExpositionsChart(graphData2)
        def diag4 = makePanelYieldChart(graphData2)
        def label1 = new Label("Solar Position at ($lat, $lon) showing local solar time")
        def label2 = new Label("Solar Position at ($lat, $lon) showing timezone ${ZoneId.systemDefault()} time")
        def label3 = new Label("Solar Panel Exposition at ($lat, $lon) with tilt $tilt looking at $direction°")
        def label4 = new Label("Solar Panel Yield at ($lat, $lon) with tilt $tilt looking at $direction°")
        def label5 = new Label("Solar Panel Yield at ($lat, $lon) with tilt $tilt looking at $direction°")
        label1.font = label2.font = label3.font = label4.font = label5.font = new Font('Arial', 24)
        VBox.setVgrow(diag1, Priority.ALWAYS)
        VBox.setVgrow(diag2, Priority.ALWAYS)
        VBox.setVgrow(diag3, Priority.ALWAYS)
        VBox.setVgrow(diag4, Priority.ALWAYS)
//        VBox.setVgrow(diag5, Priority.ALWAYS)
        box1.getChildren().addAll(label1, diag1)
        box2.getChildren().addAll(label2, diag2)
        box3.getChildren().addAll(label3, diag3)
        box4.getChildren().addAll(label4, diag4)
        box5.getChildren().addAll(label5)

//        def contentPane = new SplitPane(top, mid, below)
//        contentPane.orientation = Orientation.VERTICAL
//        contentPane.dividerPositions = [0.33, 0.66]
        def contentPane = new TabPane()
        def tab1 = new Tab("Solar Position Solar Time", box1)
        def tab2 = new Tab("Solar Position CE(S)T", box2)
        def tab3 = new Tab("Panel Exposition CE(S)T", box3)
        def tab4 = new Tab("Panel Yield CE(S)T", box4)
        def tab5 = new Tab("Panel Monthly Yield", box5)

        contentPane.getTabs().addAll(tab1, tab2, tab3, tab4, tab5)

        final Scene scene = new Scene(contentPane, 1200, 600);
        primaryStage.setTitle("Solar Position Calculations");
        primaryStage.setScene(scene);
        primaryStage.setOnCloseRequest(evt -> System.exit(0));
        primaryStage.show();
    }

    XYChart makeSolarPositionChart(def graphData) {
        def xAxis = new DefaultNumericAxis('Azimuth', -140, 140, 5)
        def yAxis = new DefaultNumericAxis('Elevation', 0, 70, 5)
        xAxis.unit = '°'
        yAxis.unit = '°'
        final XYChart xyChart = new XYChart(xAxis, yAxis);
        def sunpaths = graphData.sunpaths
        def timedPositions = graphData.timedPositions
        def datasets = []
        sunpaths.each { Object timestamp, List sunpath ->
            def dataset = new DoubleDataSet(timestamp.toString(), sunpath.size())
            sunpath.each { XY value ->
                dataset.add(value.x, value.y)
            }
            datasets << dataset
        }
        timedPositions.each { Object time, List positionAt ->
            def dataset = new DoubleDataSet(time.toString(), positionAt.size())
            positionAt.each { XY value ->
                dataset.add(value.x, value.y)
            }
            def mark = new LabelledMarker(dataset.getX(0), time.toString())
            mark.y = dataset.getY(0)
            dataset.addDataLabel(0, time.toString())
            datasets << dataset
        }
        xyChart.getDatasets().addAll(datasets)
        xyChart
    }

    XYChart makePanelTotalYieldChart(def graphData) {
        def xAxis = new CategoryAxis('Date')
        def yAxis = new DefaultNumericAxis('Relative Yield', 0, 50, 5)
//        yAxis.unit = ''
        final XYChart xyChart = new XYChart(xAxis, yAxis);
        def panelYield = graphData.panelYield
        def dataset = new DoubleDataSet(timestamp.toString(), sunpath.size())
        panelYield.each { XY value ->
            dataset.add(value.x, value.y)
            datasets << dataset
        }
        xyChart.getDatasets().add(dataset)
        xyChart
    }

    XYChart makePanelExpositionsChart(def graphData) {
        def xAxis = new DefaultNumericAxis('Azimuth', -140, 140, 5)
        def yAxis = new DefaultNumericAxis('Elevation', 0, 90, 5)
        xAxis.unit = '°'
        yAxis.unit = '°'
        final XYChart xyChart = new XYChart(xAxis, yAxis);
        def expositions = graphData.panelExpositions
        def datasets = []
        expositions.each { Object timestamp, List sunpath ->
            def dataset = new DoubleDataSet(timestamp.toString(), sunpath.size())
            sunpath.each { XY value ->
                dataset.add(value.x, value.y)
            }
            datasets << dataset
        }
        xyChart.getDatasets().addAll(datasets)
        xyChart
    }

    XYChart makePanelYieldChart(def graphData) {
        def xAxis = new DefaultNumericAxis('Time')//, -140, 140, 5)
        def yAxis = new DefaultNumericAxis('Yield', 0, 1, 5)
        xAxis.unit = 'h'
        yAxis.unit = '%'
        final XYChart xyChart = new XYChart(xAxis, yAxis);
        def yields = graphData.panelYields
        def datasets = []
        yields.each { Object timestamp, List yield ->
            def dataset = new DoubleDataSet(timestamp.toString(), yield.size())
            yield.each { XY value ->
                dataset.add(value.x, value.y)
            }
            datasets << dataset
        }
        xyChart.getDatasets().addAll(datasets)
        xyChart
    }

    static void main(String[] args) {
        println "starting GroovyChartSample.groovy"
        Application.launch(args)
    }
}
