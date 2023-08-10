package net.minecraft.server;

import com.google.common.base.Charsets;
import java.io.IOException;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class SessionLockManager implements AutoCloseable
{
    private final FileChannel field_232994_a_;
    private final FileLock field_232995_b_;
    private static final ByteBuffer field_232996_c_;

    public static SessionLockManager func_232998_a_(Path p_232998_0_) throws IOException
    {
        Path path = p_232998_0_.resolve("session.lock");

        if (!Files.isDirectory(p_232998_0_))
        {
            Files.createDirectories(p_232998_0_);
        }

        FileChannel filechannel = FileChannel.open(path, StandardOpenOption.CREATE, StandardOpenOption.WRITE);

        try
        {
            filechannel.write(field_232996_c_.duplicate());
            filechannel.force(true);
            FileLock filelock = filechannel.tryLock();

            if (filelock == null)
            {
                throw SessionLockManager.AlreadyLockedException.func_233000_a_(path);
            }
            else
            {
                return new SessionLockManager(filechannel, filelock);
            }
        }
        catch (IOException ioexception1)
        {
            try
            {
                filechannel.close();
            }
            catch (IOException ioexception)
            {
                ioexception1.addSuppressed(ioexception);
            }

            throw ioexception1;
        }
    }

    private SessionLockManager(FileChannel p_i231437_1_, FileLock p_i231437_2_)
    {
        this.field_232994_a_ = p_i231437_1_;
        this.field_232995_b_ = p_i231437_2_;
    }

    public void close() throws IOException
    {
        try
        {
            if (this.field_232995_b_.isValid())
            {
                this.field_232995_b_.release();
            }
        }
        finally
        {
            if (this.field_232994_a_.isOpen())
            {
                this.field_232994_a_.close();
            }
        }
    }

    public boolean func_232997_a_()
    {
        return this.field_232995_b_.isValid();
    }

    public static boolean func_232999_b_(Path p_232999_0_) throws IOException
    {
        Path path = p_232999_0_.resolve("session.lock");

        try (
                FileChannel filechannel = FileChannel.open(path, StandardOpenOption.WRITE);
                FileLock filelock = filechannel.tryLock();
            )
        {
            return filelock == null;
        }
        catch (AccessDeniedException accessdeniedexception)
        {
            return true;
        }
        catch (NoSuchFileException nosuchfileexception)
        {
            return false;
        }
    }

    static
    {
        byte[] abyte = "\u2603".getBytes(Charsets.UTF_8);
        field_232996_c_ = ByteBuffer.allocateDirect(abyte.length);
        field_232996_c_.put(abyte);
        ((Buffer)field_232996_c_).flip();
    }

    public static class AlreadyLockedException extends IOException
    {
        private AlreadyLockedException(Path p_i231438_1_, String p_i231438_2_)
        {
            super(p_i231438_1_.toAbsolutePath() + ": " + p_i231438_2_);
        }

        public static SessionLockManager.AlreadyLockedException func_233000_a_(Path p_233000_0_)
        {
            return new SessionLockManager.AlreadyLockedException(p_233000_0_, "already locked (possibly by other Minecraft instance?)");
        }
    }
}
