package com.github.bannmann.whisperjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import lombok.NonNull;
import lombok.experimental.UtilityClass;

@UtilityClass
public class WhisperJson
{
    /**
     * Parses the given string.
     *
     * @param raw the string to parse
     *
     * @return an ExposedJson instance
     *
     * @throws JsonSyntaxException if there is a JSON syntax error
     * @throws NullPointerException if {@code raw} is {@code null}
     */
    public static ExposedJson parse(@NonNull String raw)
    {
        return parse(new Factory.Exposed(), new Overlay.Exposed(raw));
    }

    /**
     * Parses the given char array. The returned instance is backed by the given array. When the instance is closed, the
     * backing array is wiped.
     *
     * @param raw the characters to parse
     *
     * @return a SafeJson instance backed by the given array
     *
     * @throws JsonSyntaxException if there is a JSON syntax error
     * @throws NullPointerException if {@code raw} is {@code null}
     */
    public static SafeJson parse(@NonNull char[] raw)
    {
        // We don't close the overlay or factory as the SafeJson will close them
        Overlay.Safe overlay = new Overlay.Safe(raw);

        return parse(createSafeFactory(), overlay);
    }

    @SuppressWarnings("java:S2095")
    private static Factory.Safe createSafeFactory()
    {
        // We don't close the factory as the SafeJson will close it
        return new Factory.Safe();
    }

    /**
     * Parses the contents of the given reader. The reader is consumed completely, but not closed.<br>
     * <br>
     * To avoid accidentally exposing sensitive data, make sure that the given reader is not buffered in any way.
     *
     * @param reader the reader to consume and parse
     *
     * @return a SafeJson instance
     *
     * @throws JsonSyntaxException if there is a JSON syntax error
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code reader} is {@code null}
     */
    public static SafeJson parse(@NonNull Reader reader) throws IOException
    {
        try (TextBuilder textBuilder = new TextBuilder(250))
        {
            // We don't close the text, overlay or factory as the SafeJson will close them
            Text.Safe text = textBuilder.appendAll(reader)
                .build(Text.Safe::new);
            Overlay.Safe overlay = new Overlay.Safe(text);

            return parse(createSafeFactory(), overlay);
        }
    }

    /**
     * Parses the contents of the given input stream. The stream is consumed completely, but not closed.<br>
     * <br>
     * To avoid accidentally exposing sensitive data, make sure that the given stream is not buffered in any way.
     *
     * @param inputStream the stream to consume and parse
     * @param charset the charset to use
     *
     * @return a SafeJson instance
     *
     * @throws JsonSyntaxException if there is a JSON syntax error
     * @throws IOException if an I/O error occurs
     * @throws NullPointerException if {@code inputStream} or {@code charset} is {@code null}
     */
    public static SafeJson parse(@NonNull InputStream inputStream, @NonNull Charset charset) throws IOException
    {
        return parse(new InputStreamReader(inputStream, charset));
    }

    private <J extends Json<J>, F extends Factory<J, O, F, T>, O extends Overlay<T>, T extends Text<T>> J parse(
        F factory, O overlay)
    {
        new Parser<>(overlay).execute();

        return overlay.getType(0)
            .create(overlay, 0, factory);
    }
}
