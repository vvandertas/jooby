package io.jooby;

import javax.annotation.Nonnull;

public interface Route {

  interface Chain {
    void next(@Nonnull Context ctx) throws Throwable;
  }

  interface Filter {
    void handle(@Nonnull Context ctx, @Nonnull Chain chain) throws Throwable;
  }

  interface Before extends Filter {
    default void handle(@Nonnull Context ctx, @Nonnull Chain chain) throws Throwable {
      handle(ctx);
      chain.next(ctx);
    }

    void handle(@Nonnull Context ctx) throws Throwable;
  }

  interface After extends Filter {
    default void handle(@Nonnull Context ctx, @Nonnull Chain chain) throws Throwable {
      ctx.after(this);
      chain.next(ctx);
    }

    default @Nonnull After then(@Nonnull After next) {
      return ctx -> {
        handle(ctx);
        next.handle(ctx);
      };
    }

    void handle(@Nonnull Context ctx) throws Throwable;
  }

  interface Handler extends Filter {
    default void handle(@Nonnull Context ctx, @Nonnull Chain chain) throws Throwable {
      Object result = handle(ctx);
      if (result != ctx && !ctx.committed()) {
        ctx.send(result.toString());
      }
      chain.next(ctx);
    }

    Object handle(@Nonnull Context ctx) throws Throwable;
  }

  @Nonnull String method();

  @Nonnull String pattern();

  @Nonnull Filter handler();

  static @Nonnull String normalize(@Nonnull String pattern) {
    if (pattern.equals("*")) {
      return "/**";
    }
    StringBuilder buff = new StringBuilder();
    char prev = '/';
    buff.append(prev);
    for (int i = 0; i < pattern.length(); i++) {
      char ch = pattern.charAt(i);
      if (ch != '/' || prev != '/') {
        buff.append(ch);
      }
      prev = ch;
    }
    int last = buff.length() - 1;
    if (last > 1 && buff.charAt(last) == '/') {
      buff.setLength(last);
    }
    if (buff.length() == pattern.length()) {
      return pattern;
    }
    return buff.toString();
  }
}