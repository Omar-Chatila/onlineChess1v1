module com.example.messengerserver {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires com.jfoenix;
    requires fontawesomefx;
    requires javafx.web;


    opens com.example.controller to javafx.fxml;
    exports com.example.controller;
    exports Networking;
    opens Networking to javafx.fxml;
}