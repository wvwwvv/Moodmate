package com.example.backend.Repository;

import com.example.backend.Entity.EmotionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmotionMappingRepository extends JpaRepository<EmotionMapping, Long> {
}
