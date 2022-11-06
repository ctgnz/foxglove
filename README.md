# Foxglove SVG/JavaFX

Native support for SVG in JavaFX

This library provides an XML binding of the SVG schema, and a binding to create JavaFX components from the parsed SVG objects.

Current status: Under development

### Basic Usage

```java
BorderPane parent = new BorderPane();
FoxgloveParser parser = new FoxgloveParser();
SvgGraphic graphic = parser.parse(Files.newInputStream("myfile.svg");
parent.setCenter(graphic.createGraphic());
```
