/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.eclipse.scava.platform.jadolint.model;

import org.eclipse.scava.platform.jadolint.dependencies.Dependency;
import org.eclipse.scava.platform.jadolint.violations.Violation;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author blue
 */
public class Dockerfile {
    
    private String path;
    private List<Line> lines = new ArrayList<>();
    private List<Violation> violations = new ArrayList<>();
    private List<Dependency> dependencies = new ArrayList<>();
    
    public void addLine(Line line){
        lines.add(line);
    }
    
    public void addViolations(List<Violation> violations){
        this.violations.addAll(violations);
    }
    
    public void addDependencies(List<Dependency> dependencies){
        this.dependencies.addAll(dependencies);
    }

    public List<Line> getLines() {
        return lines;
    }

    public List<Violation> getViolations() {
        return violations;
    }
    
    public List<Dependency> getDependencies() {
        return dependencies;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
