[![Build Status](https://travis-ci.org/cuzfrog/gradle-lean.svg?branch=master)](https://travis-ci.org/cuzfrog/gradle-lean)
[![Plugin version](https://img.shields.io/badge/gradle--plugin-0.1.2-blue.svg)](https://plugins.gradle.org/plugin/com.github.gradle-lean)

# Gradle-lean

A gradle plugin that minimizes dependency jars.

## Motivation

We have [maven-shade](https://github.com/apache/maven-shade-plugin) to help us minimize jars. 
But when it comes to gradle, the similar plugin [gradle-shadow](https://github.com/johnrengelman/shadow) doesn't do the favor. 
This project is to provide a lightweight way to reduce the size of an java application distribution package on the fly.

This plugin depends on `JavaPlugin` and `ApplicationPlugin`.

* for `installDist`, jars under `install/$PROJECT_NAME$/lib/`
* for `distZip`, jars under `/lib/` inside package

will be minimized by respective lean tasks.

### Todo list:

* resolve current issues
* lean for `distTar`
* customized jars

## How to use:

Add gradle-lean plugin to build:

```groovy
plugins {
    id "com.github.gradle-lean" version "0.1.2"
}
```

Execute gradle tasks:

* `installDistLean` will trigger `installDist` and then minimize the jars.
* `distZipLean` will trigger `distZip` and then minimize the zip archive.

### Exclusions

Class or dependency jar exclusion can be set in `build.gradle`:

```groovy
leanConfig {
    excludedClasses = [
            "com.google.common.cache.*",
            "com.google.common.io.ByteSink"
    ]
    excludedDependencies = [
            "com.google.jimfs:jimfs:1.1"
            //or "com.google.jimfs:jimfs:*"
    ]
}
```

## About:
Author: Cause Chung(cuzfrog@139.com)
License: Apache-2.0
