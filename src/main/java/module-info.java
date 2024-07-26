module catfan7.catclicker2.catclicker2 {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.controlsfx.controls;

    opens catfan7.catclicker2.catclicker2 to javafx.fxml;
    exports catfan7.catclicker2.catclicker2;
}