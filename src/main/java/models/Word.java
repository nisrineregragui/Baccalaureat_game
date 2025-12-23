package models;

import java.util.Locale;

public class Word {
    private int id;
    private String value;
    private char letter;
    private int id_category;
    private boolean is_valid;

    public Word(String value, char letter, int id_category) {
        this.value = value.toLowerCase().trim();
        this.letter = letter;
        this.id_category = id_category;
        this.is_valid = false;
    }
     public boolean starts_with_the_right_letter(String word){
        return !value.isEmpty() && value.toLowerCase().startsWith(word.toLowerCase());
     }


}
