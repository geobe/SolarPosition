module SolarPosition {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.gsi.chartfx.chart;
    requires org.slf4j.simple;
    
    opens org.openjfx to javafx.fxml;
    exports org.openjfx;
    exports de.geobe.solar;
}
