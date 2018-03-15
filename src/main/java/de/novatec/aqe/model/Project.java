package de.novatec.aqe.model;

import lombok.Data;

import java.util.List;
@Data
public class Project {
    public String id = null;
    public String name = null;
    public String organization  = null;
    public String repository = null;
    public String documentation = null;
    public List<String> branches = null;
    public List<String> services = null;
    public List<Artifact> artifacts = null;
    public Boolean legacy = false;

}
