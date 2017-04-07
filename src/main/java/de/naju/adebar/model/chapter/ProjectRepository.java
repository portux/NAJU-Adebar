package de.naju.adebar.model.chapter;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository to access {@link Project} instances
 * @author Rico Bergmann
 */
@Repository
public interface ProjectRepository extends CrudRepository<Project, Long> {

    /**
     * @param name the name to query for
     * @return all projects with the given name
     */
    Iterable<Project> findByName(String name);

    /**
     * @param localGroup the local group to query for
     * @return all projects that are hosted by the local group
     */
    Iterable<Project> findByLocalGroup(LocalGroup localGroup);

    /**
     * @param name the project's name
     * @param localGroup the local group that should host the project
     * @return the project with the matching name/local group combination (which should be unique)
     */
    Project findByNameAndLocalGroup(String name, LocalGroup localGroup);

}