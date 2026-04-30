package services;

import DAO.WordDAO;
import models.Word;

public class ValidationService {
    private WordDAO wordDAO;
    private APIService apiService;

    public boolean word_validation(String text, char letter, int category_id) {
        if (wordDAO.exists(text, category_id)) {
            return true;
        } else {
            if (apiService.verify_word(text, category_id)) {

                wordDAO.save(text, category_id);

            }
            return true;

        }
    }
}

