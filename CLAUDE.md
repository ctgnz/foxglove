# Foxglove SVG/JavaFX

Native support for SVG 1.1 in JavaFX: an XML binding of the SVG schema plus a binding that turns parsed SVG objects into real JavaFX scene graph nodes. Consumed by `jmsfx` (separate repo) for rendering military symbology.

## Code style

Eclipse formatter/import-order profile enforced via Spotless (`config/eclipse-formatter.xml`, `config/eclipse.importorder`), bound to the `verify` phase - `mvn verify` runs `spotless:check` and will fail on drift. `-Xlint:all -Werror` is on for the compiler. Source encoding is `UTF-8` throughout (property and compiler plugin both agree - the compiler plugin used to say `ISO-8859-1`, a stray copy-paste from jmsfx, but foxglove has no non-ASCII source content so it never actually mattered).

Enum constants are **not** named `UPPER_SNAKE_CASE` by convention across this author's projects - the view is that an enum constant is closer to an anonymous inner class instance than a `static final` constant, so names may be mixed-case. Enums can carry real behavior rather than being plain data. Don't suggest renaming toward conventional constant style or simplifying toward plain data as a "cleanup."

## Testing

An optional `conformance` Maven profile (`mvn -Pconformance test`) fetches the W3C SVG 1.1 test suite and runs it against a checked-in baseline manifest - kept out of the default build/CI since it needs network access and a display (`xvfb-run` in CI). The default `mvn test` run covers 777 tests and does not require this profile.

Reference images for that comparison are **not** the suite's own `png/` directory - those were rendered by whatever renderer the W3C used circa 2011 and no longer represent how an actively-maintained renderer draws (a 1px stroke centred on an integer coordinate antialiases across two 50%-opacity pixel columns in both this renderer's and live Chrome's output, but the suite's own reference PNG snaps it to one crisp opaque column instead). Generation is a separate, deliberately-triggered step from the comparison itself - three pieces, one per issue:

- `mvn -Pconformance test -Dtest=W3cSvgReferenceGenerator` renders every test through headless Chromium (Playwright) and writes fresh PNGs to `target/w3c-svg-references-generated/` (#196).
- `.github/workflows/generate-conformance-references.yml`, `workflow_dispatch`-only, runs that and publishes the result as a GitHub Actions artifact (`w3c-svg-references`) rather than a Release asset - these are still renders of the W3C suite's own copyrighted documents, and an Actions artifact needs a signed-in GitHub session to fetch and expires automatically, unlike a Release asset's stable public URL (#197). It expires; re-running this workflow on demand is the deliberate way to pick it back up, not an oversight to fix.
- `mvn -Pconformance test -Dtest=W3cSvgConformanceCheck` (the actual comparison) reads from `target/w3c-svg-references-generated/` by default (`conformance.reference.dir` system property to override) and fails fast, naming both of the above commands, if that directory doesn't exist - it does not silently fall back to the stale `png/` (#198).

To re-baseline `src/test/resources/conformance/manifest.properties` after a real rendering change (or after regenerating the reference set itself): dispatch `.github/workflows/conformance-pages.yml` with `mode: record`, download the `conformance-manifest` artifact it produces, review the diff, and commit it by hand - never automatic, the same deliberate-regeneration spirit as the reference images themselves (#199).
