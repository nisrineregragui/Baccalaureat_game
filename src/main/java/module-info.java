module com.example.java_project {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires mysql.connector.j;
    requires jakarta.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires org.json;
    requires java.net.http;
    requires javafx.media;

    opens com.example.java_project to javafx.fxml;

    exports com.example.java_project;

    opens models to org.hibernate.orm.core;

}