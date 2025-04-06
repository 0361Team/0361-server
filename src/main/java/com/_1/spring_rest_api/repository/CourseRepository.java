package com._1.spring_rest_api.repository;

import com._1.spring_rest_api.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("select c from Course c" +
            " join fetch c.creator" +
            " where c.creator.id = :userId")
    List<Course> findCoursesByUserId(@Param("userId") Long userId);

}
