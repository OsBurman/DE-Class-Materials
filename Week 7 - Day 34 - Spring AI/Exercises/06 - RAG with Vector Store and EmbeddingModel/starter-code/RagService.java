package com.springai;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class RagService {

    /**
     * Answers a question using Retrieval-Augmented Generation.
     *
     * @param chatModel the model to generate the final answer
     * @param store     the populated vector store to search
     * @param question  the user's natural language question
     * @return the model's answer, grounded in retrieved context
     */
    public String answer(ChatModel chatModel, SimpleVectorStore store, String question) {

        // TODO 3a: Retrieve relevant documents
        //          List<Document> relevant = store.similaritySearch(question);

        // TODO 3b: Concatenate document text with "\n" as separator
        //          String context = relevant.stream()
        //                                   .map(Document::getText)
        //                                   .collect(Collectors.joining("\n"));

        // TODO 3c: Build the augmented prompt string:
        //          "Context:\n" + context + "\n\nQuestion: " + question

        // TODO 3d: Call chatModel.call(new Prompt(new UserMessage(augmentedPrompt)))
        //          and return the content string from the response

        return null; // replace with your implementation
    }
}
