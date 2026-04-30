package models;

import jakarta.persistence.*;

@Entity
@Table(name = "validated_words")
public class Word {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String value;

    private char letter;

    @ManyToOne
    @JoinColumn(name = "id_category")
    private Category category;

    private boolean is_valid;

    public Word() {}

    public Word(String value, char letter, Category category) {
        this.value = value.toLowerCase().trim();
        this.letter = letter;
        this.category = category;
        this.is_valid = false;
    }

    public boolean starts_with_the_right_letter(String firstLetter){
        return !value.isEmpty() && value.toLowerCase().startsWith(firstLetter.toLowerCase());
    }
}