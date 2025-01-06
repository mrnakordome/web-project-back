package com.example.barbod.repository;

import com.example.barbod.model.Question;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

    // Find all questions whose IDs are not in the provided list
    List<Question> findByIdNotIn(List<String> ids);

    // Find all questions within a specific category and not in the provided list of IDs
    List<Question> findByCategoryIdAndIdNotIn(String categoryId, List<String> ids);
}
