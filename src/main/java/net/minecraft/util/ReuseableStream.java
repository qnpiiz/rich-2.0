package net.minecraft.util;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators.AbstractSpliterator;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class ReuseableStream<T>
{
    private final List<T> cachedValues = Lists.newArrayList();
    private final Spliterator<T> spliterator;

    public ReuseableStream(Stream<T> stream)
    {
        this.spliterator = stream.spliterator();
    }

    public Stream<T> createStream()
    {
        return StreamSupport.stream(new AbstractSpliterator<T>(Long.MAX_VALUE, 0)
        {
            private int nextIdx;
            public boolean tryAdvance(Consumer <? super T > p_tryAdvance_1_)
            {
                while (true)
                {
                    if (this.nextIdx >= ReuseableStream.this.cachedValues.size())
                    {
                        if (ReuseableStream.this.spliterator.tryAdvance(ReuseableStream.this.cachedValues::add))
                        {
                            continue;
                        }

                        return false;
                    }

                    p_tryAdvance_1_.accept(ReuseableStream.this.cachedValues.get(this.nextIdx++));
                    return true;
                }
            }
        }, false);
    }
}
