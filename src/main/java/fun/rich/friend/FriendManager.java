package fun.rich.friend;

import com.google.common.collect.Lists;
import lombok.Getter;

import java.util.List;

@Getter
public class FriendManager {

    private final List<Friend> friends;

    public FriendManager() {
        friends = Lists.newLinkedList();
    }

    public void addFriend(Friend friend) {
        friends.add(friend);
    }

    public void addFriend(String name) {
        addFriend(new Friend(name));
    }

    public boolean isFriend(String friend) {
        return friends.stream()
                .anyMatch(fr -> fr.getName().equals(friend));
    }

    public void removeFriend(String name) {
        friends.removeIf(friend -> friend.getName().equalsIgnoreCase(name));
    }

    public Friend getFriend(String name) {
        return friends.stream()
                .filter(friend -> friend.getName().equalsIgnoreCase(name))
                .findFirst()
                .orElse(null);
    }
}
