/*
 * Copyright 2015-2016 the original author or authors.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v1.0 which
 * accompanies this distribution and is available at
 *
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.junit.platform.commons.util;

import static java.nio.file.FileVisitResult.CONTINUE;
import static java.util.logging.Level.WARNING;

import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.function.Consumer;
import java.util.logging.Logger;

class ClassFileVisitor extends SimpleFileVisitor<Path> {

	private static final Logger LOG = Logger.getLogger(ClassFileVisitor.class.getName());

	static final String CLASS_FILE_SUFFIX = ".class";
	private static final String PACKAGE_INFO_FILE_NAME = "package-info" + CLASS_FILE_SUFFIX;

	private final Consumer<Path> classFileConsumer;

	ClassFileVisitor(Consumer<Path> classFileConsumer) {
		this.classFileConsumer = classFileConsumer;
	}

	@Override
	public FileVisitResult visitFile(Path file, BasicFileAttributes attributes) {
		if (isNotPackageInfo(file) && isClassFile(file)) {
			classFileConsumer.accept(file);
		}
		return CONTINUE;
	}

	@Override
	public FileVisitResult visitFileFailed(Path file, IOException exc) {
		LOG.log(WARNING, exc, () -> "I/O error visiting file: " + file);
		return CONTINUE;
	}

	@Override
	public FileVisitResult postVisitDirectory(Path dir, IOException exc) {
		if (exc != null) {
			LOG.log(WARNING, exc, () -> "I/O error visiting directory: " + dir);
		}
		return CONTINUE;
	}

	private static boolean isNotPackageInfo(Path path) {
		return !path.endsWith(PACKAGE_INFO_FILE_NAME);
	}

	private static boolean isClassFile(Path file) {
		return file.getFileName().toString().endsWith(CLASS_FILE_SUFFIX);
	}

}
