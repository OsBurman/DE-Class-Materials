package com.academy.ai.dto;

import java.util.Map;

/**
 * Represents a single quiz question returned from the AI.
 * This record is COMPLETE.
 */
public record QuizQuestion(
    String question,
    Map<String, String> options,
    String correctAnswer,
    String explanation
) {}
