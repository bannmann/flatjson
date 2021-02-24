package com.github.bannmann.whisperjson;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Arrays;
import java.util.function.Consumer;

import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestSensitiveText
{
    private char[] inputCharacters;

    @BeforeMethod
    public void setUp()
    {
        inputCharacters = new char[]{ 's', '5', '6', 'q', 'v', 'Q', 'U', 'P', 'v', 'U', 'v', 'Q', '7' };
    }

    @AfterMethod
    public void tearDown()
    {
        Arrays.fill(inputCharacters, (char) 0);
    }

    @Test
    public void testBasics()
    {
        try (SensitiveText text = new SensitiveText(inputCharacters))
        {
            assertThat(text).returns(false, SensitiveText::isEmpty)
                .returns(true, SensitiveText::hasValue)
                .extracting(SensitiveText::length)
                .isEqualTo(inputCharacters.length);
        }
    }

    @Test
    public void testDependentArray()
    {
        try (SensitiveText text = new SensitiveText(inputCharacters))
        {
            assertThat(text.newDependentArray()).isEqualTo(inputCharacters);
        }
    }

    @Test
    public void testImmutability()
    {
        char[] originalInput = Arrays.copyOf(inputCharacters, inputCharacters.length);
        try (SensitiveText text = new SensitiveText(inputCharacters))
        {
            assertThat(text.newDependentArray()).isEqualTo(originalInput);

            inputCharacters[0] = '#';

            assertThat(text.newDependentArray()).isEqualTo(originalInput);
        }
    }

    @Test
    public void testCloseWipesContents()
    {
        SensitiveText text = new SensitiveText(inputCharacters);

        // Closing the SensitiveText will null its array reference, so we get a copy now that we can check later
        char[] internalArray = text.contents;

        text.close();

        assertThat(isWiped(internalArray)).isTrue();
    }

    @Test
    public void testCloseWipesDependentArrays()
    {
        char[] array1;
        char[] array2;

        try (SensitiveText text = new SensitiveText(inputCharacters))
        {
            array1 = text.newDependentArray();
            array2 = text.newDependentArray();
        }

        assertThat(isWiped(array1)).overridingErrorMessage("dependent array 1 was not wiped")
            .isTrue();
        assertThat(isWiped(array2)).overridingErrorMessage("dependent array 2 was not wiped")
            .isTrue();
    }

    @Test(dataProvider = "methodsFailingAfterClose")
    public void testIllegalState(String label, Consumer<SensitiveText> method)
    {
        SensitiveText text = new SensitiveText(inputCharacters);
        text.close();

        assertThatThrownBy(() -> method.accept(text)).isInstanceOf(IllegalStateException.class);
    }

    @DataProvider
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Object[][] methodsFailingAfterClose()
    {
        return new Object[][]{
            methodCall("newDependentArray", SensitiveText::newDependentArray),
            methodCall("length", SensitiveText::length),
            methodCall("isEmpty", SensitiveText::isEmpty),
            methodCall("hasValue", SensitiveText::hasValue)
        };
    }

    @Test(dataProvider = "methodsWorkingAfterClose")
    public void testAfterClose(String label, Consumer<SensitiveText> method)
    {
        SensitiveText text = new SensitiveText(inputCharacters);
        text.close();

        assertThatCode(() -> method.accept(text)).doesNotThrowAnyException();
    }

    @DataProvider
    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static Object[][] methodsWorkingAfterClose()
    {
        return new Object[][]{
            methodCall("toString", SensitiveText::toString), methodCall("close", SensitiveText::close)
        };
    }

    public static Object[] methodCall(String label, Consumer<SensitiveText> consumer)
    {
        return new Object[]{ label, consumer };
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
