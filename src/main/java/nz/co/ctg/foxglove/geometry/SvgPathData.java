package nz.co.ctg.foxglove.geometry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javafx.geometry.Point2D;

/**
 * Parses an SVG {@code <path d="...">} attribute into a flattened polyline, for arc-length purposes (#29) rather
 * than rendering - {@code <path>} itself still hands its {@code d} straight to {@code javafx.scene.shape.SVGPath},
 * which draws it perfectly well without any of this.
 * <p>
 * Curves are flattened by evaluating their parametric form at a fixed number of steps rather than computing an
 * analytic arc length - Bezier arc length has no closed form anyway, so this is the standard approach. Only the
 * first subpath is used: parsing stops at a second {@code M}/{@code m} or once a {@code Z}/{@code z} closes the
 * first, since text-on-path against multi-subpath data is not a case this needs to handle. Compact arc-flag
 * concatenation (two single-digit flags with no separator, e.g. {@code "A1 1 0 0111 2"}) is a known SVG quirk this
 * parser does not attempt - rare in practice, and flagged here rather than silently mishandled.
 */
public final class SvgPathData {

    private static final Pattern TOKEN = Pattern.compile("[MmLlHhVvCcSsQqTtAaZz]|[-+]?(?:\\d+\\.\\d*|\\.\\d+|\\d+)(?:[eE][-+]?\\d+)?");
    private static final String COMMAND_LETTERS = "MmLlHhVvCcSsQqTtAaZz";
    private static final int CURVE_STEPS = 32;
    private static final int ARC_STEPS = 32;

    private SvgPathData() {
    }

    public static List<Point2D> flatten(String d) {
        List<Point2D> points = new ArrayList<>();
        if (d == null || d.isBlank()) {
            return points;
        }
        List<String> tokens = tokenize(d);
        int i = 0;
        double curX = 0;
        double curY = 0;
        double subpathStartX = 0;
        double subpathStartY = 0;
        double prevControlX = 0;
        double prevControlY = 0;
        char prevCommand = 0;
        char command = 0;
        boolean subpathStarted = false;

        while (i < tokens.size()) {
            if (isCommand(tokens.get(i))) {
                command = tokens.get(i).charAt(0);
                i++;
            }
            switch (Character.toUpperCase(command)) {
                case 'M': {
                    if (subpathStarted) {
                        return points;
                    }
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x += curX;
                        y += curY;
                    }
                    curX = x;
                    curY = y;
                    subpathStartX = x;
                    subpathStartY = y;
                    points.add(new Point2D(x, y));
                    subpathStarted = true;
                    // per the specification, further coordinate pairs after the first are implicit linetos
                    command = Character.isLowerCase(command) ? 'l' : 'L';
                    break;
                }
                case 'L': {
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x += curX;
                        y += curY;
                    }
                    points.add(new Point2D(x, y));
                    curX = x;
                    curY = y;
                    break;
                }
                case 'H': {
                    double x = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x += curX;
                    }
                    points.add(new Point2D(x, curY));
                    curX = x;
                    break;
                }
                case 'V': {
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        y += curY;
                    }
                    points.add(new Point2D(curX, y));
                    curY = y;
                    break;
                }
                case 'C': {
                    double x1 = parseDouble(tokens.get(i++));
                    double y1 = parseDouble(tokens.get(i++));
                    double x2 = parseDouble(tokens.get(i++));
                    double y2 = parseDouble(tokens.get(i++));
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x1 += curX;
                        y1 += curY;
                        x2 += curX;
                        y2 += curY;
                        x += curX;
                        y += curY;
                    }
                    flattenCubic(points, curX, curY, x1, y1, x2, y2, x, y);
                    prevControlX = x2;
                    prevControlY = y2;
                    curX = x;
                    curY = y;
                    break;
                }
                case 'S': {
                    double x2 = parseDouble(tokens.get(i++));
                    double y2 = parseDouble(tokens.get(i++));
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x2 += curX;
                        y2 += curY;
                        x += curX;
                        y += curY;
                    }
                    double x1 = isCubicCommand(prevCommand) ? 2 * curX - prevControlX : curX;
                    double y1 = isCubicCommand(prevCommand) ? 2 * curY - prevControlY : curY;
                    flattenCubic(points, curX, curY, x1, y1, x2, y2, x, y);
                    prevControlX = x2;
                    prevControlY = y2;
                    curX = x;
                    curY = y;
                    break;
                }
                case 'Q': {
                    double x1 = parseDouble(tokens.get(i++));
                    double y1 = parseDouble(tokens.get(i++));
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x1 += curX;
                        y1 += curY;
                        x += curX;
                        y += curY;
                    }
                    flattenQuadratic(points, curX, curY, x1, y1, x, y);
                    prevControlX = x1;
                    prevControlY = y1;
                    curX = x;
                    curY = y;
                    break;
                }
                case 'T': {
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x += curX;
                        y += curY;
                    }
                    double x1 = isQuadraticCommand(prevCommand) ? 2 * curX - prevControlX : curX;
                    double y1 = isQuadraticCommand(prevCommand) ? 2 * curY - prevControlY : curY;
                    flattenQuadratic(points, curX, curY, x1, y1, x, y);
                    prevControlX = x1;
                    prevControlY = y1;
                    curX = x;
                    curY = y;
                    break;
                }
                case 'A': {
                    double rx = parseDouble(tokens.get(i++));
                    double ry = parseDouble(tokens.get(i++));
                    double rotation = parseDouble(tokens.get(i++));
                    boolean largeArc = parseDouble(tokens.get(i++)) != 0;
                    boolean sweep = parseDouble(tokens.get(i++)) != 0;
                    double x = parseDouble(tokens.get(i++));
                    double y = parseDouble(tokens.get(i++));
                    if (Character.isLowerCase(command)) {
                        x += curX;
                        y += curY;
                    }
                    flattenArc(points, curX, curY, rx, ry, rotation, largeArc, sweep, x, y);
                    curX = x;
                    curY = y;
                    break;
                }
                case 'Z': {
                    points.add(new Point2D(subpathStartX, subpathStartY));
                    return points;
                }
                default:
                    return points;
            }
            prevCommand = command;
        }
        return points;
    }

    private static void flattenCubic(List<Point2D> points, double x0, double y0, double x1, double y1, double x2, double y2, double x3, double y3) {
        for (int step = 1; step <= CURVE_STEPS; step++) {
            double t = (double) step / CURVE_STEPS;
            double mt = 1 - t;
            double x = mt * mt * mt * x0 + 3 * mt * mt * t * x1 + 3 * mt * t * t * x2 + t * t * t * x3;
            double y = mt * mt * mt * y0 + 3 * mt * mt * t * y1 + 3 * mt * t * t * y2 + t * t * t * y3;
            points.add(new Point2D(x, y));
        }
    }

    private static void flattenQuadratic(List<Point2D> points, double x0, double y0, double x1, double y1, double x2, double y2) {
        for (int step = 1; step <= CURVE_STEPS; step++) {
            double t = (double) step / CURVE_STEPS;
            double mt = 1 - t;
            double x = mt * mt * x0 + 2 * mt * t * x1 + t * t * x2;
            double y = mt * mt * y0 + 2 * mt * t * y1 + t * t * y2;
            points.add(new Point2D(x, y));
        }
    }

    /**
     * The standard SVG endpoint-to-center arc parameterization (specification appendix F.6.5), sampled by angle
     * rather than converted to a Bezier approximation first.
     */
    private static void flattenArc(List<Point2D> points, double x0, double y0, double rx, double ry, double rotationDegrees,
        boolean largeArc, boolean sweep, double x, double y) {
        if (rx == 0 || ry == 0) {
            points.add(new Point2D(x, y));
            return;
        }
        rx = Math.abs(rx);
        ry = Math.abs(ry);
        double phi = Math.toRadians(rotationDegrees);
        double cosPhi = Math.cos(phi);
        double sinPhi = Math.sin(phi);

        double dx2 = (x0 - x) / 2.0;
        double dy2 = (y0 - y) / 2.0;
        double x1p = cosPhi * dx2 + sinPhi * dy2;
        double y1p = -sinPhi * dx2 + cosPhi * dy2;

        double lambda = (x1p * x1p) / (rx * rx) + (y1p * y1p) / (ry * ry);
        if (lambda > 1) {
            double scale = Math.sqrt(lambda);
            rx *= scale;
            ry *= scale;
        }

        double sign = largeArc != sweep ? 1 : -1;
        double num = rx * rx * ry * ry - rx * rx * y1p * y1p - ry * ry * x1p * x1p;
        double den = rx * rx * y1p * y1p + ry * ry * x1p * x1p;
        double co = den == 0 ? 0 : sign * Math.sqrt(Math.max(0, num / den));
        double cxp = co * (rx * y1p / ry);
        double cyp = co * (-(ry * x1p / rx));

        double cx = cosPhi * cxp - sinPhi * cyp + (x0 + x) / 2.0;
        double cy = sinPhi * cxp + cosPhi * cyp + (y0 + y) / 2.0;

        double theta1 = vectorAngle(1, 0, (x1p - cxp) / rx, (y1p - cyp) / ry);
        double dTheta = vectorAngle((x1p - cxp) / rx, (y1p - cyp) / ry, (-x1p - cxp) / rx, (-y1p - cyp) / ry);
        if (!sweep && dTheta > 0) {
            dTheta -= 2 * Math.PI;
        } else if (sweep && dTheta < 0) {
            dTheta += 2 * Math.PI;
        }

        for (int step = 1; step <= ARC_STEPS; step++) {
            double theta = theta1 + dTheta * step / ARC_STEPS;
            double ex = cx + rx * cosPhi * Math.cos(theta) - ry * sinPhi * Math.sin(theta);
            double ey = cy + rx * sinPhi * Math.cos(theta) + ry * cosPhi * Math.sin(theta);
            points.add(new Point2D(ex, ey));
        }
    }

    private static double vectorAngle(double ux, double uy, double vx, double vy) {
        double sign = ux * vy - uy * vx < 0 ? -1 : 1;
        double dot = ux * vx + uy * vy;
        double length = Math.sqrt((ux * ux + uy * uy) * (vx * vx + vy * vy));
        double cos = Math.clamp(length == 0 ? 1 : dot / length, -1.0, 1.0);
        return sign * Math.acos(cos);
    }

    private static boolean isCommand(String token) {
        return token.length() == 1 && COMMAND_LETTERS.indexOf(token.charAt(0)) >= 0;
    }

    private static boolean isCubicCommand(char command) {
        return command == 'C' || command == 'c' || command == 'S' || command == 's';
    }

    private static boolean isQuadraticCommand(char command) {
        return command == 'Q' || command == 'q' || command == 'T' || command == 't';
    }

    private static double parseDouble(String token) {
        return Double.parseDouble(token);
    }

    private static List<String> tokenize(String d) {
        List<String> tokens = new ArrayList<>();
        Matcher matcher = TOKEN.matcher(d);
        while (matcher.find()) {
            tokens.add(matcher.group());
        }
        return tokens;
    }

}
