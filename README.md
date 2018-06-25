# Gradle-lean

A gradle plugin that minimizes dependency jars.

## Motivation

We have [maven-shade](https://github.com/apache/maven-shade-plugin) to help us minimize jars. 
But when it comes to gradle, the similar plugin [gradle-shadow](https://github.com/johnrengelman/shadow) doesn't do the favor. 
This project is to provide a lightweight way to reduce the size of an java application distribution package on the fly.

This plugin depends on `JavaPlugin` and `ApplicationPlugin`.

* for `installDist`, jars under `install/$PROJECT_NAME$/lib/`
* for `distZip`, `distTar`, jars under `/lib/` inside package
* any customized jars

are automatically minimized.

## How to use:

### Retain classes:


## License:
Apache-2.0

## Author:
Cause Chung(cuzfrog@139.com)