package de.novatec.aqe.repository;

import de.novatec.aqe.model.Project;

import java.util.List;

public interface ProjectRepository {
    List<Project> findAll();
    Project findById(String id);
    void refresh();
}
