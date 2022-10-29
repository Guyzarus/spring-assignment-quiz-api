package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuizMapper quizMapper;
	private final QuestionMapper questionMapper;

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		return quizMapper.entitiesToDtos(quizRepository.findAll());
	}

	@Override
	public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
		Quiz quizToSave = quizMapper.requestDtoToEntity(quizRequestDto);
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToSave));
	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		Quiz quizToDelete = getQuiz(id);
		return quizMapper.entityToDto(quizToDelete);
	}

	private Quiz getQuiz(Long id) {
		Optional<Quiz> optionalQuiz = quizRepository.findById(id);
		if (optionalQuiz.isEmpty()) {
			return null;
		}
		return optionalQuiz.get();
	}

	@Override
	public QuizResponseDto addQuestion(Long id, QuestionRequestDto questionRequestDto) {
		Quiz quizToAddQuestionTo = getQuiz(id);
		List<Question> quizQuestions = quizToAddQuestionTo.getQuestions();
		Question questionToAdd = questionMapper.requestDtoToEntity(questionRequestDto);
		List<Answer> questionAnswers = questionToAdd.getAnswers();
		for (Answer answer : questionAnswers) {
			answer.setQuestion(questionToAdd);
		}
		
		questionToAdd.setQuiz(quizToAddQuestionTo);
		quizToAddQuestionTo.setQuestions(quizQuestions);
				
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToAddQuestionTo));
	}

	@Override
	public QuizResponseDto renameQuiz(Long id, String newNameForQuiz) {
		Quiz quizToUpdate = getQuiz(id);
		if (newNameForQuiz != null) {
			quizToUpdate.setName(newNameForQuiz);
		}
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToUpdate));
	}

}
