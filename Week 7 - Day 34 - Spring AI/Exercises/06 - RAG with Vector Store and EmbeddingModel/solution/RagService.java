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

    public String answer(ChatModel chatModel, SimpleVectorStore store, String question) {
        List<Document> relevant = store.similaritySearch(question);

        String context = relevant.stream()
                .map(Document::getText)
                .collect(Collectors.joining("\n"));

        String augmentedPrompt = "Context:\n" + context + "\n\nQuestion: " + question;

        return chatModel.call(new Prompt(new UserMessage(augmentedPrompt)))
                .getResult()
                .getOutput()
                .getText();
    }
}
