package io.cheeta.server.web.component.markdown;

import java.util.List;

import io.cheeta.server.model.User;

public interface UserMentionSupport {

	List<User> findUsers(String query, int count);

}
