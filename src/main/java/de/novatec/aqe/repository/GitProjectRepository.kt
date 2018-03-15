package de.novatec.aqe.repository

import com.fasterxml.jackson.databind.ObjectMapper
import de.novatec.aqe.model.Project
import de.novatec.aqe.repository.ProjectRepository
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Service
import java.io.File
import java.nio.file.Files.list
import java.nio.file.Path
import java.util.stream.Collectors.toList
import javax.annotation.PostConstruct

@Service
@ConfigurationProperties("git")
internal class GitProjectRepository(
        private val objectMapper: ObjectMapper,
        private val localRepository: LocalGitRepository
) : ProjectRepository {

    var repositoryUrl: String? = null

    private val fileSuffixFilter = { file: File -> file.name.endsWith(".project") }

    @PostConstruct
    fun initLocalRepository() {
        with(localRepository) {
            setOriginRemote(repositoryUrl!!)
            fetchOrigin()
            resetToOriginMaster()
        }
    }

    override fun findAll(): List<Project> {
        val workingDirectory = localRepository.getWorkingDirectory()
        return list(workingDirectory)
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(fileSuffixFilter)
                .map(this::readAsProject)
                .collect(toList())
    }

    override fun findById(id: String): Project? {
        return list(localRepository.getWorkingDirectory())
                .map(Path::toFile)
                .filter(File::isFile)
                .filter(fileSuffixFilter)
                .filter { file -> id == file.nameWithoutExtension }
                .map(this::readAsProject)
                .findFirst().orElse(null)
    }

    override fun refresh() {
        with(localRepository) {
            fetchOrigin();
            resetToOriginMaster()
        }
    }

    private fun readAsProject(projectFile: File): Project {
        return objectMapper.readValue(projectFile, Project::class.java)
                .apply { id = projectFile.nameWithoutExtension }
    }

}
