package de.novatec.aqe.repository

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand
import org.eclipse.jgit.transport.URIish
import org.slf4j.Logger
import org.slf4j.LoggerFactory.getLogger
import org.springframework.stereotype.Component
import java.nio.file.Files
import java.nio.file.Path

@Component
internal class LocalGitRepository {

    private val log: Logger = getLogger(javaClass)

    private lateinit var git: Git

    init {
        val localGitRepository = Files.createTempDirectory("git-repo").toFile().apply { deleteOnExit() }
        log.info("temporary folder for local GIT repository: {}", localGitRepository)
        git = Git.init().setDirectory(localGitRepository).call()
    }

    fun setOriginRemote(remoteUrl: String) {
        with(git.remoteAdd()) {
            setName("origin")
            setUri(URIish(remoteUrl))
            call()
        }
        log.info("GIT: 'remote add origin ${remoteUrl}'")
    }

    fun fetchOrigin() {
        with(git.fetch()) {
            setRemote("origin")
            call()
        }
        log.info("GIT: 'fetch origin'")
    }

    fun resetToOriginMaster() {
        with(git.reset()) {
            setMode(ResetCommand.ResetType.HARD)
            setRef("origin/master")
            call()
        }
        log.info("GIT: 'reset origin/master --hard'")
    }

    fun getWorkingDirectory(): Path {
        return git.repository.workTree.toPath()
    }

}
