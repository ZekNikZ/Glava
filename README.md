# Glava
_Glava_ is an [esoteric programming language](http://esolangs.org/wiki/Esoteric_programming_language), built on top of a [Java](https://go.java/index.html?intcmp=gojava-banner-java-com) base, that is designed to be effective in [code golf](https://en.wikipedia.org/wiki/Code_golf) competitions.

Note: the Glava builds found here were previously referred to as _Glava 2.X_ with its [counterpart](https://github.com/GamrCorps/Glava_Legacy) referred to as simply _Glava_. After July 2017, these builds are now under the _Glava_ name with the older counterparts now known as _Glava-Legacy_.

## Usage
Basic usage: `java -jar glava.jar [command-line args]`. The file extension for _Glava_ programs is `.gl` by convention, but any extension should work. Also, _Glava_ uses its own encoding, found here: [Glava Code Page](https://docs.google.com/spreadsheets/d/1THomC_jrmYAeH9h0kJPx4lrb5UIgM5jU7Mj6r2HtoGg/edit?usp=sharing).

### Command-line Flags:
 - `-help`, `-h`, `-?`:
  Display the command-line reference sheet.
 - `-file [file]`, `-f [file]`:
  Open a file and interpret it as Glava code in UTF-8.
 - `-gfile [file]`, `-gf [file]`:
  Open a file and interpret it as Glava code in the Glava Code Page.
 - `-code [code]`, `-c [code]`:
  Run a UTF-8 string as Glava code.
 - `-debug`, `-d`:
  Enable debug mode. Note: must come before any other flags / arguments to avoid odd behaviour.

### Examples:
 - Display the help: `java -jar glava.jar -help`
 - Run a file (in `UTF-8`): `java -jar glava.jar -file hello_world.gl`
 - Run a file (in the [Glava Code Page](https://docs.google.com/spreadsheets/d/1THomC_jrmYAeH9h0kJPx4lrb5UIgM5jU7Mj6r2HtoGg/edit?usp=sharing)): `java -jar glava.jar -gfile factorial.gl`
 - Debug a snippet of code: `java -jar glava.jar -debug -code "λ\"Hello, World!"`

## Why use _Glava_?
Even if you don't use _Glava_ for code golf competitions, it still has some handy features, including:
 - Multi-line strings.
 - Shorthands for command-line arguments.
 - Automatic class wrapping.
 - Automatic bracket closing.
 - and more!

A full list of shorthands and useful features can found on the [wiki](https://github.com/GamrCorps/Glava/wik).

## External Resources
 - [Apache Commons](https://commons.apache.org/): Useful library functions.
 - [Jalopy](http://notzippy.github.io/JALOPY2-MAIN/): Code beautifier.
 - [Glava Legacy](https://github.com/GamrCorps/Glava_Legacy): Previous version of _Glava_.
 - [Esolangs Page](http://esolangs.org/wiki/Glava): Esolang wiki page (still a work-in-progress).
