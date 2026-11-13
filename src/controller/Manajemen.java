package controller;

public class Manajemen {
    private static Manajemen instance;
    private Manajemen() {}
    
    public static Manajemen getInstance() {
        if (instance == null) {
            instance = new Manajemen();
        }
        return instance;
    }
}