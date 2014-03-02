# <a name="Introduction"/>Introduction

This bnd workspace is setup to be built with [Gradle](http://www.gradle.org).


# <a name="InstallingGradle"/>Installing Gradle

## On The System

Obviously, Gradle must be installed on the system before the workspace can be
built with Gradle.

This description assumes a Linux machine. Details may vary on other OSes.

* Download Gradle from [http://www.gradle.org](http://www.gradle.org).

* Unpack the downloaded archive and put it in ```/usr/local/lib```,
  as ```/usr/local/lib/gradle-1.11```.

* Put the Gradle executable ```/usr/local/lib/gradle-1.11/bin/gradle``` on
  the search path by linking to it from ```/usr/local/bin```:

```
ln -s /usr/local/lib/gradle-1.11/bin/gradle /usr/local/bin/
```

## In The Workspace

Gradle can be installed in the workspace so that the workspace can be built on
systems that do not have Gradle installed (like build servers).

The procedure is:

* Open a shell.

* Go to the workspace root directory.

* Assuming Gradle is properly installed on the system, run:

```
gradle wrapper
```

* Commit the files that were downloaded and created in the workspace to your
  version control system.


# <a name="GradleDaemon"/>Configuring The Gradle Daemon

Startup times of a Gradle build can be much improved by using the Gradle daemon.

The Gradle daemon works well when the Gradle build scripts are not changed,
which makes it well suited to regular development but **not** for build servers.

The daemon can be easily setup by adding the following line
to ```~/.gradle/gradle.properties```:

```
org.gradle.daemon=true
```


# <a name="Projects"/>Projects & Workspaces

## <a name="RootProject"/>Root Project

The Gradle root project is the directory that contains the ```settings.gradle```
file.

Gradle locates the root project by first looking for the ```settings.gradle```
file in the directory from which it was run, and - when not found - then
searching up in the directory tree for the file.

## <a name="SubProjects"/>Sub-Projects

The build will include all projects in the build that are:

* **bnd** projects   : Directories directly below the root project with
                       a ```bnd.bnd``` file.

* **Gradle** projects: Directories directly below the root project with
                       a ```build.gradle``` file.

## <a name="GradleWorkspace"/>Gradle Workspace

The Gradle workspace is rooted in the root project and consists of all included
projects - **bnd** *and* **gradle** projects.

## <a name="BndWorkspace"/>Bnd Workspace

The bnd workspace is rooted in the root project and contains a single
configuration project, and zero or more **bnd** projects.

For it to be a *useful* bnd workspace, it will have to contain at least one bnd
project.

## <a name="Cnf"/>Configuration Project

The configuration project is - by default - located in the ```cnf``` directory.

It contains:

* Placeholder source and classes directories (```src``` and ```bin```
  respectively).

* Bnd workspace configuration.

  * &nbsp;```ext/*.bnd```

    The ```ext``` directory contains bnd settings files that are loaded
    **before** the ```build.bnd``` file.

    The directory typically contains:

    * &nbsp;```junit.bnd```

      This file defines a bnd variable for the libraries that are needed on the
      classpath when running junit tests.

    * &nbsp;```pluginpaths.bnd```

      This file instructs bnd to load a number of plugin libraries when it
      runs. Typically it will instruct bnd to load repository plugins. However,
      custom plugins can also be loaded by bnd by adding them to
      the ```-pluginpath``` instruction in this file.

    * &nbsp;```repositories.bnd```

      This file configures the plugins that bnd loads. Typically it will
      configure the repository plugins that are loaded. However, if any built-in
      plugins or custom plugins are loaded then these also will have to be
      configured here. This file also defines which repository is the release
      repository.

  * &nbsp;```build.bnd```

    This file contains workspace-wide settings for bnd and will override
    settings that are defined in either of the ```ext/*.bnd``` files.

* Repositories.

  * &nbsp;```buildrepo```

    This repository contains libraries that are intended **only for build-time**
    usage. None are intended to be deployed as bundles into a running OSGi
    framework, and indeed they may cause unexpected errors if they are used
    in such a fashion.

  * &nbsp;```localrepo```

    This repository contains no libraries by default. It is intended for
    external libraries that are needed by one or more of the projects.

  * &nbsp;```releaserepo```

    This repository contains no libraries by default. Bundles end up in this
    repository when they are released.

* Cache.

  The ```cache``` directory contains libraries that are downloaded by the build.
  If the build is self-contained then this cache only contain libraries that are
  downloaded by bnd from the workspace during the build.

* Build files.

  * <a name="BuildDependencies"/>```build.properties```

    This file is used to bootstrap the build and defines the build dependencies:

    * All ```*.location``` and ```*.url``` settings are considered to be build
      dependencies.

    * A ```*.location``` setting has priority over the corresponding ```*.url```
      setting, which means that the ```example.url``` setting will be ignored
      if ```example.location``` is also specified.

    * An ```example.location``` setting will make the build script add the
      specified location (path) to the build dependencies.

    * An ```example.url``` setting will make the build script download
      the ```example``` build dependency from the specified URL into
      the ```cnf/cache/gradle``` directory and add it to the build dependencies.

      Using a ```*.url``` setting is not **not recommended** since the build
      will then no longer be self-contained (because it needs network access).

  * &nbsp;```gradle```

    This directory contains all build script files that are used by the build,
    and documentation pertaining to the build.

    * &nbsp;```template```

      This directory contains build script files that define the build. These
      are **not** meant to be changed.

    * &nbsp;```custom```

      This directory contains build script files that allow specification of
      overrides for various settings and and addtions to the build. The build
      script files are effectively hooks into the build setup.

      These **are** meant to be changed - when the build customisations are
      needed.

    * &nbsp;```dependencies```

      This directory contains libraries that are used by the build.

    * &nbsp;```doc```

      This directory contains documentation pertaining to the build. The
      document you're now reading is located in this directory.

      Also found in this directory is a diagram ([template.svg](template.svg))
      that provides an overview of the build setup, much like the Gradle User
      Guide does for the Java Plugin.

      It shows all tasks of the build and how they are related:

      * The arrows depict **execution flow** (so the dependencies are in the
        reverse direction).

      * The **dotted** arrow depicts a convenience flow/dependency;
        running ```gradle jar``` will build all jars *and* bundles in all
        relevant projects.

      * The **red** arrows depict flows from (dependencies on) dependent
        projects.

        For example:

        The ```compileJava``` task of a project is dependent on the ```bundle```
        task of another project if the latter project is on the build path of
        the former project.

      * The **blue** arrows depict flows/dependencies that are only present
        when the task from which the flows originate is present in the project.

      * The **green** arrows depict flows/dependencies that are disabled by
        default.

# <a name="BuildTasks"/>Build Tasks

FIXME WIP HERE

As a bnd project basically is a Java project with some extra
The build template adds several tasks

## Bnd Projects

A bnd project is special java project and therefore applies the Gradle 'java'
plugin before setting up the customisations it needs.

Refer to the Gradle User Guide for more information on the java plugin.

The table below only describes the tasks that are added for bnd projects and
how they hook into the build cycle that is defined by the java plugin.

### bundle

### bundleTest

### release

### releaseNeeded

### export

### bndproperties

This task - analogous to the Gradle ```properties``` task - displays the bnd
properties that are defined for the project.

These properties are defined in the ```bnd.bnd``` file in the root of the
project (and optionally other ```*.bnd``` files when using the ```-sub```
instruction for sub-bundles).


## All Projects

### indexOBR

### indexR5

### index

### cleanNeeded

FIXME mention only active when....

### distClean

This task performs additional cleanup compared to the ```clean``` task.

* For Java projects it removes:
  * The classes output directory of all defined sourcesets.
  * The resources output directory of all defined sourcesets.

* For the root project it removes:
  * The cache directory in the configuration project.
  * The Gradle cache directory.

### distcleanNeeded


## Java Projects

### findbugsMain

Reports only with line numbers when run on a debug build.

### findbugsTest

### findbugs

### findbugstest

### javadoc


## Root Project

### wrapper



# Understanding The Build

## Gradle Build Setup Diagram


see FIXME

## <a name="BuildFlow" />Build Flow

The build has the following flow:

2. From the root project the ```settings.gradle``` file is loaded,
   which will instruct Gradle to include all bnd projects and Gradle projects
   (see [Introduction](#Introduction) for an explanation).

3. From the root project Gradle loads the ```build.gradle``` file, which will:

   1. Load the default build settings.

      The default build settings are used to:
      1. Configure the build itself.
      2. Configure tasks of the root project.
      3. Configure tasks of the included projects.

   2. Load the overrides of the build settings (from
      the ```cnf/gradle/custom/settings-workspace.gradle``` file). FIXME

   3. Setup the build dependencies.

      All jars and bundles that are located in ```cnf/plugins``` are considered
      to be build dependencies. Additional FIXME
      (see [the explanation of the build dependencies](#BuildDependencies)



   4. Initialise the bnd workspace and load the workspace defaults.

   5. Apply the build template.

      1. Setup defaults for all projects.
      2. Apply build settings overrides for all projects that have
         a ```build-settings-overrides.gradle``` file.
      3. Apply build customisations to all projects (from
         the ```cnf/gradle/custom/allProjects.gradle``` file).
      4. Apply build customisations to all non-bnd projects (from
         the ```cnf/gradle/custom/nonBndProjects.gradle``` file).
      5. Apply the bnd build setup to all bnd projects.
      6. Apply build customisations to all bnd projects (from
         the ```cnf/gradle/custom/bndProjects.gradle``` file).
      7. Apply build customisations to all included projects (from
         the ```cnf/gradle/custom/subProjects.gradle``` file).
      8. Setup the root project.
      9. Apply build customisations to the root project (from
         the ```cnf/gradle/custom/rootProject.gradle``` file).

4. Gradle now resolves the build setup.

5. Gradle will build the project.


# Build Options

## Findbugs

FIXME

* ```findbugs``` must be explicitly specified as a task to run (on the
  command line) for the ```findbugsMain``` task to be enabled.
* ```findbugstest``` must be explicitly specified as a task to run (on the
  command line) for the ```findbugsTest``` task to be enabled.
* Set the ```CI``` property (```-PCI``` on the command line) to generate XML
  reports instead of html reports.


# Customising The Build


FIXME show cnf/build.gradle & cnf/build-settings.gradle examples

The build be can easily customised by putting overrides in any of
the ```cnf/gradle/custom/*.gradle``` build script files.

Also, any project can - on an individual basis - override build settings by
placing a ```build-settings-overrides.gradle``` in its root directory.

Make sure to read the [Build Flow](#BuildFlow) section to pick the correct
build script file.
