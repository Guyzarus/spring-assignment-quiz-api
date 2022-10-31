package com.cooksys.quiz_api.services.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.quiz_api.dtos.QuestionRequestDto;
import com.cooksys.quiz_api.dtos.QuestionResponseDto;
import com.cooksys.quiz_api.dtos.QuizRequestDto;
import com.cooksys.quiz_api.dtos.QuizResponseDto;
import com.cooksys.quiz_api.entities.Answer;
import com.cooksys.quiz_api.entities.Question;
import com.cooksys.quiz_api.entities.Quiz;
import com.cooksys.quiz_api.mappers.QuestionMapper;
import com.cooksys.quiz_api.mappers.QuizMapper;
import com.cooksys.quiz_api.repositories.QuestionRepository;
import com.cooksys.quiz_api.repositories.QuizRepository;
import com.cooksys.quiz_api.services.QuizService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuizServiceImpl implements QuizService {

	private final QuizRepository quizRepository;
	private final QuizMapper quizMapper;
	private final QuestionMapper questionMapper;
	private final QuestionRepository questionRepository;

	@Override
	public List<QuizResponseDto> getAllQuizzes() {
		return quizMapper.entitiesToDtos(quizRepository.findAll());
	}

	@Override
	public QuizResponseDto createQuiz(QuizRequestDto quizRequestDto) {
		Quiz quizToSave = quizMapper.requestDtoToEntity(quizRequestDto);
		for (Question q : quizToSave.getQuestions()) {
			q.setQuiz(quizToSave);
			for (Answer a : q.getAnswers()) {
				a.setQuestion(q);
			}
		}
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToSave));
	}

	@Override
	public QuizResponseDto deleteQuiz(Long id) {
		Quiz quizToDelete = getQuiz(id);
		quizRepository.delete(quizToDelete);
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
		quizQuestions.add(questionToAdd);

		for (Answer answer : questionToAdd.getAnswers()) {
			answer.setQuestion(questionToAdd);
		}

		questionToAdd.setQuiz(quizToAddQuestionTo);
		quizToAddQuestionTo.setQuestions(quizQuestions);

		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToAddQuestionTo));
	}

	@Override
	public QuizResponseDto renameQuiz(Long id, String newNameForQuiz) {
		Quiz quizToUpdate = getQuiz(id);
		quizToUpdate.setName(newNameForQuiz);
		return quizMapper.entityToDto(quizRepository.saveAndFlush(quizToUpdate));
	}

	@Override
	public QuestionResponseDto deleteQuestion(Long id, Long questionId) {
		Quiz quizToDeleteFrom = getQuiz(id);
		Question questionToFind = questionRepository.getById(questionId);
		quizToDeleteFrom.getQuestions().remove(questionToFind);
		questionRepository.delete(questionToFind);

		return questionMapper.entityToDto(questionToFind);
	}

	@Override
	public QuestionResponseDto getRandomQuestion(Long id) {
		Quiz quizToGrabQuestionFrom = getQuiz(id);
		List<Question> listOfQuestions = quizToGrabQuestionFrom.getQuestions();
		int rng = (int)Math.floor(Math.random() * listOfQuestions.size());
		
		return questionMapper.entityToDto(listOfQuestions.get(rng));
	}

}
