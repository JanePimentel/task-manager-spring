package br.com.treinaweb.twtodos.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import br.com.treinaweb.twtodos.model.ToDo;
import br.com.treinaweb.twtodos.repository.ToDoRepository;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
@RequestMapping()
public class TodoController {

    private final ToDoRepository todoRepository;

    public TodoController(ToDoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping("/tw")
    public ModelAndView list(){
        return new ModelAndView("todo/list",
            Map.of("todos",todoRepository.findAll(Sort.by("deadLine"))));
    }

    @GetMapping("/create")
    public ModelAndView create() {
        return new ModelAndView("todo/form",
        Map.of("todo", new ToDo()));
    }

    //BindingResult captura resultado da validacao/verifica se o result tem erros e retorna para o form novamente
    @PostMapping("/create")
    public String create(@Valid @ModelAttribute("todo") ToDo todo, BindingResult result) {  
        if(result.hasErrors()){
            return "todo/form";
        }
        todoRepository.save(todo);      
        return "redirect:/tw";
    }

    @GetMapping("/edit/{id}")
    public ModelAndView edit(@PathVariable Long id){
        var todo = todoRepository.findById(id);
        if(todo.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("todo/form", Map.of("todo",todo.get()));
    }   

    /*@Valid informa que as validacoes dessa classe devem ser aplicadas
    @ModelAttribute para mapear automaticamente as configurações de requisição para as propriedades do objeto
    se o formulário HTML enviar campos com name="title"e name="deadLine", esses valores serão automaticamente associados aos atributos titlee deadLinedo objeto ToDo
    permitindo que ele seja acessado nos templates e formulários renderizados pela aplicação*/
    @PostMapping("/edit/{id}")
    public String edit(@Valid @ModelAttribute("todo") ToDo todo, BindingResult result){
        if(result.hasErrors()){
            return "todo/form";
        }
        todoRepository.save(todo);
        return "redirect:/tw";
    }

    @GetMapping("/delete/{id}")
    public ModelAndView delete(@PathVariable Long id){
        var todo = todoRepository.findById(id);
        if(todo.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return new ModelAndView("todo/delete", Map.of("todo",todo.get()));
    }
    @PostMapping("/delete/{id}")
    public String delete(ToDo todo){
        todoRepository.delete(todo);
        return "redirect:/tw";
    }
    @PostMapping("/finish/{id}")
    public String finish(@PathVariable Long id) {
        var optionalTodo = todoRepository.findById(id);
        if(optionalTodo.isEmpty()){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        var todo = optionalTodo.get();
        todo.markHasFinished();
        todoRepository.save(todo);
        return "redirect:/tw";
    }
    
    /*
     * public ModelAndView home() {
     * var modelAndView = new ModelAndView("todo/list");
        modelAndView.addObject("todos", todoRepository.findAll());
        return modelAndView;
     * var modelAndView = new ModelAndView("home");
     * modelAndView.addObject("nome", "Juca Linz");
     * var alunos = List.of("Test 1", "Joao", "Marcela", "Ana");
     * modelAndView.addObject("alunos",alunos);
     * modelAndView.addObject("ehProgramador",false);
     * return modelAndView;
     * }
     */

}
