module com.example.projet_crypto_v2 {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.logging;
    requires jdk.httpserver;
    requires java.mail;
    requires java.desktop;
    requires jpbc.api;
    requires jpbc.plaf;

    opens com.example.projet_crypto_v2 to javafx.fxml;
    exports com.example.projet_crypto_v2;
    exports com.example.projet_crypto_v2.gui;
}