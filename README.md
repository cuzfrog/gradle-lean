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

* __get this plugin published!__
* lean for `distTar`
* customized jars
* exclusive classes

## How to use:

Add gradle-lean plugin to build:

```groovy
plugins {
    id "com.github.cuzfrog.gradle.lean.LeanPlugin"
}
```

Execute gradle tasks:

* `installDistLean` will trigger `installDist` and then minimize the jars.
* `distZipLean` will trigger `distZip` and then minimize the zip archive.

## License:
Apache-2.0

## Author:
Cause Chung(cuzfrog@139.com)