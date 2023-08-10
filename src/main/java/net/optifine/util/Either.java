package net.optifine.util;

import java.util.Optional;

public class Either<L, R>
{
    private Optional<L> left;
    private Optional<R> right;

    private Either(Optional<L> leftIn, Optional<R> rightIn)
    {
        this.left = leftIn;
        this.right = rightIn;

        if (!this.left.isPresent() && !this.right.isPresent())
        {
            throw new IllegalArgumentException("Both left and right are not present");
        }
        else if (this.left.isPresent() && this.right.isPresent())
        {
            throw new IllegalArgumentException("Both left and right are present");
        }
    }

    public Optional<L> getLeft()
    {
        return this.left;
    }

    public Optional<R> getRight()
    {
        return this.right;
    }

    public static <L, R> Either<L, R> makeLeft(L value)
    {
        return new Either<>(Optional.of(value), Optional.empty());
    }

    public static <L, R> Either makeRight(R value)
    {
        return new Either<>(Optional.empty(), Optional.of(value));
    }
}
