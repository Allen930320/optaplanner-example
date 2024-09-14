package com.allen.example.persistence;

import com.allen.example.entity.Task;
import org.springframework.data.repository.Repository;

public interface TaskRepository extends Repository<Task,Long> {

}
