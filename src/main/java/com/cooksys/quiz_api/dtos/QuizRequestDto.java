package com.cooksys.quiz_api.dtos;

import java.util.List;

import com.cooksys.quiz_api.entities.Question;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class QuizRequestDto {

	private Long id;
	
	private String name;

	private List<Question> questions;

}
