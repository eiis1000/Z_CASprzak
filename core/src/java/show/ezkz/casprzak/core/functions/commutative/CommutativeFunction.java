package show.ezkz.casprzak.core.functions.commutative;

import show.ezkz.casprzak.core.config.Settings;
import show.ezkz.casprzak.core.config.SimplificationSettings;
import show.ezkz.casprzak.core.functions.GeneralFunction;
import show.ezkz.casprzak.core.functions.Outputable;
import show.ezkz.casprzak.core.functions.endpoint.Constant;
import org.jetbrains.annotations.NotNull;
import show.ezkz.casprzak.core.output.OutputCommutative;
import show.ezkz.casprzak.core.output.OutputFunction;
import show.ezkz.casprzak.core.tools.ArrayTools;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * The abstract {@link CommutativeFunction} class represents function that are commutative. Ex: {@code addition} or {@code multiplication}
 */
public abstract class CommutativeFunction extends GeneralFunction {

	/**
	 * The array of {@link GeneralFunction}s operated on by the {@link CommutativeFunction}
	 */
	protected final GeneralFunction[] functions;

	/**
	 * Constructs a new {@link CommutativeFunction}
	 * @param functions The {@link GeneralFunction}s that will be acted on
	 */
	public CommutativeFunction(GeneralFunction... functions) {
		this.functions = functions;
		Arrays.sort(this.functions);
	}

	/**
	 * Returns {@link #functions}
	 * @return {@link #functions}
	 */
	public GeneralFunction[] getFunctions() {
		return functions;
	}

	/**
	 * Returns an instance of this {@link GeneralFunction}
	 * @param functions the {@link GeneralFunction}s that will be acted on
	 * @return an instance of this {@link GeneralFunction}
	 */
	public abstract CommutativeFunction getInstance(GeneralFunction... functions);

	public String toString() {
		return Arrays.stream(functions)
				.map(GeneralFunction::toString)
				.collect(getJoiningCollector());
	}

	/**
	 * Returns a collector used to create the {@code toString} of the {@link CommutativeFunction}
	 * @return a joining collector
	 */
	protected Collector<CharSequence, ?, String> getJoiningCollector() {
		return Collectors.joining(", ", this.getClass().getSimpleName().toLowerCase() + "(", ")");
	}

	public boolean equalsFunction(GeneralFunction that) {
		if (that instanceof CommutativeFunction function && this.getClass().equals(that.getClass()))
			return ArrayTools.equalsSimplified(functions, function.getFunctions());
		else
			return false;
	}

	public int compareSelf(GeneralFunction that) {
		if (that instanceof CommutativeFunction function) {
			if (functions.length != function.getFunctions().length)
				return functions.length - function.getFunctions().length;
			GeneralFunction[] thisFunctions = functions;
			GeneralFunction[] thatFunctions = function.getFunctions();
			for (int i = 0; i < thisFunctions.length; i++)
				if (!thisFunctions[i].equalsFunction(thatFunctions[i]))
					return thisFunctions[i].compareTo(thatFunctions[i]);
			throw new IllegalStateException("Called compareSelf on two equal functions: " + this + ", " + that + "");
		} else
			throw new IllegalCallerException("Illegally called CommutativeFunction.compareSelf on a non-CommutativeFunction.");
	}


	public OutputFunction toOutputFunction() {
		return new OutputCommutative(
				getClass().getSimpleName().toLowerCase(),
				Arrays.stream(functions)
						.map(Outputable::toOutputFunction)
						.collect(Collectors.toList())
		);
	}

	public GeneralFunction substituteAll(Predicate<? super GeneralFunction> test, Function<? super GeneralFunction, ? extends GeneralFunction> replacer) {
		if (test.test(this))
			return replacer.apply(this);

		GeneralFunction[] newFunctions = new GeneralFunction[functions.length];
		for (int i = 0; i < functions.length; i++)
			newFunctions[i] = functions[i].substituteAll(test, replacer);
		return getInstance(newFunctions);
	}

	public GeneralFunction simplify(SimplificationSettings settings) {
		return this.simplifyInternal(settings).simplifyTrivialElement(settings);
	}

	/**
	 * Simplifies this {@link CommutativeFunction} using all methods that are guaranteed to return a {@link CommutativeFunction} of the same type
	 * @return the simplified {@link CommutativeFunction}
	 * @param settings the {@link SimplificationSettings} object describing the parameters of simplification
	 */
	public CommutativeFunction simplifyInternal(SimplificationSettings settings) {
		CommutativeFunction current = this;
		current = current.simplifyElements(settings);
		current = current.simplifyPull(settings);
		current = current.simplifyIdentity(settings);
		if (settings.simplifyFunctionsOfConstants)
			current = current.simplifyConstants(settings);
		return current;
	}

	/**
	 * Simplifies each element of this {@link CommutativeFunction}
	 * @return a new {@link CommutativeFunction} with each element of {@link #functions} simplified
	 * @param settings the {@link SimplificationSettings} object describing the parameters of simplification
	 */
	public CommutativeFunction simplifyElements(SimplificationSettings settings) {
		GeneralFunction[] simplifiedFunctions = new GeneralFunction[functions.length];
		for (int i = 0; i < functions.length; i++)
			simplifiedFunctions[i] = functions[i].simplify(settings);
		return getInstance(simplifiedFunctions);
	}

	/**
	 * Removes all instances of the identity of the function from {@link #functions}
	 * @return a new {@link CommutativeFunction} with
	 * @param settings the {@link SimplificationSettings} object describing the parameters of simplification
	 */
	public CommutativeFunction simplifyIdentity(SimplificationSettings settings) {
		List<GeneralFunction> toPut = new ArrayList<>(Arrays.asList(functions));
		toPut.removeIf(generalFunction -> generalFunction instanceof Constant constant && constant.constant == getIdentityValue());
		return getInstance(toPut.toArray(new GeneralFunction[0]));
	}

	/**
	 * Combines all {@link Constant}s using {@link #operate(double, double)}
	 * @return a new {@link CommutativeFunction} with the combined {@link Constant}s
	 * @param settings the {@link SimplificationSettings} object describing the parameters of simplification
	 */
	public CommutativeFunction simplifyConstants(SimplificationSettings settings) {
		if (hasMultipleConstants()) {
			double accumulator = getIdentityValue();
			List<GeneralFunction> functionList = new LinkedList<>(List.of(functions));
			ListIterator<GeneralFunction> iter = functionList.listIterator();
			while (iter.hasNext()) {
				if (iter.next() instanceof Constant constant) {
					if (settings.simplifyFunctionsOfSpecialConstants || !constant.isSpecial()) {
						accumulator = operate(accumulator, constant.constant);
						iter.remove();
					}
				}
			}
			functionList.add(new Constant(accumulator));
			return getInstance(functionList.toArray(new GeneralFunction[0])).simplifyIdentity(settings);
		} else {
			return this;
		}
	}

	private boolean hasMultipleConstants() {
		boolean flag = false;
		for (GeneralFunction function : functions)
			if (function instanceof Constant)
				if (flag)
					return true;
				else
					flag = true;
		return false;
	}


	/**
	 * Composes all sub-functions of the same type. Ex: {@code (a+(b+c))} becomes {@code (a+b+c)}
	 * @return a new {@link CommutativeFunction} with all compositions performed
	 * @param settings the {@link SimplificationSettings} object describing the parameters of simplification
	 */
	public CommutativeFunction simplifyPull(SimplificationSettings settings) {
		for (int i = 0; i < functions.length; i++)
			if (this.getClass().equals(functions[i].getClass()))
				return getInstance(pullUp(functions, ((CommutativeFunction) functions[i]).getFunctions(), i)).simplifyPull(settings);

		return this;
	}

	private static GeneralFunction[] pullUp(GeneralFunction[] outer, GeneralFunction[] inner, int indexInOuter) {
		GeneralFunction[] newArray = new GeneralFunction[outer.length + inner.length - 1];
		if (indexInOuter > 0)
			System.arraycopy(outer, 0, newArray, 0, indexInOuter);
		if (indexInOuter < outer.length - 1)
			System.arraycopy(outer, indexInOuter + 1, newArray, indexInOuter, outer.length - indexInOuter - 1);
		System.arraycopy(inner, 0, newArray, outer.length - 1, inner.length);
		return newArray;
	}

	/**
	 * Returns identity {@link Constant} if {@link #functions} length is 0 and returns the {@link GeneralFunction} if {@link #functions} length is 1
	 * @return identity {@link Constant} if {@link #functions} length is 0 and returns the {@link GeneralFunction} if {@link #functions} length is 1
	 * @param settings the {@link SimplificationSettings} object describing the parameters of simplification
	 */
	public GeneralFunction simplifyTrivialElement(SimplificationSettings settings) {
		if (functions.length == 0)
			return new Constant(getIdentityValue());
		else if (functions.length == 1)
			return functions[0].simplify(settings);
		else
			return this;
	}

	/**
	 * Returns the identity of this {@link CommutativeFunction}, such as 0 for + or gcd and 1 for * or lcm
	 * @return the identity of this {@link CommutativeFunction}
	 */
	public abstract double getIdentityValue();

	/**
	 * Performs the operation of this {@link CommutativeFunction} on the two inputs
	 * @param a the first input
	 * @param b the second input
	 * @return the operation applied to the inputs
	 */
	public abstract double operate(double a, double b);

	public int hashCode() {
		int code = this.getClass().hashCode();
		for (GeneralFunction f : functions)
			code = code * 31 + f.hashCode();
		return code;
	}

	public @NotNull Iterator<GeneralFunction> iterator() {
		return new CommutativeIterator();
	}

	private class CommutativeIterator implements Iterator<GeneralFunction> {
		private int loc;

		private CommutativeIterator() {
			loc = 0;
		}

		@Override
		public boolean hasNext() {
			return loc < functions.length;
		}

		@SuppressWarnings("ValueOfIncrementOrDecrementUsed")
		@Override
		public GeneralFunction next() {
			if (!hasNext())
				throw new NoSuchElementException("Out of elements in CommutativeFunction " + Arrays.toString(functions) + "");
			return functions[loc++];
		}
	}
}
