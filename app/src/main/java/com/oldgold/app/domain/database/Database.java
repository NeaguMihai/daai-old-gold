package com.oldgold.app.domain.database;

import com.google.firebase.firestore.FirebaseFirestore;

public class Database {

    private static volatile  Database INSTANCE;

    private FirebaseFirestore connection;

    private Database() {
        if (INSTANCE != null) {
            throw new RuntimeException("Reflection is not allowed");
        }

        connection = FirebaseFirestore.getInstance();
    }

    public static Database getInstance() {

        if(INSTANCE == null) {

            synchronized (Database.class) {

                if(INSTANCE == null) INSTANCE = new Database();
            }
        }

        return INSTANCE;
    }

    public FirebaseFirestore getConnection() {
        return connection;
    }
}
