package net.optifine.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LineBuffer implements Iterable<String>
{
    private ArrayList<String> lines = new ArrayList<>();

    public int size()
    {
        return this.lines.size();
    }

    public String get(int index)
    {
        return this.lines.get(index);
    }

    public void add(String line)
    {
        this.checkLine(line);
        this.lines.add(line);
    }

    public void add(String[] ls)
    {
        for (int i = 0; i < ls.length; ++i)
        {
            String s = ls[i];
            this.add(s);
        }
    }

    public void insert(int index, String line)
    {
        this.checkLine(line);
        this.lines.add(index, line);
    }

    public void insert(int index, String[] ls)
    {
        for (int i = 0; i < ls.length; ++i)
        {
            String s = ls[i];
            this.checkLine(s);
        }

        this.lines.addAll(index, Arrays.asList(ls));
    }

    private void checkLine(String line)
    {
        if (line == null)
        {
            throw new IllegalArgumentException("Line is null");
        }
        else if (line.indexOf(10) >= 0)
        {
            throw new IllegalArgumentException("Line contains LF");
        }
        else if (line.indexOf(13) >= 0)
        {
            throw new IllegalArgumentException("Line contains CR");
        }
    }

    public int indexMatch(Pattern regexp)
    {
        for (int i = 0; i < this.lines.size(); ++i)
        {
            String s = this.lines.get(i);
            Matcher matcher = regexp.matcher(s);

            if (matcher.matches())
            {
                return i;
            }
        }

        return -1;
    }

    public static LineBuffer readAll(Reader reader) throws IOException
    {
        LineBuffer linebuffer = new LineBuffer();
        BufferedReader bufferedreader = new BufferedReader(reader);

        while (true)
        {
            String s = bufferedreader.readLine();

            if (s == null)
            {
                bufferedreader.close();
                return linebuffer;
            }

            linebuffer.add(s);
        }
    }

    public String[] getLines()
    {
        return this.lines.toArray(new String[this.lines.size()]);
    }

    public Iterator<String> iterator()
    {
        return new LineBuffer.Itr();
    }

    public String toString()
    {
        StringBuilder stringbuilder = new StringBuilder();

        for (int i = 0; i < this.lines.size(); ++i)
        {
            String s = this.lines.get(i);
            stringbuilder.append(s);
            stringbuilder.append("\n");
        }

        return stringbuilder.toString();
    }

    public class Itr implements Iterator<String>
    {
        private int position;

        public boolean hasNext()
        {
            return this.position < LineBuffer.this.lines.size();
        }

        public String next()
        {
            String s = LineBuffer.this.lines.get(this.position);
            ++this.position;
            return s;
        }
    }
}
