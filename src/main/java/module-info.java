module com.example.messengerserver {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.controller to javafx.fxml;
    exports com.example.controller;
    exports Networking;
    opens Networking to javafx.fxml;
}