# Foxglove SVG/JavaFX

Native support for SVG 1.1 in JavaFX: an XML binding of the SVG schema plus a binding that turns parsed SVG objects into real JavaFX scene graph nodes. Consumed by `jmsfx` (separate repo) for rendering military symbology.

## Code style

Eclipse formatter/import-order profile enforced via Spotless (`config/eclipse-formatter.xml`, `config/eclipse.importorder`), bound to the `verify` phase - `mvn verify` runs `spotless:check` and will fail on drift. `-Xlint:all -Werror` is on for the compiler. Source encoding is `UTF-8` throughout (property and compiler plugin both agree - the compiler plugin used to say `ISO-8859-1`, a stray copy-paste from jmsfx, but foxglove has no non-ASCII source content so it never actually mattered).

Enum constants are **not** named `UPPER_SNAKE_CASE` by convention across this author's projects - the view is that an enum constant is closer to an anonymous inner class instance than a `static final` constant, so names may be mixed-case. Enums can carry real behavior rather than being plain data. Don't suggest renaming toward conventional constant style or simplifying toward plain data as a "cleanup."

## Testing

An optional `conformance` Maven profile (`mvn -Pconformance test`) fetches the W3C SVG 1.1 test suite and runs it against a checked-in baseline manifest - kept out of the default build/CI since it needs network access and a display (`xvfb-run` in CI). The default `mvn test` run covers 777 tests and does not require this profile.
