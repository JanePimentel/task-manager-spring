package br.com.treinaweb.twtodos.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import br.com.treinaweb.twtodos.model.ToDo;

public interface ToDoRepository extends JpaRepository<ToDo,Long>{
    
}
