import functions.Function;
import org.junit.jupiter.api.Test;
import parsing.Parser;
import tools.singlevariable.Extrema;
import tools.singlevariable.Solver;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class SolverTest {

    @Test
    void simplePolynomial() {
        Function test = Parser.parse("x^2+-1");
        assertEquals(1, Solver.getSolutionPoint(test, 3));
    }

    @Test
    void moreAdvancedPolynomial() {
        Function test = Parser.parse("x^4-5x^2+4");
        assertArrayEquals(new double[]{-2, -1, 1, 2}, Solver.getSolutionsRange(test, -10, 10));
    }

    @Test
    void polynomialWithNoSolution() {
        Function test = Parser.parse("x^2+1");
        assertEquals(Double.NaN, Solver.getSolutionPoint(test, 4));
    }

    @Test
    void polynomialWithNoSolutionRange() {
        Function test = Parser.parse("x^2+1");
        assertArrayEquals(new double[]{}, Solver.getSolutionsRange(test, -10, 10));
    }

    @Test
    void simpleNotPolynomial1() {
        Function test = Parser.parse("ln(x)");
        assertArrayEquals(new double[]{1}, Solver.getSolutionsRange(test, -10, 10));
    }

     @Test
    void simpleNotPolynomial2() {
        Function test = Parser.parse("e^(x-5) - 1");
         assertArrayEquals(new double[]{5}, Solver.getSolutionsRange(test, 0, 7.68785));
    }

    @Test
    void simpleTrigZero() {
        Function test = Parser.parse("sin(x-3)");
        assertEquals(3, Solver.getSolutionPoint(test, 3.5));
    }

    @Test
    void simpleConstant() {
        Function test = Parser.parse("2");
        assertEquals(Double.NaN, Solver.getSolutionPoint(test, 23));
    }

    @Test
    void simpleExponent() {
        Function test = Parser.parse("(x+1)^2");
        assertEquals(-1, Solver.getSolutionPoint(test, 23));
    }

}
