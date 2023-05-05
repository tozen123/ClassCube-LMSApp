package com.doublehammerstudios.classcube;

import java.io.Serializable;

public class QuizQuestionItem {

    public String QuizQuestionCount;
    public String QuizQuestionDesc;
    public String QuizQuestionChoiceA;
    public String QuizQuestionChoiceB;
    public String QuizQuestionChoiceC;
    public String QuizQuestionChoiceD;
    public String QuizQuestionChoiceAnswer;

    public QuizQuestionItem(String QuizQuestionCount,
                            String QuizQuestionDesc,
                            String QuizQuestionChoiceA,
                            String QuizQuestionChoiceB,
                            String QuizQuestionChoiceC,
                            String QuizQuestionChoiceD,
                            String QuizQuestionChoiceAnswer) {

        this.QuizQuestionCount = QuizQuestionCount;
        this.QuizQuestionDesc = QuizQuestionDesc;
        this.QuizQuestionChoiceA = QuizQuestionChoiceA;
        this.QuizQuestionChoiceB = QuizQuestionChoiceB;
        this.QuizQuestionChoiceC = QuizQuestionChoiceC;
        this.QuizQuestionChoiceD = QuizQuestionChoiceD;
        this.QuizQuestionChoiceAnswer = QuizQuestionChoiceAnswer;
    }

    public String getQuizQuestionCount() {
        return QuizQuestionCount;
    }

    public String getQuizQuestionDesc() {
        return QuizQuestionDesc;
    }

    public String getQuizQuestionChoiceA() {
        return QuizQuestionChoiceA;
    }

    public String getQuizQuestionChoiceB() {
        return QuizQuestionChoiceB;
    }

    public String getQuizQuestionChoiceC() {
        return QuizQuestionChoiceC;
    }

    public String getQuizQuestionChoiceD() {
        return QuizQuestionChoiceD;
    }

    public String getQuizQuestionChoiceAnswer() {
        return QuizQuestionChoiceAnswer;
    }
}
