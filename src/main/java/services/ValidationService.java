package services;

import DAO.CategoryDAO;
import DAO.WordDAO;
import models.Category;
import models.Word;

public class ValidationService {
    private WordDAO wordDAO;
    private APIService apiService;

    public boolean word_validation(String text, char letter, int category_id) {
        if (wordDAO.exists(text, category_id)) {
            return true;
        } else {
            if (apiService.verify_word(text, category_id)) {
                CategoryDAO categoryDAO = new CategoryDAO();
                Category cat = categoryDAO.getCategory(category_id);
                Word newWord = new Word(text, letter, cat);
                wordDAO.save(newWord);

            }
            return true;

        }
    }
}

