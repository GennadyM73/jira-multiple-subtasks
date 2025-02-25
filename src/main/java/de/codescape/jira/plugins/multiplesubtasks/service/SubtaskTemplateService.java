package de.codescape.jira.plugins.multiplesubtasks.service;

import com.atlassian.activeobjects.external.ActiveObjects;
import com.atlassian.activeobjects.tx.Transactional;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import de.codescape.jira.plugins.multiplesubtasks.ao.SubtaskTemplate;
import de.codescape.jira.plugins.multiplesubtasks.model.SubtaskTemplateType;
import net.java.ao.DBParam;
import net.java.ao.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Subtask template service to persist and load templates from and to the database.
 */
@Transactional
@Component
public class SubtaskTemplateService {

    private final ActiveObjects activeObjects;

    @Autowired
    public SubtaskTemplateService(@ComponentImport ActiveObjects activeObjects) {
        this.activeObjects = activeObjects;
    }

    /**
     * Save a user specific subtask template.
     */
    public void saveUserTemplate(Long userId, Long templateId, String templateName, String templateText) {
        if (templateId == null) {
            SubtaskTemplate subtaskTemplate = activeObjects.create(SubtaskTemplate.class,
                new DBParam("TEMPLATE_TYPE", SubtaskTemplateType.USER),
                new DBParam("USER_ID", userId),
                new DBParam("NAME", templateName),
                new DBParam("TEMPLATE", templateText)
            );
            subtaskTemplate.save();
        } else {
            SubtaskTemplate subtaskTemplate = findUserTemplate(userId, templateId);
            if (subtaskTemplate != null) {
                subtaskTemplate.setName(templateName);
                subtaskTemplate.setTemplate(templateText);
                subtaskTemplate.save();
            }
        }
    }

    /**
     * Return a list of all subtask templates for a user.
     */
    public List<SubtaskTemplate> getUserTemplates(Long userId) {
        return Arrays.asList(activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("USER_ID = ? and TEMPLATE_TYPE = ?", userId, SubtaskTemplateType.USER)
                .order("ID DESC"))
        );
    }

    /**
     * Delete a given subtask template for a user.
     */
    public void deleteUserTemplate(Long userId, Long templateId) {
        SubtaskTemplate subtaskTemplate = findUserTemplate(userId, templateId);
        if (subtaskTemplate != null) {
            activeObjects.delete(subtaskTemplate);
        }
    }

    /**
     * Find a given subtask template for a user.
     */
    public SubtaskTemplate findUserTemplate(Long userId, Long templateId) {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("USER_ID = ? and ID = ? and TEMPLATE_TYPE = ?", userId, templateId, SubtaskTemplateType.USER));
        return subtaskTemplates.length > 0 ? subtaskTemplates[0] : null;
    }

    /**
     * Save a project specific subtask template.
     */
    public void saveProjectTemplate(Long projectId, Long userId, Long templateId, String templateName, String templateText) {
        if (templateId == null) {
            SubtaskTemplate subtaskTemplate = activeObjects.create(SubtaskTemplate.class,
                new DBParam("TEMPLATE_TYPE", SubtaskTemplateType.PROJECT),
                new DBParam("PROJECT_ID", projectId),
                new DBParam("USER_ID", userId),
                new DBParam("NAME", templateName),
                new DBParam("TEMPLATE", templateText)
            );
            subtaskTemplate.save();
        } else {
            SubtaskTemplate subtaskTemplate = findProjectTemplate(projectId, templateId);
            if (subtaskTemplate != null) {
                subtaskTemplate.setUserId(userId);
                subtaskTemplate.setName(templateName);
                subtaskTemplate.setTemplate(templateText);
                subtaskTemplate.save();
            }
        }
    }

    /**
     * Return a list of all subtask templates for a project.
     */
    public List<SubtaskTemplate> getProjectTemplates(Long projectId) {
        return Arrays.asList(activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("PROJECT_ID = ? and TEMPLATE_TYPE = ?", projectId, SubtaskTemplateType.PROJECT)
                .order("ID DESC"))
        );
    }

    /**
     * Delete a given subtask template for a project.
     */
    public void deleteProjectTemplate(Long projectId, Long templateId) {
        SubtaskTemplate subtaskTemplate = findProjectTemplate(projectId, templateId);
        if (subtaskTemplate != null) {
            activeObjects.delete(subtaskTemplate);
        }
    }

    /**
     * Find a given subtask template for a project.
     */
    public SubtaskTemplate findProjectTemplate(Long projectId, Long templateId) {
        SubtaskTemplate[] subtaskTemplates = activeObjects.find(SubtaskTemplate.class,
            Query.select()
                .where("PROJECT_ID = ? and ID = ? and TEMPLATE_TYPE = ?", projectId, templateId, SubtaskTemplateType.PROJECT));
        return subtaskTemplates.length > 0 ? subtaskTemplates[0] : null;
    }

}
