package org.febit.wit.io;

import lombok.Singular;
import lombok.experimental.UtilityClass;
import org.febit.wit.io.loader.AdvancePathLoaderDecorator;
import org.febit.wit.io.loader.ClasspathLoader;
import org.febit.wit.io.loader.DebouncedLoaderDecorator;
import org.febit.wit.io.loader.DispatcherLoader;
import org.febit.wit.io.loader.EmptyLoader;
import org.febit.wit.io.loader.FileSystemLoader;
import org.febit.wit.io.loader.PathBasedLoader;
import org.febit.wit.io.loader.SecurityLoaderDecorator;
import org.febit.wit.io.loader.StringLoader;
import org.jspecify.annotations.Nullable;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

@UtilityClass
public class Loaders {

    public static EmptyLoader empty() {
        return new EmptyLoader();
    }

    public static DispatcherLoader.Builder dispatcher() {
        return DispatcherLoader.builder();
    }

    public static StringLoader.Builder string() {
        return StringLoader.builder();
    }

    public static SecurityLoaderDecorator security(Loader delegate, List<String> allows) {
        return SecurityLoaderDecorator.of(delegate, allows);
    }

    public static DebouncedLoaderDecorator debounce(Loader delegate, long delayMillis) {
        return DebouncedLoaderDecorator.of(delegate, delayMillis);
    }

    public static DebouncedLoaderDecorator debounce(Loader delegate, Duration delay) {
        return DebouncedLoaderDecorator.of(delegate, (int) delay.toMillis());
    }

    public static AdvancePathLoaderDecorator.Builder advance(PathBasedLoader delegate) {
        return AdvancePathLoaderDecorator.builder()
                .delegate(delegate);
    }

    @lombok.Builder(
            builderClassName = "ClasspathBuilder",
            builderMethodName = "classpath"
    )
    private static Loader classpath0(
            @Nullable Charset charset,
            Source.@Nullable BeginWith beginWith,
            @Nullable Boolean cacheEnabled,

            @Nullable String root,
            @Nullable String missingSuffix,
            @Singular List<String> deputySuffixes
    ) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (beginWith == null) {
            beginWith = Source.BeginWith.SCRIPT;
        }

        var delegate = ClasspathLoader.of(charset, beginWith);
        var advance = advance(delegate)
                .root(root)
                .missingSuffix(missingSuffix)
                .deputySuffixes(deputySuffixes);
        if (cacheEnabled != null) {
            advance.cacheEnabled(cacheEnabled);
        }
        return advance.build();
    }

    @lombok.Builder(
            builderClassName = "FileSystemBuilder",
            builderMethodName = "fileSystem"
    )
    private static Loader fileSystem0(
            @Nullable Charset charset,
            Source.@Nullable BeginWith beginWith,
            @Nullable Boolean cacheEnabled,
            @Nullable String root,
            @Nullable String missingSuffix,
            @Singular List<String> deputySuffixes
    ) {
        if (charset == null) {
            charset = StandardCharsets.UTF_8;
        }
        if (beginWith == null) {
            beginWith = Source.BeginWith.SCRIPT;
        }
        var delegate = FileSystemLoader.of(charset, beginWith);
        var advance = advance(delegate)
                .root(root)
                .missingSuffix(missingSuffix)
                .deputySuffixes(deputySuffixes);
        if (cacheEnabled != null) {
            advance.cacheEnabled(cacheEnabled);
        }
        return advance.build();
    }

}
