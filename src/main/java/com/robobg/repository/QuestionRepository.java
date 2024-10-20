package com.robobg.repository;

import com.robobg.entity.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question,Long> {
    List<Question> findByRobotId(Long robotId);

    List<Question> findByAuthorId(Long authorId);

    List<Question> findByCreateTimeAfter(LocalDateTime date);

}
