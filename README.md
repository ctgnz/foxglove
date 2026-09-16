# Foxglove SVG/JavaFX

[![Java CI with Maven](https://github.com/ctgnz/foxglove/actions/workflows/maven.yml/badge.svg)](https://github.com/ctgnz/foxglove/actions/workflows/maven.yml)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.ctgnz/foxglove.svg)](https://central.sonatype.com/artifact/io.github.ctgnz/foxglove)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](LICENSE.md)

Native support for [SVG 1.1](https://www.w3.org/TR/SVG11/) in JavaFX.

This library provides an XML binding of the SVG schema, and a binding that turns the parsed SVG objects into real JavaFX scene graph nodes - no rasterising, no headless browser, no native library. A parsed document renders straight into `javafx.scene.Node`s you can add to your own scene graph, style, transform and interact with like anything else in JavaFX.

## Status

Static rendering is the primary, most complete target: shapes, paths, gradients and patterns, clipping/masking, filter effects, text (including embedded SVG fonts), and the structural elements (`<use>`/`<symbol>`, nested `<svg>`, `<image>`). [SMIL declarative animation](https://www.w3.org/TR/SVG11/animate.html) (`<animate>`, `<animateColor>`, `<animateMotion>`, `<animateTransform>`, `<set>`) is implemented and playable/seekable via `SvgGraphic.createAnimatedGraphic`.

**Interaction and scripting are explicitly out of scope, by design, not by omission.** Foxglove renders SVG into ordinary JavaFX `Node`s - any interactivity (hover states, click handlers, drag behaviour) is the consuming application's own responsibility, the same as for any other JavaFX scene graph content. There is no internal scripting engine and none is planned.

Real, current conformance numbers - not a status claim taken on faith - are published from the [W3C SVG 1.1 (Second Edition) test suite](https://www.w3.org/Graphics/SVG/Test/20110816/) on every push to `master`:

**[ctgnz.github.io/foxglove](https://ctgnz.github.io/foxglove/)** - separately reported for static rendering, animation (seeked against a live browser reference, not a single static frame) and interaction (which stays near-zero by design, per the above).

A live desktop tool for browsing the same test suite and visually comparing this library's rendering against a real browser side by side, `ConformanceSuiteBrowser`, lives under `src/test/java` for anyone working on this repository - see its own class documentation.

## Installation

```xml
<dependency>
    <groupId>io.github.ctgnz</groupId>
    <artifactId>foxglove</artifactId>
    <version>0.9.9</version>
</dependency>
```

Requires JDK 25. [JavaFX](https://openjfx.io/) itself comes in transitively - nothing extra to add for it.

## Basic Usage

```java
// The parser should be created once in the application and injected wherever needed
FoxgloveParser parser = new FoxgloveParser();
...
BorderPane parent = new BorderPane();
try {
    SvgGraphic graphic = parser.parse(Files.newInputStream(Path.of("myfile.svg")));
    parent.setCenter(graphic.createGroup());
} catch (Exception e) {
    // handle a malformed or unreadable document
}
```

This exact snippet is compiled and run as part of the test suite (`ReadmeExampleTest`), so it can't silently drift out of date the way the previous example did.
