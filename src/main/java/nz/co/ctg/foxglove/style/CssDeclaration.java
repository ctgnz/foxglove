package nz.co.ctg.foxglove.style;

/**
 * One {@code property: value} pair from a declaration block - either the inline {@code style} attribute or a
 * stylesheet rule's body - with whether it carried a trailing {@code !important}.
 */
public record CssDeclaration(String property, String value, boolean important) {

}
