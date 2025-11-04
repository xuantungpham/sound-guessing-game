package com.hoacx.game.service;

import com.hoacx.game.model.Question;
import com.hoacx.game.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class GameLogicService {

    private final QuestionRepository questionRepository;
    private final Random random = new Random();

    @Autowired
    public GameLogicService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }


    public Question getRandomQuestion() {
        List<Question> questions = questionRepository.findAll();
        if (questions.isEmpty()) return null;
        return questions.get(random.nextInt(questions.size()));
    }


    public boolean checkAnswer(Question question, char selectedOption) {
        return question.getCorrectOption() == selectedOption;
    }
}
