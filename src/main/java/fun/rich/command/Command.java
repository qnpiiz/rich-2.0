package fun.rich.command;

@FunctionalInterface
public interface Command {
    void execute(String... strings);
}
