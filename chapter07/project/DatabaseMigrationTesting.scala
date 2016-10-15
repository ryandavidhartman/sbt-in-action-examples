import sbt._
import sbt.complete._
import DefaultParsers._
import sbt.Project.Initialize
import sbt.Keys._


object DatabaseEvolutionTesting {
  
  sealed trait Command {
    def script: File
  }
  case class TestUp(script: File) extends Command {
    override def toString = s"UPS - ${script.getAbsolutePath}"
  }
  case class TestDown(script: File) extends Command {
    override def toString = s"DOWNS - ${script.getAbsolutePath}"
  }

  val evolutionsDirectory = settingKey[File]("location of evolutions scripts.")

  val evolutionsDirectoryDefaultSetting: Setting[_] = 
    evolutionsDirectory := {
      (resourceDirectory in Compile).value / "evolutions/default" 
    }
  
  val parser: Initialize[Parser[Command]] =
    Def.setting { 
      commandParser(baseDirectory.value / "conf/evolutions")
   }
  
  sealed trait OldCommand
  case class UpCommand(number: String) extends OldCommand
  case class DownCommand(number: String) extends OldCommand
  
  def availableScripts(dir: File): Set[String] = {
   val scripts = IO.listFiles(dir, "*.sql")
   val scriptNumbers =
      for {
        script <- scripts
        filename = script.getName
        if filename matches "\\d+\\.sql"
     } yield filename.dropRight(4)
    scriptNumbers.toSet
}
  
  
  val oldParser: Initialize[Parser[OldCommand]] = Def.setting {
      
    val examples =
      availableScripts(evolutionsDirectory.value)

    val migrationNumber: Parser[String] =  
      token(Digit.+ map (_.mkString), "<Migration Script Number>").examples(examples, true)
    
    
    val upCommand = literal("up") ~> Space ~> migrationNumber map UpCommand.apply
    val downCommand = literal("down") ~> Space ~> migrationNumber map DownCommand.apply
    
    Space ~> (upCommand | downCommand)
  }
  
  def scriptNameParser(baseDirectory: File): Parser[File] = {
    // TODO - Make sure all script names are numbered...
    
    val samples = IO.listFiles(baseDirectory, "\\d+.sql")
    val migrations: Set[String] = samples.map(_.getName dropRight 4).toSet
    
    val scriptName = 
      token(Digit.+ map (_.mkString), "migration script").examples(migrations, true)
      
    scriptName map { name =>
      baseDirectory / s"$name.sql"  
    }
  }
  
    
  def commandParser(baseDirectory: File): Parser[Command] = {
    val scriptName: Parser[File] = scriptNameParser(baseDirectory)    
    
    val upCmd: Parser[TestUp] =
       literal("up") ~> Space ~> scriptName map TestUp.apply
    
    val downCmd =
      "down" ~> Space ~> scriptName map TestDown.apply
      
    Space ~> (upCmd | downCmd)
  }
  
  
  
  def runCommand(cmd: Command, db: DatabaseHelper, log: Logger): Unit = {
    // TODO - We dont do anything about enusring we go up/down and keep the databse consistent.
    // All we do is run the commands in the evolutions script.
    
    log.info(s"Testing $cmd")
    val commands = getCommands(cmd)
    for(sql <- commands) {
      // TODO - Print statement?
      log.info("Exeucting query = [" + sql + "]")
      db.runQuery(sql, log)
    }
  }
  
  val upsMatcher = "\\s*#[\\s]*\\-*\\s*\\!Ups.*"
  val downsMatcher = "\\s*#[\\s]*\\-*\\s*\\!Downs.*"
  def isComment(line: String): Boolean = line.trim startsWith "#"
  def getCommands(cmd: Command): Seq[String] = {
    val lines = IO.readLines(cmd.script)
    val cmdLines = 
      cmd match {
        case _: TestUp => getUpLines(lines)
        case _: TestDown => getDownLines(lines)
      }
    cmdLines.map(_.trim).mkString(" ").split(";").map(_.trim).filterNot(_.isEmpty)
  }
  def getUpLines(lines: List[String]): List[String] = 
    lines dropWhile { line =>
      !(line matches upsMatcher)
    }  takeWhile { line =>
      !(line matches downsMatcher)
    } filterNot isComment
  def getDownLines(lines: List[String]): List[String] = 
    lines dropWhile { line =>
      !(line matches downsMatcher)
    } filterNot isComment
}