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
    requires activation;

    opens projet_crypto to javafx.fxml;
    exports projet_crypto;
    exports projet_crypto.gui;
}