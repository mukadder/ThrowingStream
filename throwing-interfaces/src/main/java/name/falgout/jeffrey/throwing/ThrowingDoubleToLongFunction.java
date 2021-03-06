package name.falgout.jeffrey.throwing;

import java.util.function.Consumer;
import java.util.function.DoubleToLongFunction;
import java.util.function.Function;

import javax.annotation.Nullable;

@FunctionalInterface
public interface ThrowingDoubleToLongFunction<X extends Throwable> {
  public long applyAsLong(double value) throws X;

  default public DoubleToLongFunction fallbackTo(DoubleToLongFunction fallback) {
    return fallbackTo(fallback, null);
  }

  default public DoubleToLongFunction fallbackTo(DoubleToLongFunction fallback,
      @Nullable Consumer<? super Throwable> thrown) {
    ThrowingDoubleToLongFunction<Nothing> t = fallback::applyAsLong;
    return orTry(t, thrown)::applyAsLong;
  }

  default public <Y extends Throwable> ThrowingDoubleToLongFunction<Y>
      orTry(ThrowingDoubleToLongFunction<? extends Y> f) {
    return orTry(f, null);
  }

  default public <Y extends Throwable> ThrowingDoubleToLongFunction<Y> orTry(
      ThrowingDoubleToLongFunction<? extends Y> f, @Nullable Consumer<? super Throwable> thrown) {
    return t -> {
      ThrowingSupplier<Long, X> s = () -> applyAsLong(t);
      return s.orTry(() -> f.applyAsLong(t), thrown).get();
    };
  }

  default public <Y extends Throwable> ThrowingDoubleToLongFunction<Y> rethrow(Class<X> x,
      Function<? super X, ? extends Y> mapper) {
    return t -> {
      ThrowingSupplier<Long, X> s = () -> applyAsLong(t);
      return s.rethrow(x, mapper).get();
    };
  }
}
