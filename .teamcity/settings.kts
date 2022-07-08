import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPack
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetPublish
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetTest
import jetbrains.buildServer.configs.kotlin.triggers.vcs

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.04"

project {
    buildType(BuildClassLib)
    buildType(BuildSite2)
}

object BuildClassLib : BuildType({
    name = "Build classlib"
    publishArtifacts = PublishMode.SUCCESSFUL
    artifactRules = "src/TestTeamCity.ClassLib/bin/Release/TestTeamCity.ClassLib.*.nupkg"

    var projectPath = "src/TestTeamCity.ClassLib/TestTeamCity.ClassLib.csproj"
    var buildConfiguration = "Release"

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            enforceCleanCheckout = true
        }
    }

    steps {
        dotnetBuild {
            name = "dotnet build"
            projects = projectPath
            configuration = buildConfiguration
        }
        dotnetTest {
            name = "dotnet test"
            projects = "test/TestTeamCity.ClassLib.Tests/TestTeamCity.ClassLib.Tests.csproj"
            configuration = buildConfiguration
            coverage = dotcover()
        }
        dotnetPack {
            name = "dotnet pack"
            projects = projectPath
            configuration = buildConfiguration
            args = "--no-build"
        }
    }
})

object BuildSite2 : BuildType({
    var outputDirectoryPath = "output/site"
    var projectPath = "src/TestTeamCity.Site/TestTeamCity.Site.csproj"
    var buildConfiguration = "Release"

    name = "Build site"
    publishArtifacts = PublishMode.SUCCESSFUL
    artifactRules = outputDirectoryPath + " => site"

    vcs {
        root(DslContext.settingsRoot)
    }

    triggers {
        vcs {
            enforceCleanCheckout = true
        }
    }

    steps {
        dotnetBuild {
            name = "dotnet build"
            projects = projectPath
            configuration = buildConfiguration
        }
        dotnetPublish {
            name = "dotnet publish"
            projects = projectPath
            configuration = buildConfiguration
            outputDir = outputDirectoryPath
        }

    }
})
