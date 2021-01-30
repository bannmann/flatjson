package com.github.bannmann.whisperjson;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

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
     */
    public static ExposedJson parse(String raw)
    {
        return new Factory.Exposed().create(new Overlay.Exposed(raw), 0);
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
     */
    public static SafeJson parse(char[] raw)
    {
        // We don't close the overlay or factory as the SafeJson will close them
        Overlay.Safe overlay = new Overlay.Safe(raw);

        return createSafeFactory().create(overlay, 0);
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
     */
    public static SafeJson parse(Reader reader) throws IOException
    {
        try (TextBuilder textBuilder = new TextBuilder(250))
        {
            // We don't close the text, overlay or factory as the SafeJson will close them
            Text.Safe text = textBuilder.appendAll(reader)
                .build(Text.Safe::new);
            Overlay.Safe overlay = new Overlay.Safe(text);

            return createSafeFactory().create(overlay, 0);
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
     */
    public static SafeJson parse(InputStream inputStream, Charset charset) throws IOException
    {
        return parse(new InputStreamReader(inputStream, charset));
    }
}
