package de.novatec.aqe.controller;


import de.novatec.aqe.model.Project;
import de.novatec.aqe.repository.GitProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@RestController
@EnableWebMvc
public class PingController {

    @Autowired
    GitProjectRepository repository;

    @RequestMapping(path = "/ping", method = RequestMethod.GET)
    public Map<String, String> ping() {
        Map<String, String> pong = new HashMap<>();
        pong.put("pong", "Hello, AQE!");
        return pong;
    }


    @RequestMapping(path = "/index/{project}", method = RequestMethod.GET,  produces = MediaType.TEXT_HTML_VALUE)
    public String ping(@PathVariable("project") String projectId ) {
        Project project = repository.findById(projectId);
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateEngine.setTemplateResolver(templateResolver);
        StringWriter stringWriter = new StringWriter();
        templateEngine.process("templates/project.html", getProjectCtx(project), stringWriter);

        return stringWriter.toString();
    }


    @RequestMapping(path = "/index", method = RequestMethod.GET, produces = MediaType.TEXT_HTML_VALUE)
    public String index() {
        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver templateResolver = new ClassLoaderTemplateResolver();
        templateResolver.setTemplateMode("HTML5");
        templateEngine.setTemplateResolver(templateResolver);
        StringWriter stringWriter = new StringWriter();
        templateEngine.process("templates/index.html", getIndexCtx(), stringWriter);
        return stringWriter.toString();
    }




    private Context getProjectCtx(Project project){

        Context context = new Context();
        context.setVariable("project", project);
        context.setVariable("render_documentation", project.getDocumentation());
        context.setVariable("render_ci", !project.getBranches().isEmpty());
        context.setVariable("render_artifacts", !project.getArtifacts().isEmpty());
        context.setVariable("render_travis", project.getServices().contains("travis"));
        context.setVariable("render_codecov", project.getServices().contains("codecov"));
        context.setVariable("render_bettercode", project.getServices().contains("bettercode"));
        return context;

    }

    private Context getIndexCtx(){

        Context context = new Context();
        context.setVariable("projects", findAllProjects() );
        context.setVariable("legacyProjects", findAllLegacyProjects() );

        return context;
    }

    private List<Project> findAllLegacyProjects(){
        return repository.findAll().stream().filter(p -> p.legacy).collect(Collectors.toList());
    }

    private List<Project> findAllProjects(){
        return repository.findAll().stream().filter(p -> !p.legacy).collect(Collectors.toList());
    }

}
