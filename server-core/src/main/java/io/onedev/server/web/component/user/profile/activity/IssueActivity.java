package io.cheeta.server.web.component.user.profile.activity;

import java.util.Date;

import io.cheeta.server.model.Issue;
import io.cheeta.server.security.SecurityUtils;

public abstract class IssueActivity extends UserActivity {

    public IssueActivity(Date date) {
        super(date);
    }

    public abstract Issue getIssue();

    public boolean isAccessible() {
        return SecurityUtils.canAccessProject(getIssue().getProject());
    }

    @Override
    public Type getType() {
        return Type.ISSUE;
    }

}