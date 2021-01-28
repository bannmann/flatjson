package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.google.common.io.CharStreams;

public class SafeJsonTest
{
    private static final String TEST_JSON = "SafeJsonTest.json";
    private static final char[] EXPECTED_USERNAME = { 'a', 'l', 'i', 'c', 'e' };
    private static final char[] EXPECTED_PASSWORD = { 's', '5', '6', 'q', 'v', 'Q', 'U', 'P', 'v', 'U', 'v', 'Q', '7' };

    private char[] inputCharacters;

    @BeforeMethod
    public void setUp() throws IOException
    {
        inputCharacters = readTestData();

        assertThat(inputCharacters.length > 0).isTrue();
    }

    private char[] readTestData() throws IOException
    {
        try (InputStream inputStream = getClass().getResourceAsStream(TEST_JSON);
             InputStreamReader reader = new InputStreamReader(inputStream))
        {
            StringBuilder stringBuilder = new StringBuilder();
            CharStreams.copy(reader, stringBuilder);

            char[] chars = StringBuilders.copyToCharArray(stringBuilder);
            stringBuilder.setLength(0);

            return chars;
        }
    }

    @Test
    public void dataAccess()
    {
        try (SafeJson root = WhisperJson.parse(inputCharacters);
             SafeJson username = root.asObject()
                 .get("username");
             SafeJson password = root.asObject()
                 .get("password"))
        {
            assertThat(username.asCharArray()).isEqualTo(EXPECTED_USERNAME);
            assertThat(password.asCharArray()).isEqualTo(EXPECTED_PASSWORD);
        }
    }

    @Test
    public void rootInputWiped()
    {
        try (SafeJson root = WhisperJson.parse(inputCharacters);
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
        try (SafeJson root = WhisperJson.parse(inputCharacters);
             SafeJson password = root.asObject()
                 .get("password"))
        {
            // Simulate application code retrieving the text contents before closing the SafeJson instance
            char[] successfulRead = password.asCharArray();
            password.close();

            assertThat(successfulRead).isEqualTo(EXPECTED_PASSWORD);

            // Determine whether the internal array was wiped by retrieving a copy of it
            char[] failedRead = password.asCharArray();
            assertThat(isWiped(failedRead)).describedAs("non-wiped array after closing")
                .isTrue();
            assertThat(failedRead.length).isEqualTo(EXPECTED_PASSWORD.length);
        }

        if (!isInputWiped())
        {
            fail("Input was not wiped:\n" + new String(inputCharacters));
        }
    }

    @Test
    public void rootInputNotWipedPrematurely()
    {
        try (SafeJson root = WhisperJson.parse(inputCharacters))
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