package fun.rich.config;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public abstract class Manager<T> {

    private List<T> contents = Lists.newLinkedList();
}
