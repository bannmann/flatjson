package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.CharStreams;

public class TestSafeJson
{
    private static final String TEST_JSON = "SafeJsonTest.json";
    private static final char[] EXPECTED_USERNAME = { 'a', 'l', 'i', 'c', 'e' };
    private static final char[] EXPECTED_PASSWORD = { 's', '5', '6', 'q', 'v', 'Q', 'U', 'P', 'v', 'U', 'v', 'Q', '7' };

    private char[] inputCharacters;
    private WhisperJson whisperJson;

    @BeforeMethod
    public void setUp() throws IOException
    {
        whisperJson = new WhisperJson();
        inputCharacters = getTestDataArray();

        assertThat(inputCharacters.length > 0).isTrue();
    }

    private char[] getTestDataArray() throws IOException
    {
        try (InputStreamReader reader = getTestDataReader())
        {
            StringBuilder stringBuilder = new StringBuilder();
            CharStreams.copy(reader, stringBuilder);

            char[] chars = StringBuilders.copyToCharArray(stringBuilder);
            stringBuilder.setLength(0);

            return chars;
        }
    }

    private InputStreamReader getTestDataReader()
    {
        return new InputStreamReader(getTestDataInputStream());
    }

    private InputStream getTestDataInputStream()
    {
        return getClass().getResourceAsStream(TEST_JSON);
    }

    @Test
    public void dataAccess()
    {
        try (SafeJson root = whisperJson.parse(inputCharacters);
             SafeJson username = root.asObject()
                 .get("username");
             SafeJson password = root.asObject()
                 .get("password"))
        {
            assertThat(username.asCharArray()).isEqualTo(EXPECTED_USERNAME);
            assertThat(password.asCharArray()).isEqualTo(EXPECTED_PASSWORD);

            assertContentsViaSensitiveText(username, EXPECTED_USERNAME);
            assertContentsViaSensitiveText(password, EXPECTED_PASSWORD);
        }
    }

    private void assertContentsViaSensitiveText(SafeJson json, char[] chars)
    {
        try (SensitiveText text = json.asSensitiveText())
        {
            assertThat(text.newDependentArray()).isEqualTo(chars);
        }
    }

    @Test
    public void consumeReader() throws IOException
    {
        try (SafeJson fromArray = whisperJson.parse(inputCharacters);
             SafeJson fromReader = whisperJson.parse(getTestDataReader()))
        {
            assertThat(fromReader).isEqualTo(fromArray);
        }
    }

    @Test
    public void consumeStream() throws IOException
    {
        try (SafeJson fromArray = whisperJson.parse(inputCharacters);
             SafeJson fromInputStream = whisperJson.parse(getTestDataInputStream(), StandardCharsets.UTF_8))
        {
            assertThat(fromInputStream).isEqualTo(fromArray);
        }
    }

    @Test
    public void rootInputWiped()
    {
        try (SafeJson root = whisperJson.parse(inputCharacters);
             SafeJson password = root.asObject()
                 .get("password"))
        {
            assertThat(password.asCharArray()).isEqualTo(EXPECTED_PASSWORD);
        }

        if (!isInputWiped())
        {
            fail("Input was not wiped:\n" + new String(inputCharacters));
        }
    }

    @Test
    public void copiedTextWiped()
    {
        try (SafeJson root = whisperJson.parse(inputCharacters);
             SafeJson passwordElement = root.asObject()
                 .get("password"))
        {
            // Simulate application code retrieving the text contents before closing the SafeJson instance
            char[] successfulRead = passwordElement.asCharArray();

            // Closing the password element will null its array reference, so we get a copy now that we can check later
            char[] internalArray = ((Strng.Safe) passwordElement).text.contents;

            passwordElement.close();

            assertThat(successfulRead).isEqualTo(EXPECTED_PASSWORD);
            assertThat(isWiped(internalArray)).overridingErrorMessage("non-wiped array after closing")
                .isTrue();
            assertThatThrownBy(passwordElement::asCharArray).isInstanceOf(IllegalStateException.class);
        }

        if (!isInputWiped())
        {
            fail("Input was not wiped:\n" + new String(inputCharacters));
        }
    }

    @Test
    public void transitiveClose()
    {
        SafeJson root = whisperJson.parse(inputCharacters);
        SafeJson passwordElement = root.asObject()
            .get("password");
        root.close();

        assertThatThrownBy(passwordElement::asCharArray).isInstanceOf(IllegalStateException.class);
    }

    @Test
    public void rootInputNotWipedPrematurely()
    {
        try (SafeJson root = whisperJson.parse(inputCharacters))
        {
            root.asObject()
                .get("password")
                .close();
            assertInputNotWiped();

            root.asObject()
                .get("extraObject")
                .asObject()
                .get("numbersArray")
                .close();
            assertInputNotWiped();

            root.asObject()
                .get("extraObject")
                .close();
            assertInputNotWiped();
        }
    }

    private void assertInputNotWiped()
    {
        if (isInputWiped())
        {
            fail("Input was wiped prematurely");
        }
    }

    private boolean isInputWiped()
    {
        return isWiped(inputCharacters);
    }

    private boolean isWiped(char[] inputCharacters)
    {
        for (char c : inputCharacters)
        {
            if (c == 0)
            {
                return true;
            }
        }
        return false;
    }
}
