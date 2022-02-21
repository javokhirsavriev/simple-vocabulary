package uz.javokhirdev.svocabulary.swipecards

import kotlin.math.sqrt

/*************************************************************************
 * Compilation:  javac LinearRegression.java
 * Execution:    java  LinearRegression
 *
 * Compute least squares solution to y = beta * x + alpha.
 * Simple linear regression.
 *
 * The <tt>LinearRegression</tt> class performs a simple linear regression
 * on an set of *N* data points (*y<sub>i</sub>*, *x<sub>i</sub>*).
 * That is, it fits a straight line *y* =  +  *x*,
 * (where *y* is the response variable, *x* is the predictor variable,
 *  is the *y-intercept*, and  is the *slope*)
 * that minimizes the sum of squared residuals of the linear regression model.
 * It also computes associated statistics, including the coefficient of
 * determination *R*<sup>2</sup> and the standard deviation of the
 * estimates for the slope and *y*-intercept.
 *
 * @author Robert Sedgewick
 * @author Kevin Wayne
 *
 * Performs a linear regression on the data points <tt>(y[], x[])</tt>.
 * @param x the values of the predictor variable
 * @param y the corresponding values of the response variable
 */
internal class LinearRegression(x: FloatArray, y: FloatArray) {

    private val N: Int
    private val alpha: Double
    private val beta: Double
    private val R2: Double
    private val svar: Double
    private val svar0: Double
    private val svar1: Double

    /**
     * Returns the *y*-intercept  of the best of the best-fit line *y* =  +  *x*.
     * @return the *y*-intercept  of the best-fit line *y =  +  x*
     */
    fun intercept(): Double = alpha

    /**
     * Returns the slope  of the best of the best-fit line *y* =  +  *x*.
     * @return the slope  of the best-fit line *y* =  +  *x*
     */
    fun slope(): Double = beta

    /**
     * Returns the coefficient of determination *R*<sup>2</sup>.
     * @return the coefficient of determination *R*<sup>2</sup>, which is a real number between 0 and 1
     */
    private fun rSup(): Double = R2

    /**
     * Returns the standard error of the estimate for the intercept.
     * @return the standard error of the estimate for the intercept
     */
    fun interceptStdErr(): Double = sqrt(svar0)

    /**
     * Returns the standard error of the estimate for the slope.
     * @return the standard error of the estimate for the slope
     */
    fun slopeStdErr(): Double = sqrt(svar1)

    /**
     * Returns the expected response <tt>y</tt> given the value of the predictor
     * variable <tt>x</tt>.
     * @param x the value of the predictor variable
     * @return the expected response <tt>y</tt> given the value of the predictor
     * variable <tt>x</tt>
     */
    fun predict(x: Double): Double = beta * x + alpha

    /**
     * Returns a string representation of the simple linear regression model.
     * @return a string representation of the simple linear regression model,
     * including the best-fit line and the coefficient of determination *R*<sup>2</sup>
     */
    override fun toString(): String {
        var s = ""
        s += String.format("%.2f N + %.2f", slope(), intercept())
        return s + "  (R^2 = " + String.format("%.3f", rSup()) + ")"
    }

    init {
        require(x.size == y.size) { "Array lengths are not equal" }
        N = x.size

        // first pass
        var sumx = 0.0
        var sumy = 0.0
        var sumx2 = 0.0

        for (i in 0 until N) sumx += x[i]
        for (i in 0 until N) sumx2 += (x[i] * x[i]).toDouble()
        for (i in 0 until N) sumy += y[i]

        val xbar = sumx / N
        val ybar = sumy / N

        // second pass: compute summary statistics
        var xxbar = 0.0
        var yybar = 0.0
        var xybar = 0.0

        for (i in 0 until N) {
            xxbar += (x[i] - xbar) * (x[i] - xbar)
            yybar += (y[i] - ybar) * (y[i] - ybar)
            xybar += (x[i] - xbar) * (y[i] - ybar)
        }

        beta = xybar / xxbar
        alpha = ybar - beta * xbar

        // more statistical analysis
        var rss = 0.0 // residual sum of squares
        var ssr = 0.0 // regression sum of squares

        for (i in 0 until N) {
            val fit = beta * x[i] + alpha
            rss += (fit - y[i]) * (fit - y[i])
            ssr += (fit - ybar) * (fit - ybar)
        }

        val degreesOfFreedom = N - 2

        R2 = ssr / yybar
        svar = rss / degreesOfFreedom
        svar1 = svar / xxbar
        svar0 = svar / N + xbar * xbar * svar1
    }
}