package com.github.cuzfrog.gradle.lean;

import org.gradle.internal.impldep.com.google.common.annotations.VisibleForTesting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vafer.jdependency.Clazz;
import org.vafer.jdependency.Clazzpath;
import org.vafer.jdependency.ClazzpathUnit;

import javax.annotation.concurrent.Immutable;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Immutable
final class MinimizerImpl implements Minimizer {
    private static final Logger logger = LoggerFactory.getLogger(MinimizerImpl.class);
    private static final Pattern jarNamePattern = Pattern.compile(".+:(.+):(.+)");
    private static final Pattern classPattern = Pattern.compile("(\\w+\\.)+(\\w+|\\*)");

    private final JarMan jarMan;
    private final Set<String> excludedClasses;
    private final Set<JarNameMatcher> excludedDependencies;

    @VisibleForTesting
    MinimizerImpl(final JarMan jarMan,
                  final Collection<String> excludedClasses,
                  final Collection<String> excludedDependencies) {
        this.jarMan = jarMan;
        for (final String excludedClass : excludedClasses) {
            if(!classPattern.matcher(excludedClass).matches()){
                throw new IllegalArgumentException(String.format(
                        "Excluded class name can only be in format like 'dom.pkg.ClassName' or 'dom.pkg' or 'dom.pkg.*'," +
                                " but config is: '%s'", excludedClass));
            }
        }
        this.excludedClasses = Collections.unmodifiableSet(
                excludedClasses.stream()
                        .map(clz -> clz.endsWith(".*") ? clz.substring(0, clz.length() - 2) : clz)
                        .collect(Collectors.toSet())
        );
        this.excludedDependencies = Collections.unmodifiableSet(
                excludedDependencies.stream().map(MinimizerImpl::parseJarName).collect(Collectors.toSet()));
    }

    @Override
    public void minimize(final Path archivePath, final Path libDir) {
        try {
            final Clazzpath cp = new Clazzpath();
            final ClazzpathUnit artifact = cp.addClazzpathUnit(archivePath);

            final String artifactName = archivePath.getFileName().toString();
            final List<Path> dependencyJars = getLibDependencyJars(libDir, artifactName);
            for (final Path jar : dependencyJars) {
                cp.addClazzpathUnit(jar, jar.getFileName().toString());
            }

            final Set<Clazz> removable = cp.getClazzes().stream()
                    .filter(clz -> excludedClasses.stream().noneMatch(ptn -> clz.getName().startsWith(ptn)))
                    .filter(clz -> !artifact.getClazzes().contains(clz))
                    .filter(clz -> !artifact.getTransitiveDependencies().contains(clz))
                    .collect(Collectors.toSet());

            for (final Path jar : dependencyJars) {
                logger.trace("Try to minimize jar '{}'", jar);
                jarMan.removeEntry(jar, removable);
            }
        } catch (final IOException e) {
            throw new RuntimeException("Error happened while minimizing jars in dir:" + libDir, e);
        }
    }

    @VisibleForTesting
    List<Path> getLibDependencyJars(final Path libDir,
                                    final String artifactName) throws IOException {
        return Files.list(libDir)
                .filter(p -> {
                    final String filename = p.getFileName().toString();
                    return filename.endsWith(".jar") && !filename.equals(artifactName);
                })
                .filter(p -> excludedDependencies.stream().noneMatch(m -> m.matches(p.getFileName().toString())))
                .collect(Collectors.toList());
    }

    private static JarNameMatcher parseJarName(final String dependencyName) {
        final Matcher matcher = jarNamePattern.matcher(dependencyName);
        if (!matcher.matches()) {
            throw new IllegalArgumentException(String.format(
                    "Dependency name must be in format of 'domain:name:version' or 'domain:name:*', but given name is: '%s'", dependencyName));
        }
        final String name = matcher.group(1);
        final String version = matcher.group(2);
        return new JarNameMatcher(name, version);
    }

    private static final class JarNameMatcher {
        private final String name;
        private final String version;
        private JarNameMatcher(final String name, final String version) {
            this.name = name;
            this.version = version;
        }

        boolean matches(final String jarName) {
            if (Objects.equals("*", version)) {
                if (jarName.startsWith(name)) {
                    final String latter = jarName.substring(name.length());
                    return latter.startsWith("-") && latter.endsWith(".jar");
                }
            } else {
                return Objects.equals(jarName, name + "-" + version + ".jar");
            }
            return false;
        }
    }
}
