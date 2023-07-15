# Foxglove SVG/JavaFX

Native support for [SVG 1.1](https://www.w3.org/TR/SVG11/) in JavaFX

This library provides an XML binding of the SVG schema, and a binding to create JavaFX components from the parsed SVG objects.

Current status: Under development

### Basic Usage

```java
// The parser should be created once in the application and injected wherever needed
FoxgloveParser parser = new FoxgloveParser();
...
BorderPane parent = new BorderPane();
SvgGraphic graphic = parser.parse(Files.newInputStream("myfile.svg"));
parent.setCenter(graphic.createGraphic());
```
