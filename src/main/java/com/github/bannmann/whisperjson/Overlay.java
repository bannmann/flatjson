package com.github.bannmann.whisperjson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.NonNull;

import com.github.mizool.core.exception.CodeInconsistencyException;
import com.google.common.annotations.VisibleForTesting;

class Overlay<T extends Text<T>>
{
    public static final class Exposed extends Overlay<Text.Exposed>
    {
        public Exposed(@NonNull String raw)
        {
            super(new Text.Exposed(raw));
        }
    }

    public static final class Safe extends Overlay<Text.Safe> implements AutoCloseable
    {
        public Safe(@NonNull char[] raw)
        {
            super(new Text.Safe(raw));
        }

        public Safe(@NonNull Text.Safe text)
        {
            super(text);
        }

        @Override
        public void close()
        {
            text.close();

            for (int[] block : blocks)
            {
                Arrays.fill(block, 0);
            }
        }
    }

    @VisibleForTesting
    static int calculateBlockSize(int rawChars)
    {
        // make block size (in bytes) roughly equal to input size
        // (min block size is 64 B, max block size is 16 KB)
        return 4 * Math.min(Math.max(rawChars / 16, 4), 1024);
    }

    private static final int TYPE = 0;
    private static final int FROM = 1;
    private static final int TO = 2;
    private static final int CHILD_COUNT = 3;

    @Getter
    protected final T text;
    protected final List<int[]> blocks = new ArrayList<>();
    protected final int blockSize;

    private Overlay(@NonNull T text)
    {
        this.text = text;
        this.blockSize = calculateBlockSize(text.length());
    }

    public Type getType(int element)
    {
        return Type.values()[getComponent(element, TYPE)];
    }

    public int getChildCount(int element)
    {
        return getComponent(element, CHILD_COUNT);
    }

    public T getJson(int element)
    {
        return text.getPart(getComponent(element, FROM), getComponent(element, TO) + 1);
    }

    public int getOffset(int element)
    {
        return getComponent(element, FROM);
    }

    public T getUnescapedText(int element)
    {
        T value = text.getPart(getComponent(element, FROM) + 1, getComponent(element, TO));
        if (getType(element) == Type.STRING_ESCAPED)
        {
            try
            {
                return value.unescape();
            }
            catch (JsonSyntaxException e)
            {
                // Should have occurred during initial parsing
                throw new CodeInconsistencyException(e);
            }
        }
        return value;
    }

    private int getComponent(int element, int offset)
    {
        return getBlock(element)[getBlockIndex(element) + offset];
    }

    private int[] getBlock(int element)
    {
        return blocks.get((element * 4) / blockSize);
    }

    public void createLeafElement(int element, Type type, int from, int to)
    {
        createElement(element, type, from, to, 0);
    }

    private void createElement(int element, Type type, int from, int to, int childCount)
    {
        int currentBlock = (element * 4) / blockSize;
        if (currentBlock == blocks.size())
        {
            blocks.add(new int[blockSize]);
        }
        int[] block = blocks.get(currentBlock);
        int index = getBlockIndex(element);
        block[index] = type.ordinal();
        block[index + FROM] = from;
        block[index + TO] = to;
        block[index + CHILD_COUNT] = childCount;
    }

    private int getBlockIndex(int element)
    {
        return (element * 4) % blockSize;
    }

    public void openStructureElement(int nextElementNumber, Type type, int pos)
    {
        createElement(nextElementNumber, type, pos, -1, -1);
    }

    public void closeStructureElement(int element, int to, int childCount)
    {
        int[] block = getBlock(element);
        int index = getBlockIndex(element);
        block[index + TO] = to;
        block[index + CHILD_COUNT] = childCount;
    }
}
