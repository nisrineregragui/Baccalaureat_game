package services;

import DAO.CategoryDAO;
import DAO.WordDAO;
import models.Category;
import models.Word;

public class ValidationService {
    private WordDAO wordDAO;
    private APIService apiService;

    public ValidationService() {
        this.wordDAO = new WordDAO();
        this.apiService = new APIService();
    }

    public boolean word_validation(String text, char letter, int category_id) {
        if (text == null || text.isEmpty() || Character.toUpperCase(text.charAt(0)) != Character.toUpperCase(letter)) {
            return false;
        }

        if (wordDAO.exists(text, category_id)) {
            return true;
        }
        CategoryDAO catDAO = new CategoryDAO();
        Category cat = catDAO.getCategory(category_id);

        if (cat == null) return false;

        if (apiService.verify_word(text, cat.getName(), letter)) {
            Word newWord = new Word(text, letter, cat);
            wordDAO.save(newWord);
            return true;
        }

        return false;
    }
}

