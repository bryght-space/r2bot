package com.bryghts.r2bot

import java.nio.file.Path
import java.nio.file.Files

object PathOps {

  def path(name: String): Path =
    sbt.file(name).toPath

  implicit class PathOps(val path: Path) extends AnyVal {

    def allFilesRecursively: Iterator[Path] = {
      import scala.collection.JavaConverters._
      Files.walk(path).iterator().asScala.filter(Files.isRegularFile(_))
    }

    def delete(): Boolean =
      path.toFile().delete()

    def renameTo(target: Path): Boolean =
      path.toFile().renameTo(target.toFile())

  }

}
